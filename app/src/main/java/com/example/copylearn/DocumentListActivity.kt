package com.example.copylearn

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import Controller.DocumentController
import Entity.Document
import kotlinx.coroutines.launch

/**
 * Activity para listar documentos
 */
class DocumentListActivity : AppCompatActivity() {

    private lateinit var controller: DocumentController
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var searchView: SearchView
    private lateinit var adapter: DocumentAdapter

    private var currentDocs: List<Document> = listOf()
    private var lastQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_list)

        // CAMBIO: Ahora el controller recibe Context
        controller = DocumentController(this)

        recyclerView = findViewById(R.id.rvDocuments)
        emptyView = findViewById(R.id.txtEmpty)
        searchView = findViewById(R.id.svSearch)

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DocumentAdapter(
            documents = listOf(),
            onItemClick = { doc ->
                startActivity(
                    Intent(this, DocumentActivity::class.java).putExtra("doc_id", doc.ID)
                )
            },
            onDeleteClick = { doc ->
                confirmDelete(doc)
            }
        )
        recyclerView.adapter = adapter

        refreshList("")

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                lastQuery = query ?: ""
                refreshList(lastQuery)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                lastQuery = newText ?: ""
                refreshList(lastQuery)
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        refreshList(lastQuery)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_document_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuNew -> {
                startActivity(Intent(this, DocumentActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun confirmDelete(doc: Document) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dlg_delete_title))
            .setMessage("${getString(R.string.dlg_delete_msg)}\n\n\"${doc.Title}\"")
            .setPositiveButton(getString(R.string.dlg_yes)) { _, _ ->
                deleteDocument(doc)
            }
            .setNegativeButton(getString(R.string.dlg_no), null)
            .show()
    }

    private fun deleteDocument(doc: Document) {
        // CAMBIO: Usar lifecycleScope para llamada asíncrona
        lifecycleScope.launch {
            val success = controller.Delete(doc.ID)
            if (success) {
                Toast.makeText(this@DocumentListActivity, getString(R.string.msg_deleted), Toast.LENGTH_SHORT).show()
                refreshList(lastQuery)
            } else {
                Toast.makeText(this@DocumentListActivity, controller.ErrorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refreshList(query: String) {
        // CAMBIO: Usar lifecycleScope para llamada asíncrona
        lifecycleScope.launch {
            currentDocs = if (query.isBlank()) {
                controller.GetAll()
            } else {
                controller.Search(query)
            }

            if (currentDocs.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }

            adapter.updateDocuments(currentDocs)
        }
    }
}