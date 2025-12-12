package Entity

/**
 * Generic class for handling API responses, as specified in the API documentation.
 */
data class ApiResponse<T>(
    val data: T,
    val message: String,
    val responseCode: Int
)
