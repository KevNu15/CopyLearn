package Data

import Entity.Document
import Entity.Language

class MemoryDataManager : IDataManager {

    // In-memory storage
    private val documents: MutableList<Document> = mutableListOf()
    private val languages: MutableList<Language> = mutableListOf(
        Language("en", "English"),
        Language("es", "Spanish")
    )

    override fun AddDocument(document: Document): Boolean {
        // Avoid duplicates by ID
        val exists = this.documents.any { it.ID == document.ID && it.ID.isNotEmpty() }
        if (exists) return false
        this.documents.add(document)
        return true
    }

    override fun UpdateDocument(document: Document): Boolean {
        val index = this.documents.indexOfFirst { it.ID == document.ID }
        if (index == -1) return false
        this.documents[index] = document
        return true
    }

    override fun DeleteDocument(id: String): Boolean {
        val index = this.documents.indexOfFirst { it.ID == id }
        if (index == -1) return false
        this.documents.removeAt(index)
        return true
    }

    override fun GetDocumentById(id: String): Document? {
        return this.documents.firstOrNull { it.ID == id }
    }

    override fun GetAllDocuments(): MutableList<Document> {
        return this.documents.toMutableList()
    }

    override fun SearchDocuments(text: String): MutableList<Document> {
        if (text.isBlank()) return GetAllDocuments()
        val q = text.lowercase()
        return this.documents.filter { doc ->
            doc.Title.lowercase().contains(q) ||
                    doc.RecognizedText.lowercase().contains(q)
        }.toMutableList()
    }

    override fun GetLanguages(): MutableList<Language> {
        return this.languages.toMutableList()
    }
}
