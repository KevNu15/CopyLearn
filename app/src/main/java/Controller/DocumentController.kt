package Controller

import Data.IDataManager
import Entity.Document
import Entity.Language
import Util.IdUtil
import java.time.LocalDate

class DocumentController(private val dataManager: IDataManager) {

    var ErrorMessage: String = ""

    fun Save(document: Document): Boolean {
        ErrorMessage = ""

        // Normalize
        document.Title = document.Title.trim()
        document.ImageUri = document.ImageUri.trim()

        // Generate ID if missing
        if (document.ID.isBlank()) {
            document.ID = IdUtil.newId()
        }

        // Validate
        if (!validate(document)) return false

        val existing = dataManager.GetDocumentById(document.ID)
        return if (existing == null) {
            dataManager.AddDocument(document)
        } else {
            dataManager.UpdateDocument(document)
        }
    }

    private fun validate(document: Document): Boolean {
        if (document.Title.isBlank()) {
            ErrorMessage = "Title is required."
            return false
        }
        if (document.ImageUri.isBlank()) {
            ErrorMessage = "ImageUri is required."
            return false
        }

        // Ensure CaptureDate
        try {
            document.CaptureDate
        } catch (_: UninitializedPropertyAccessException) {
            document.CaptureDate = LocalDate.now()
        }

        // Default Language
        if (document.Language.Name.isBlank()) {
            document.Language = Language().apply {
                Code = "en"; Name = "English"
            }
        }
        return true
    }

    fun Delete(id: String): Boolean = dataManager.DeleteDocument(id)
    fun GetById(id: String): Document? = dataManager.GetDocumentById(id)
    fun GetAll(): MutableList<Document> = dataManager.GetAllDocuments()
    fun Search(query: String): MutableList<Document> = dataManager.SearchDocuments(query)
    fun GetLanguages(): MutableList<Language> = dataManager.GetLanguages()
}
