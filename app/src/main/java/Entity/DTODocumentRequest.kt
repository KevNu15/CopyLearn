package Entity

import com.google.gson.annotations.SerializedName

/**
 * DTO for creating/updating documents.
 * Aligned with the API specification.
 */
data class DTODocumentRequest(
    @SerializedName("documentId") val documentId: String? = null,
    @SerializedName("title") val title: String,
    @SerializedName("captureDate") val captureDate: String,
    @SerializedName("imageUri") val imageUri: String,
    @SerializedName("recognizedText") val recognizedText: String,
    @SerializedName("language") val language: DTOLanguageRequest,
    @SerializedName("ocrConfidence") val ocrConfidence: Double
)

data class DTOLanguageRequest(
    @SerializedName("code") val code: String,
    @SerializedName("name") val name: String
)
