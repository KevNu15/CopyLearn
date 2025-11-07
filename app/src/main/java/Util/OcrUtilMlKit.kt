package Util

import android.content.Context
import android.net.Uri

/**
 * Skeleton de OCR con ML Kit.
 *
 * Nota: Este archivo está diseñado para COMPILAR sin dependencias externas.
 * Por eso, la función extractText() llama inmediatamente a onError(...) para
 * que la Activity haga fallback al stub (OcrUtil).
 *
 * Cuando decidas habilitar ML Kit:
 *  1) Agrega las dependencias en build.gradle.kts (app).
 *  2) Reemplaza el cuerpo de extractText() por el código de ejemplo que
 *     está al final de este archivo (comentado).
 */
object OcrUtilMlKit {

    data class OcrResult(val text: String, val confidence: Double)

    /** API basada en callbacks (no bloquea UI). */
    fun extractText(
        context: Context,
        imageUri: Uri,
        onResult: (OcrResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        // ML Kit aún NO está habilitado: devolvemos error para que el caller
        // use el fallback (OcrUtil) sin romper la compilación.
        onError(IllegalStateException("ML Kit is not enabled in this build."))
    }

    /** Versión suspend (no usada de momento). */
    @Suppress("UNUSED_PARAMETER")
    suspend fun extractTextSuspend(
        context: Context,
        imageUri: Uri
    ): OcrResult {
        throw IllegalStateException("ML Kit is not enabled in this build.")
    }

    /* ------------------ EJEMPLO REAL (COMENTADO) ------------------
    // Habilitar luego de agregar dependencias:

    // import com.google.mlkit.vision.common.InputImage
    // import com.google.mlkit.vision.text.TextRecognition
    // import com.google.mlkit.vision.text.latin.TextRecognizerOptions
    // import kotlinx.coroutines.tasks.await

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
                .addOnSuccessListener { txt ->
                    val text = txt.text ?: ""
                    onResult(OcrResult(text = text, confidence = 0.95))
                }
                .addOnFailureListener { e -> onError(e) }
        } catch (e: Exception) {
            onError(e)
        }
    }

    suspend fun extractTextSuspend(
        context: Context,
        imageUri: Uri
    ): OcrResult {
        val image = InputImage.fromFilePath(context, imageUri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val txt = recognizer.process(image).await()
        return OcrResult(text = txt.text ?: "", confidence = 0.95)
    }
    ---------------------------------------------------------------- */
}
