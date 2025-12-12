package Entity

/**
 * Respuesta de la API para endpoints POST, PUT y DELETE
 * Devuelve un solo documento (o null)
 */
data class DocumentResponse(
    val data: DTODocument?,
    val message: String,
    val responseCode: Int
)