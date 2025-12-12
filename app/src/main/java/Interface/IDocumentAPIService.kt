package Interface

import Entity.ApiResponse
import Entity.DTODocument
import Entity.DTODocumentRequest
import Entity.DTOLanguage
import Entity.DeleteRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit service interface for the CopyLearn API.
 * Aligned with the API documentation.
 */
interface IDocumentAPIService {

    @GET("documents")
    suspend fun getAllDocuments(): ApiResponse<List<DTODocument>>

    @GET("documents/{id}")
    suspend fun getDocument(@Path("id") id: String): ApiResponse<List<DTODocument>>

    @GET("documents/search/{query}")
    suspend fun searchDocuments(@Path("query") query: String): ApiResponse<List<DTODocument>>

    @POST("documents")
    suspend fun createDocument(@Body document: DTODocumentRequest): ApiResponse<DTODocument>

    @PUT("documents")
    suspend fun updateDocument(@Body document: DTODocumentRequest): ApiResponse<DTODocument>

    @HTTP(method = "DELETE", path = "documents", hasBody = true)
    suspend fun deleteDocument(@Body request: DeleteRequest): ApiResponse<DTODocument>

    @GET("languages")
    suspend fun getLanguages(): ApiResponse<List<DTOLanguage>>
}
