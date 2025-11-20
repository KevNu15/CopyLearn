package Util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageFileUtil {

    // Preferencia: app-scoped en Pictures/ usando FileProvider (API <29)
    fun createImageUri(context: Context): Uri {
        val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "IMG_$time.jpg"

        return if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/")
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }
            val resolver = context.contentResolver
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IllegalStateException("Cannot create MediaStore Uri")
        } else {
            val pictures = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ?: throw IllegalStateException("Pictures dir not available")
            val image = File(pictures, fileName)
            val authority = context.packageName + ".fileprovider"
            FileProvider.getUriForFile(context, authority, image)
        }
    }
}
