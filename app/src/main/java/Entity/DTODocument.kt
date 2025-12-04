package Entity

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object para Document
 * Se usa para comunicación con la API (enviar/recibir JSON)
 *
 * Siguiendo el patrón de DTOPerson en Census
 */
data class DTODocument(
    @SerializedName("documentId") val ID: String,
    @SerializedName("title") val Title: String = "",
    @SerializedName("captureDate") val CaptureDate: String = "",
    @SerializedName("imageUri") val ImageUri: String = "",
    @SerializedName("recognizedText") val RecognizedText: String = "",
    @SerializedName("language") val Language: DTOLanguage? = null,
    @SerializedName("ocrConfidence") val OcrConfidence: Double = 0.0,
    @SerializedName("createdAt") val CreatedAt: String = "",
    @SerializedName("updatedAt") val UpdatedAt: String = ""
)

/**
 * DTO para Language (anidado en DTODocument)
 */
data class DTOLanguage(
    @SerializedName("code") val Code: String = "",
    @SerializedName("name") val Name: String = ""
)