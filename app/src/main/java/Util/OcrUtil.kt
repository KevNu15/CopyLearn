package Util

import android.content.Context
import android.net.Uri

object OcrUtil {

    data class OcrResult(val text: String, val confidence: Double)


    fun extractText(context: Context, imageUri: Uri): OcrResult {
        return OcrResult(
            text = "OCR (stub) — replace with ML Kit. Uri: $imageUri",
            confidence = 0.90
        )
    }
}
