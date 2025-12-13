package Controller

import Entity.DTODocument
import Entity.DTODocumentRequest
import Entity.DTOLanguage
import Entity.DTOLanguageRequest
import Entity.DeleteRequest
import Entity.Document
import Entity.Language
import Util.CopyLearnAPIService
import Util.IdUtil
import android.content.Context
import android.util.Log
import com.example.copylearn.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Controlador de documentos
 */
class DocumentController(private val context: Context) {

    var ErrorMessage: String = ""
    private val TAG = "DocumentController"

    /**
     * Guardar documento (crear o actualizar)
     */
    suspend fun Save(document: Document): Boolean {
        ErrorMessage = ""

        try {
            // Normalizar
            document.Title = document.Title.trim()
            document.ImageUri = document.ImageUri.trim()

            // Generar ID si falta
            if (document.ID.isBlank()) {
                document.ID = IdUtil.newId()
            }

            // Validar
            if (!validate(document)) return false

            Log.d(TAG, "Saving document with ID: ${document.ID}")

            // Convertir a DTO simplificado
            val dtoRequest = DTODocumentRequest(
                documentId = document.ID,
                title = document.Title,
                captureDate = formatDateToString(document.CaptureDate),
                imageUri = document.ImageUri,
                recognizedText = document.RecognizedText,
                language = DTOLanguageRequest(
                    code = document.Language.Code,
                    name = document.Language.Name
                ),
                ocrConfidence = document.OcrConfidence
            )

            // Verificar si existe
            val existing = getByIdInternal(document.ID)

            Log.d(TAG, "Document exists: ${existing != null}")

            val response = if (existing == null) {
                Log.d(TAG, "Creating new document...")
                CopyLearnAPIService.apiDocuments.createDocument(dtoRequest)
            } else {
                Log.d(TAG, "Updating existing document...")
                CopyLearnAPIService.apiDocuments.updateDocument(dtoRequest)
            }

            Log.d(TAG, "Response code: ${response.responseCode}, message: ${response.message}")

            if (response.responseCode != 200 && response.responseCode != 201) {
                ErrorMessage = response.message
                return false
            }

            return true

        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e(TAG, "HTTP Error ${e.code()}: $errorBody", e)
            ErrorMessage = "HTTP ${e.code()}: ${e.message()}"
            return false
        } catch (e: Exception) {
            Log.e(TAG, "Error saving document: ${e.message}", e)
            e.printStackTrace()
            ErrorMessage = "Error: ${e.message ?: "Unknown error"}"
            return false
        }
    }

    /**
     * Eliminar documento
     */
    suspend fun Delete(id: String): Boolean {
        try {
            Log.d(TAG, "Deleting document with ID: $id")

            val deleteRequest = DeleteRequest(documentId = id)
            val response = CopyLearnAPIService.apiDocuments.deleteDocument(deleteRequest)

            Log.d(TAG, "Delete response: ${response.responseCode}, ${response.message}")

            if (response.responseCode != 200) {
                ErrorMessage = response.message
                return false
            }

            return true

        } catch (e: retrofit2.HttpException) {
            Log.e(TAG, "HTTP Error deleting: ${e.code()}", e)
            ErrorMessage = "HTTP ${e.code()}: ${e.message()}"
            return false
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting document: ${e.message}", e)
            ErrorMessage = context.getString(R.string.err_delete_document)
            return false
        }
    }

    /**
     * Obtener documento por ID
     */
    suspend fun GetById(id: String): Document? {
        return try {
            getByIdInternal(id)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting document by ID: ${e.message}", e)
            ErrorMessage = context.getString(R.string.err_get_document)
            null
        }
    }

