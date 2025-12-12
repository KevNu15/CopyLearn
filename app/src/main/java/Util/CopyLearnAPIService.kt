package Util

import Interface.IDocumentAPIService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object CopyLearnAPIService {

    /**
     * URL base de la API
     */
    private const val BASE_URL = "https://copylearn-api-gvdvhfh5fzfed5gt.eastus-01.azurewebsites.net/"

    /**
     * OkHttpClient con logging y timeouts aumentados
     */
    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Instancia lazy de la API de documentos
     */
    val apiDocuments: IDocumentAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IDocumentAPIService::class.java)
    }
}