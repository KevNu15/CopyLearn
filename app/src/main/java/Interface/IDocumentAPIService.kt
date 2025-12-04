package Interface

import Entity.DTODocument
import Entity.DocumentGetResponse
import Entity.DocumentResponse
import Entity.LanguageGetResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Interfaz de Retrofit para la API de CopyLearn
 * Define todos los endpoints disponibles
 */
interface IDocumentAPIService {

    /**
     * GET /documents - Obtener todos los documentos
     */
    @GET("/documents")
    suspend fun getAll(): DocumentGetResponse

    /**
     * GET /documents/{id} - Obtener documento por ID
     */
    @GET("/documents/{id}")
    suspend fun getById(@Path("id") documentId: String): DocumentGetResponse

    /**
     * GET /documents/search/{query} - Buscar documentos
     */
    @GET("/documents/search/{query}")
    suspend fun search(@Path("query") searchQuery: String): DocumentGetResponse

    /**
     * POST /documents - Crear nuevo documento
     */
    @Headers("Content-Type: application/json")
    @POST("/documents")
    suspend fun postDocument(@Body document: DTODocument): DocumentResponse

    /**
     * PUT /documents - Actualizar documento existente
     */
    @Headers("Content-Type: application/json")
    @PUT("/documents")
    suspend fun updateDocument(@Body document: DTODocument): DocumentResponse

    /**
     * DELETE /documents - Eliminar documento
     */
    @Headers("Content-Type: application/json")
    @HTTP(method = "DELETE", path = "/documents", hasBody = true)
    suspend fun deleteDocument(@Body document: DTODocument): DocumentResponse

    /**
     * GET /languages - Obtener idiomas disponibles
     */
    @GET("/languages")
    suspend fun getLanguages(): LanguageGetResponse
}