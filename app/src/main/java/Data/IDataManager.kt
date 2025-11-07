package Data

import Entity.Document
import Entity.Language

interface IDataManager {
    // Document actions (CRUD)
    fun AddDocument(document: Document): Boolean
    fun UpdateDocument(document: Document): Boolean
    fun DeleteDocument(id: String): Boolean

    // Queries
    fun GetDocumentById(id: String): Document?
    fun GetAllDocuments(): MutableList<Document>
    fun SearchDocuments(text: String): MutableList<Document>

    // Master data
    fun GetLanguages(): MutableList<Language>
}