    /**
     * Obtener todos los documentos
     */
    suspend fun GetAll(): MutableList<Document> {
        return try {
            val response = CopyLearnAPIService.apiDocuments.getAllDocuments()

            if (response.responseCode != 200) {
                ErrorMessage = response.message
                return mutableListOf()
            }

            val documents = mutableListOf<Document>()
            response.data.forEach { dtoDoc ->
                documents.add(convertToDocument(dtoDoc))
            }

            Log.d(TAG, "Retrieved ${documents.size} documents")
            documents

        } catch (e: Exception) {
            Log.e(TAG, "Error getting all documents: ${e.message}", e)
            ErrorMessage = context.getString(R.string.err_get_documents)
            mutableListOf()
        }
    }

    /**
     * Buscar documentos
     */
    suspend fun Search(query: String): MutableList<Document> {
        return try {
            if (query.isBlank()) {
                return GetAll()
            }

            val response = CopyLearnAPIService.apiDocuments.searchDocuments(query)

            if (response.responseCode != 200) {
                ErrorMessage = response.message
                return mutableListOf()
            }

            val documents = mutableListOf<Document>()
            response.data.forEach { dtoDoc ->
                documents.add(convertToDocument(dtoDoc))
            }

            Log.d(TAG, "Search found ${documents.size} documents")
            documents

        } catch (e: Exception) {
            Log.e(TAG, "Error searching documents: ${e.message}", e)
            ErrorMessage = context.getString(R.string.err_search_documents)
            mutableListOf()
        }
    }

    /**
     * Obtener idiomas disponibles
     */
    suspend fun GetLanguages(): MutableList<Language> {
        return try {
            val response = CopyLearnAPIService.apiDocuments.getLanguages()

            if (response.responseCode != 200) {
                ErrorMessage = response.message
                return getDefaultLanguages()
            }

            val languages = mutableListOf<Language>()
            response.data.forEach { dtoLang ->
                languages.add(Language(dtoLang.code, dtoLang.name))
            }

            languages

        } catch (e: Exception) {
            Log.e(TAG, "Error getting languages: ${e.message}", e)
            return getDefaultLanguages()
        }
    }


    // MÉTODOS PRIVADOS

    private fun validate(document: Document): Boolean {
        if (document.Title.isBlank()) {
            ErrorMessage = "Title is required."
            return false
        }

        try {
            document.CaptureDate
        } catch (_: UninitializedPropertyAccessException) {
            document.CaptureDate = LocalDate.now()
        }

        if (document.Language.Name.isBlank()) {
            document.Language = Language().apply {
                Code = "en"
                Name = "English"
            }
        }

        return true
    }

    private suspend fun getByIdInternal(id: String): Document? {
        return try {
            val response = CopyLearnAPIService.apiDocuments.getDocument(id)

            if (response.responseCode != 200 || response.data.isEmpty()) {
                null
            } else {
                convertToDocument(response.data[0])
            }
        } catch (e: Exception) {
            Log.w(TAG, "Document not found: $id", e)
            null
        }
    }

    private fun convertToDocument(dtoDoc: DTODocument): Document {
        val document = Document()
        document.ID = dtoDoc.documentId
        document.Title = dtoDoc.title
        document.CaptureDate = parseStringToDate(dtoDoc.captureDate)
        document.ImageUri = dtoDoc.imageUri ?: ""
        document.RecognizedText = dtoDoc.recognizedText ?: ""
        document.OcrConfidence = dtoDoc.ocrConfidence ?: 0.0

        dtoDoc.language?.let { dtoLang ->
            document.Language = Language(dtoLang.code, dtoLang.name)
        } ?: run {
            document.Language = Language("en", "English")
        }

        return document
    }

    private fun formatDateToString(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
    }

    private fun parseStringToDate(dateString: String): LocalDate {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            LocalDate.parse(dateString, formatter)
        } catch (e: Exception) {
            Log.w(TAG, "Error parsing date: $dateString", e)
            LocalDate.now()
        }
    }

    private fun getDefaultLanguages(): MutableList<Language> {
        return mutableListOf(
            Language("en", "English"),
            Language("es", "Spanish")
        )
    }
}
