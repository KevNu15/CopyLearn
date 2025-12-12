package Entity

/**
 * Respuesta de la API para endpoints GET
 * Devuelve una lista de documentos
 */
data class DocumentGetResponse(
    val data: List<DTODocument>,
    val message: String,
    val responseCode: Int
)