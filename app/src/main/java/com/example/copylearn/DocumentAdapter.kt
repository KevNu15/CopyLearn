package com.example.copylearn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import Entity.Document

class DocumentAdapter(
    private var documents: List<Document>,
    private val onItemClick: (Document) -> Unit,
    private val onDeleteClick: (Document) -> Unit
) : RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {

    class DocumentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTitle: TextView = view.findViewById(R.id.txtDocumentTitle)
        val txtDate: TextView = view.findViewById(R.id.txtDocumentDate)
        val txtPreview: TextView = view.findViewById(R.id.txtDocumentPreview)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document, parent, false)
        return DocumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val doc = documents[position]
        holder.txtTitle.text = doc.Title
        holder.txtDate.text = doc.CaptureDate.toString()

        // Preview del texto reconocido (primeras 50 caracteres)
        val preview = if (doc.RecognizedText.length > 50) {
            doc.RecognizedText.take(50) + "..."
        } else {
            doc.RecognizedText.ifBlank { "(No text)" }
        }
        holder.txtPreview.text = preview

        // Click en el item completo para ver/editar
        holder.itemView.setOnClickListener {
            onItemClick(doc)
        }

        // Click en el botón de eliminar
        holder.btnDelete.setOnClickListener {
            onDeleteClick(doc)
        }
    }

    override fun getItemCount(): Int = documents.size

    fun updateDocuments(newDocuments: List<Document>) {
        documents = newDocuments
        notifyDataSetChanged()
    }
}