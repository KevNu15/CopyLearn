package Util

import Interface.IDocumentAPIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton que configura Retrofit para conectarse a la API de CopyLearn
 * Siguiendo el patrón del proyecto Census
 */
object CopyLearnAPIService {

    /**
     * URL base de la API
     *
     * IMPORTANTE: Cambiar esta URL por la de tu API desplegada en Azure
     *
     * Opciones:
     * - Local: "http://10.0.2.2:8080" (emulador Android)
     * - Azure: "https://copylearn-api-gvdvhfh5fzfed5gt.eastus-01.azurewebsites.net/"
     */
    private const val BASE_URL = "https://copylearn-api-gvdvhfh5fzfed5gt.eastus-01.azurewebsites.net/"

    /**
     * Instancia lazy de la API de documentos
     * Se crea solo cuando se necesita por primera vez
     */
    val apiDocuments: IDocumentAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IDocumentAPIService::class.java)
    }
}