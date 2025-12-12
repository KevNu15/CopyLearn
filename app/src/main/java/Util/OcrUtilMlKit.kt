package Util

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await


object OcrUtilMlKit {

    data class OcrResult(val text: String, val confidence: Double)


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