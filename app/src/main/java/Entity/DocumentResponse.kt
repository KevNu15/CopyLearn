package Entity

/**
 * Respuesta de la API para endpoints POST, PUT y DELETE
 * Devuelve un solo documento (o null)
 *
 * Siguiendo el patrón de PersonResponse en Census
 */
data class DocumentResponse(
    val data: DTODocument?,
    val message: String,
    val responseCode: Int
)