package Entity

/**
 * Respuesta de la API para endpoints GET
 * Devuelve una lista de documentos
 *
 * Siguiendo el patrón de PersonGetResponse en Census
 */
data class DocumentGetResponse(
    val data: List<DTODocument>,
    val message: String,
    val responseCode: Int
)