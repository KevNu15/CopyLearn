package Util

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

/**
 * Utilidad de OCR con ML Kit Text Recognition (ACTIVADO).
 *
 * Uso recomendado: llamar extractText() con callbacks para no bloquear UI.
 */
object OcrUtilMlKit {

    data class OcrResult(val text: String, val confidence: Double)

    /**
     * Extrae texto de una imagen usando ML Kit (callback-based).
     *
     * @param context Contexto para resolver el Uri
     * @param imageUri Uri de la imagen (content:// o file://)
     * @param onResult Callback con el resultado exitoso
     * @param onError Callback en caso de error
     */
    fun extractText(
        context: Context,
        imageUri: Uri,
        onResult: (OcrResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val image = InputImage.fromFilePath(context, imageUri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val text = visionText.text
                    // ML Kit no devuelve confianza global, asignamos valor alto
                    val result = OcrResult(text = text, confidence = 0.95)
                    onResult(result)
                }
                .addOnFailureListener { exception ->
                    onError(exception)
                }
        } catch (e: Exception) {
            onError(e)
        }
    }

    /**
     * Versión suspend para uso con coroutines (opcional).
     */
    suspend fun extractTextSuspend(
        context: Context,
        imageUri: Uri
    ): OcrResult {
        val image = InputImage.fromFilePath(context, imageUri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val visionText = recognizer.process(image).await()
        return OcrResult(text = visionText.text, confidence = 0.95)
    }
}