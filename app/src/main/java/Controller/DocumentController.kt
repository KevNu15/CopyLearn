package Controller

import Entity.DTODocument
import Entity.DTOLanguage
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
 * Conecta con la API REST en lugar de usar DataManager
 *
 * Siguiendo el patrón de PersonController en Census
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

            // Convertir a DTO
            val dtoDocument = convertToDTODocument(document)

            // Verificar si existe
            val existing = getByIdInternal(document.ID)

            val response = if (existing == null) {
                // Crear nuevo
                CopyLearnAPIService.apiDocuments.postDocument(dtoDocument)
            } else {
                // Actualizar existente
                CopyLearnAPIService.apiDocuments.updateDocument(dtoDocument)
            }

            if (response.responseCode != 200) {
                ErrorMessage = response.message
                return false
            }

            return true

        } catch (e: Exception) {
            Log.e(TAG, "Error saving document: ${e.message}", e)
            ErrorMessage = context.getString(R.string.err_save_document)
            return false
        }
    }

    /**
     * Eliminar documento
     */
    suspend fun Delete(id: String): Boolean {
        try {
            val dtoDocument = DTODocument(ID = id)
            val response = CopyLearnAPIService.apiDocuments.deleteDocument(dtoDocument)

            if (response.responseCode != 200) {
                ErrorMessage = response.message
                return false
            }

            return true

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
            val response = CopyLearnAPIService.apiDocuments.getAll()

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

            val response = CopyLearnAPIService.apiDocuments.search(query)

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
                languages.add(Language(dtoLang.Code, dtoLang.Name))
            }

            languages

        } catch (e: Exception) {
            Log.e(TAG, "Error getting languages: ${e.message}", e)
            return getDefaultLanguages()
        }
    }

    // ========================================
    // MÉTODOS PRIVADOS
    // ========================================

    /**
     * Validar documento
     */
    private fun validate(document: Document): Boolean {
        if (document.Title.isBlank()) {
            ErrorMessage = "Title is required."
            return false
        }

        // ImageUri ya no es obligatorio (puede estar en la nube)
        // if (document.ImageUri.isBlank()) {
        //     ErrorMessage = "ImageUri is required."
        //     return false
        // }

        // Asegurar CaptureDate
        try {
            document.CaptureDate
        } catch (_: UninitializedPropertyAccessException) {
            document.CaptureDate = LocalDate.now()
        }

        // Default Language
        if (document.Language.Name.isBlank()) {
            document.Language = Language().apply {
                Code = "en"
                Name = "English"
            }
        }

        return true
    }

    /**
     * Obtener documento por ID (interno, sin manejo de errores)
     */
    private suspend fun getByIdInternal(id: String): Document? {
        val response = CopyLearnAPIService.apiDocuments.getById(id)

        if (response.responseCode != 200 || response.data.isEmpty()) {
            return null
        }

        return convertToDocument(response.data[0])
    }

    /**
     * Convertir Document a DTODocument
     */
    private fun convertToDTODocument(document: Document): DTODocument {
        val dateString = formatDateToString(document.CaptureDate)

        val dtoLanguage = DTOLanguage(
            Code = document.Language.Code,
            Name = document.Language.Name
        )

        return DTODocument(
            ID = document.ID,
            Title = document.Title,
            CaptureDate = dateString,
            ImageUri = document.ImageUri,
            RecognizedText = document.RecognizedText,
            Language = dtoLanguage,
            OcrConfidence = document.OcrConfidence
        )
    }

    /**
     * Convertir DTODocument a Document
     */
    private fun convertToDocument(dtoDoc: DTODocument): Document {
        val document = Document()
        document.ID = dtoDoc.ID
        document.Title = dtoDoc.Title
        document.CaptureDate = parseStringToDate(dtoDoc.CaptureDate)
        document.ImageUri = dtoDoc.ImageUri
        document.RecognizedText = dtoDoc.RecognizedText
        document.OcrConfidence = dtoDoc.OcrConfidence

        // Convertir Language
        dtoDoc.Language?.let { dtoLang ->
            document.Language = Language(dtoLang.Code, dtoLang.Name)
        } ?: run {
            document.Language = Language("en", "English")
        }

        return document
    }

    /**
     * Formatear LocalDate a String (yyyy-MM-dd)
     */
    private fun formatDateToString(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
    }

    /**
     * Parsear String a LocalDate (yyyy-MM-dd)
     */
    private fun parseStringToDate(dateString: String): LocalDate {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            LocalDate.parse(dateString, formatter)
        } catch (e: Exception) {
            Log.w(TAG, "Error parsing date: $dateString", e)
            LocalDate.now()
        }
    }

    /**
     * Idiomas por defecto (fallback)
     */
    private fun getDefaultLanguages(): MutableList<Language> {
        return mutableListOf(
            Language("en", "English"),
            Language("es", "Spanish")
        )
    }
}