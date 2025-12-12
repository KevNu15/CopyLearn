package Entity

import com.google.gson.annotations.SerializedName

/**
 * DTO for receiving documents from the API.
 * Matches the API's data model.
 */
data class DTODocument(
    val documentId: String,
    val title: String,
    val captureDate: String,
    val imageUri: String?,
    val recognizedText: String?,
    val language: DTOLanguage?,
    val ocrConfidence: Double?,
    val createdAt: String?,
    val updatedAt: String?
)
