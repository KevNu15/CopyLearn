package Util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageFileUtil {

    private const val TAG = "ImageFileUtil"

    /**
     * Crea un Uri para guardar una imagen capturada.
     * Optimizado para API 36 (Android 15).
     */
    fun createImageUri(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "CopyLearn_$timeStamp.jpg"

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // API 29+ (Android 10+): Usar MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/CopyLearn")
                }

                val uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                if (uri != null) {
                    Log.d(TAG, "Uri creado exitosamente: $uri")
                    uri
                } else {
                    Log.e(TAG, "MediaStore retornó null, usando fallback")
                    createFileProviderUri(context, fileName)
                }

            } else {
                // API <29: Usar FileProvider
                createFileProviderUri(context, fileName)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creando Uri: ${e.message}", e)
            // Fallback: usar FileProvider
            createFileProviderUri(context, fileName)
        }
    }

    /**
     * Método fallback usando FileProvider
     */
    private fun createFileProviderUri(context: Context, fileName: String): Uri {
        val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: context.filesDir

        val appDir = File(picturesDir, "CopyLearn").apply {
            if (!exists()) {
                val created = mkdirs()
                Log.d(TAG, "Directorio creado: $created")
            }
        }

        val imageFile = File(appDir, fileName)
        val authority = "${context.packageName}.fileprovider"

        return FileProvider.getUriForFile(context, authority, imageFile).also {
            Log.d(TAG, "FileProvider Uri creado: $it")
        }
    }
}