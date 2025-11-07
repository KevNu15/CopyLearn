package com.example.copylearn

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import Controller.DocumentController
import Data.MemoryDataManager
import Entity.Document
import Entity.Language
import Util.ImageFileUtil
import Util.OcrUtil
import Util.OcrUtilMlKit
import java.time.LocalDate

class DocumentActivity : AppCompatActivity() {

    private lateinit var controller: DocumentController
    private lateinit var languages: MutableList<Language>
    private var selectedDate: LocalDate = LocalDate.now()
    private var currentId: String? = null
    private var captureUri: Uri? = null

    // UI refs
    private lateinit var edtTitle: EditText
    private lateinit var txtDate: TextView
    private lateinit var btnPickDate: Button
    private lateinit var edtImageUri: EditText
    private lateinit var edtRecognized: EditText
    private lateinit var spnLanguage: Spinner
    private lateinit var edtConfidence: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var btnScan: Button

    // Camera launcher
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            captureUri?.let { uri ->
                edtImageUri.setText(uri.toString())
                Toast.makeText(this, getString(R.string.msg_image_captured), Toast.LENGTH_SHORT).show()
                val result = OcrUtil.extractText(this, uri)
                edtRecognized.setText(result.text)
                edtConfidence.setText(result.confidence.toString())
            }
        } else {
            Toast.makeText(this, getString(R.string.msg_capture_canceled), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)

        controller = DocumentController(MemoryDataManager())
        languages = controller.GetLanguages()

        edtTitle = findViewById(R.id.edtTitle)
        txtDate = findViewById(R.id.txtDate)
        btnPickDate = findViewById(R.id.btnPickDate)
        edtImageUri = findViewById(R.id.edtImageUri)
        edtRecognized = findViewById(R.id.edtRecognizedText)
        spnLanguage = findViewById(R.id.spnLanguage)
        edtConfidence = findViewById(R.id.edtConfidence)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        btnScan = findViewById(R.id.btnScan)

        val names = languages.map { "${it.Name} (${it.Code})" }
        spnLanguage.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, names)

        txtDate.text = selectedDate.toString()
        btnPickDate.setOnClickListener {
            val d = selectedDate
            DatePickerDialog(
                this,
                { _, y, m, day ->
                    selectedDate = LocalDate.of(y, m + 1, day)
                    txtDate.text = selectedDate.toString()
                },
                d.year, d.monthValue - 1, d.dayOfMonth
            ).show()
        }

        // Cámara al tocar ImageUri (enviar SIEMPRE Uri no-nulo al launcher)
        edtImageUri.isFocusable = false
        edtImageUri.isClickable = true
        edtImageUri.setOnClickListener {
            val targetUri: Uri = ImageFileUtil.createImageUri(this)
            captureUri = targetUri
            takePicture.launch(targetUri)
        }

        btnSave.setOnClickListener { attemptSave() }
        btnCancel.setOnClickListener { finish() }
        btnScan.setOnClickListener { runOcr() }

        intent?.getStringExtra("doc_id")?.let { id ->
            currentId = id
            loadDocument(id)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_document, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuSave -> { attemptSave(); true }
            R.id.menuCancel -> { finish(); true }
            R.id.menuScan -> { runOcr(); true }
            R.id.menuDelete -> { attemptDelete(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ---- CRUD helpers with dialogs ----
    private fun attemptSave() {
        if (!currentId.isNullOrBlank()) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.dlg_update_title))
                .setMessage(getString(R.string.dlg_update_msg))
                .setPositiveButton(getString(R.string.dlg_yes)) { _, _ -> commitSave() }
                .setNegativeButton(getString(R.string.dlg_no), null)
                .show()
        } else {
            commitSave()
        }
    }

    private fun attemptDelete() {
        val id = currentId
        if (id.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.msg_delete_unavailable), Toast.LENGTH_SHORT).show()
            return
        }
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dlg_delete_title))
            .setMessage(getString(R.string.dlg_delete_msg))
            .setPositiveButton(getString(R.string.dlg_yes)) { _, _ ->
                val ok = controller.Delete(id)
                if (ok) { Toast.makeText(this, getString(R.string.msg_deleted), Toast.LENGTH_SHORT).show(); finish() }
                else     { Toast.makeText(this, getString(R.string.err_doc_not_found), Toast.LENGTH_SHORT).show() }
            }
            .setNegativeButton(getString(R.string.dlg_no), null)
            .show()
    }

    private fun commitSave() {
        val idx = spnLanguage.selectedItemPosition.coerceIn(0, languages.lastIndex)
        val lang = languages[idx]

        val doc = Document().apply {
            if (!currentId.isNullOrBlank()) ID = currentId!!
            Title = edtTitle.text.toString()
            CaptureDate = selectedDate
            ImageUri = edtImageUri.text.toString()
            RecognizedText = edtRecognized.text.toString()
            Language = lang
            OcrConfidence = edtConfidence.text.toString().toDoubleOrNull() ?: 0.0
        }

        val ok = controller.Save(doc)
        if (ok) {
            Toast.makeText(this, getString(R.string.msg_saved), Toast.LENGTH_SHORT).show()
            if (currentId.isNullOrBlank()) clearForm() else finish()
        } else {
            Toast.makeText(this, controller.ErrorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun runOcr() {
        // 1) Construye el Uri (nullable)
        val maybeUri: Uri? = if (!edtImageUri.text.isNullOrBlank()) {
            runCatching { Uri.parse(edtImageUri.text.toString()) }.getOrNull()
        } else {
            captureUri
        }

        // 2) Valida y retorna temprano
        if (maybeUri == null) {
            Toast.makeText(this, getString(R.string.msg_set_or_capture_image), Toast.LENGTH_SHORT).show()
            return
        }

        // 3) A partir de aquí 'uri' es no-nulo
        val uri: Uri = maybeUri

        // 4) Intenta ML Kit (skeleton) y si no está habilitado, usa el stub
        OcrUtilMlKit.extractText(
            context = this,
            imageUri = uri,
            onResult = { res ->
                edtRecognized.setText(res.text)
                edtConfidence.setText(res.confidence.toString())
                Toast.makeText(this, getString(R.string.msg_ocr_mlkit_done), Toast.LENGTH_SHORT).show()
            },
            onError = {
                val res = OcrUtil.extractText(this, uri)
                edtRecognized.setText(res.text)
                edtConfidence.setText(res.confidence.toString())
                Toast.makeText(this, getString(R.string.msg_ocr_stub_used), Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun clearForm() {
        edtTitle.text?.clear()
        edtImageUri.text?.clear()
        edtRecognized.text?.clear()
        edtConfidence.text?.clear()
        selectedDate = LocalDate.now()
        txtDate.text = selectedDate.toString()
        spnLanguage.setSelection(0)
        edtTitle.requestFocus()
        currentId = null
    }

    private fun loadDocument(id: String) {
        val doc = controller.GetById(id) ?: run {
            Toast.makeText(this, getString(R.string.err_doc_not_found), Toast.LENGTH_SHORT).show()
            return
        }
        edtTitle.setText(doc.Title)
        selectedDate = doc.CaptureDate
        txtDate.text = selectedDate.toString()
        edtImageUri.setText(doc.ImageUri)
        edtRecognized.setText(doc.RecognizedText)
        edtConfidence.setText(doc.OcrConfidence.toString())
        val idx = languages.indexOfFirst { it.Code.equals(doc.Language.Code, ignoreCase = true) }
        spnLanguage.setSelection(if (idx >= 0) idx else 0)
        Toast.makeText(this, getString(R.string.msg_loaded), Toast.LENGTH_SHORT).show()
    }
}
