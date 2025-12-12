package Entity

/**
 * Respuesta de la API para GET /languages
 * Devuelve lista de idiomas disponibles
 */
data class LanguageGetResponse(
    val data: List<DTOLanguage>,
    val message: String,
    val responseCode: Int
)