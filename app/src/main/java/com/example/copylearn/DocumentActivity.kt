package com.example.copylearn

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import Controller.DocumentController
import Entity.Document
import Entity.Language
import Util.ImageFileUtil
import Util.OcrUtilMlKit
import java.time.LocalDate
import kotlinx.coroutines.launch

/**
 * Activity para crear/editar documentos
 */
class DocumentActivity : AppCompatActivity() {

    private lateinit var controller: DocumentController
    private var selectedDate: LocalDate = LocalDate.now()
    private var currentId: String? = null
    private var captureUri: Uri? = null

    // UI refs
    private lateinit var edtTitle: EditText
    private lateinit var txtDate: TextView
    private lateinit var btnPickDate: Button
    private lateinit var btnSelectImage: Button
    private lateinit var txtImageStatus: TextView
    private lateinit var edtRecognized: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    // Camera launcher
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            captureUri?.let { uri ->
                onImageSelected(uri)
                Toast.makeText(this, getString(R.string.msg_image_captured), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.msg_capture_canceled), Toast.LENGTH_SHORT).show()
            captureUri = null
        }
    }

    // Gallery launcher
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            captureUri = it
            onImageSelected(it)
        }
    }

    // Camera permission launcher
    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)

        // CAMBIO: Ahora el controller recibe Context
        controller = DocumentController(this)

        edtTitle = findViewById(R.id.edtTitle)
        txtDate = findViewById(R.id.txtDate)
        btnPickDate = findViewById(R.id.btnPickDate)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        txtImageStatus = findViewById(R.id.txtImageStatus)
        edtRecognized = findViewById(R.id.edtRecognizedText)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

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

        btnSelectImage.setOnClickListener {
            showImageSourceDialog()
        }

        btnSave.setOnClickListener { attemptSave() }
        btnCancel.setOnClickListener { finish() }

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
            R.id.menuDelete -> { attemptDelete(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showImageSourceDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dlg_image_picker_title))
            .setItems(arrayOf(
                getString(R.string.dlg_image_camera),
                getString(R.string.dlg_image_gallery)
            )) { _, which ->
                when (which) {
                    0 -> launchCamera()
                    1 -> launchGallery()
                }
            }
            .show()
    }

    private fun launchCamera() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                AlertDialog.Builder(this)
                    .setTitle("Camera Permission Required")
                    .setMessage("This app needs camera access to take photos of documents.")
                    .setPositiveButton("OK") { _, _ ->
                        requestCameraPermission.launch(Manifest.permission.CAMERA)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            else -> {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        try {
            val targetUri: Uri = ImageFileUtil.createImageUri(this)
            captureUri = targetUri
            takePicture.launch(targetUri)
        } catch (e: Exception) {
            Toast.makeText(this, "Error opening camera: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun launchGallery() {
        pickImage.launch("image/*")
    }

    private fun onImageSelected(uri: Uri) {
        captureUri = uri
        txtImageStatus.text = "Image: ${uri.lastPathSegment ?: uri.toString()}"
        txtImageStatus.visibility = View.VISIBLE

        // Ejecutar OCR automáticamente
        runOcr()
    }

    private fun runOcr() {
        val uri = captureUri
        if (uri == null) {
            Toast.makeText(this, getString(R.string.msg_no_image), Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, getString(R.string.msg_ocr_processing), Toast.LENGTH_SHORT).show()

        OcrUtilMlKit.extractText(
            context = this,
            imageUri = uri,
            onResult = { result ->
                edtRecognized.setText(result.text)
                Toast.makeText(this, getString(R.string.msg_ocr_success), Toast.LENGTH_SHORT).show()
            },
            onError = { exception ->
                Toast.makeText(this, "${getString(R.string.msg_ocr_failed)}: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

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
                // CAMBIO: Usar lifecycleScope para llamada asíncrona
                lifecycleScope.launch {
                    val ok = controller.Delete(id)
                    if (ok) {
                        Toast.makeText(this@DocumentActivity, getString(R.string.msg_deleted), Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@DocumentActivity, controller.ErrorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton(getString(R.string.dlg_no), null)
            .show()
    }

    private fun commitSave() {
        val doc = Document().apply {
            if (!currentId.isNullOrBlank()) ID = currentId!!
            Title = edtTitle.text.toString()
            CaptureDate = selectedDate
            ImageUri = captureUri?.toString() ?: ""
            RecognizedText = edtRecognized.text.toString()
            Language = Language("en", "English") // Default
            OcrConfidence = 0.95 // Default confidence
        }

        // CAMBIO: Usar lifecycleScope para llamada asíncrona
        lifecycleScope.launch {
            val ok = controller.Save(doc)
            if (ok) {
                Toast.makeText(this@DocumentActivity, getString(R.string.msg_saved), Toast.LENGTH_SHORT).show()
                if (currentId.isNullOrBlank()) clearForm() else finish()
            } else {
                Toast.makeText(this@DocumentActivity, controller.ErrorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearForm() {
        edtTitle.text?.clear()
        edtRecognized.text?.clear()
        selectedDate = LocalDate.now()
        txtDate.text = selectedDate.toString()
        captureUri = null
        txtImageStatus.text = ""
        txtImageStatus.visibility = View.GONE
        edtTitle.requestFocus()
        currentId = null
    }

    private fun loadDocument(id: String) {
        // CAMBIO: Usar lifecycleScope para llamada asíncrona
        lifecycleScope.launch {
            val doc = controller.GetById(id)
            if (doc == null) {
                Toast.makeText(this@DocumentActivity, getString(R.string.err_doc_not_found), Toast.LENGTH_SHORT).show()
                return@launch
            }

            edtTitle.setText(doc.Title)
            selectedDate = doc.CaptureDate
            txtDate.text = selectedDate.toString()

            if (doc.ImageUri.isNotBlank()) {
                captureUri = Uri.parse(doc.ImageUri)
                txtImageStatus.text = "Image: ${captureUri?.lastPathSegment ?: "loaded"}"
                txtImageStatus.visibility = View.VISIBLE
            }

            edtRecognized.setText(doc.RecognizedText)
            Toast.makeText(this@DocumentActivity, getString(R.string.msg_loaded), Toast.LENGTH_SHORT).show()
        }
    }
}