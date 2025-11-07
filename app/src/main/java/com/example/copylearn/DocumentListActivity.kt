package com.example.copylearn

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import Controller.DocumentController
import Data.MemoryDataManager
import Entity.Document

class DocumentListActivity : AppCompatActivity() {

    private lateinit var controller: DocumentController
    private lateinit var listView: ListView
    private lateinit var emptyView: TextView
    private lateinit var searchView: SearchView

    private lateinit var adapter: ArrayAdapter<String>
    private var currentDocs: MutableList<Document> = mutableListOf()
    private var lastQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_list)

        controller = DocumentController(MemoryDataManager())

        listView = findViewById(R.id.lvDocuments)
        emptyView = findViewById(R.id.txtEmpty)
        searchView = findViewById(R.id.svSearch)
        listView.emptyView = emptyView

        // <<< FIX: layout seguro de Android con TextView interno >>>
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

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

        listView.setOnItemClickListener { _, _, position, _ ->
            val doc = currentDocs.getOrNull(position)
            if (doc != null) {
                startActivity(
                    Intent(this, DocumentActivity::class.java).putExtra("doc_id", doc.ID)
                )
            } else {
                Toast.makeText(this, getString(R.string.err_doc_not_found), Toast.LENGTH_SHORT).show()
            }
        }
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
            R.id.menuNew -> { startActivity(Intent(this, DocumentActivity::class.java)); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refreshList(query: String) {
        currentDocs = if (query.isBlank()) controller.GetAll() else controller.Search(query)
        val titles = currentDocs.map { it.Title }
        adapter.clear()
        adapter.addAll(titles)
        adapter.notifyDataSetChanged()
    }
}
