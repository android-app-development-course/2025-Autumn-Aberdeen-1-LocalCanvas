package com.example.mylocalcanvas.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.IOException

private val httpClient = OkHttpClient()

suspend fun saveImageToGallery(
    context: Context,
    imageUrl: String,
    workflowTypeId: String?,
    strength: Int?,
    steps: Int?
): Uri? {
    val bytes = downloadImageBytes(imageUrl) ?: return null

    val resolver = context.contentResolver
    val fileName = buildFileName(workflowTypeId, strength, steps)
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/LocalCanvas")
        }
    }

    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        ?: return null

    resolver.openOutputStream(uri)?.use { outputStream ->
        outputStream.write(bytes)
    } ?: return null

    return uri
}

private suspend fun downloadImageBytes(url: String): ByteArray? {
    val request = Request.Builder().url(url).build()
    val response = httpClient.newCall(request).execute()
    if (!response.isSuccessful) {
        response.close()
        return null
    }
    return response.body?.bytes().also { response.close() }
}

private fun buildFileName(
    workflowTypeId: String?,
    strength: Int?,
    steps: Int?
): String {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    if (workflowTypeId.isNullOrBlank() || strength == null || steps == null) {
        return "localcanvas_$timestamp.png"
    }
    val workflow = if (workflowTypeId == "local") "local" else "global"
    return "LC_${workflow}_s${strength}_steps${steps}_$timestamp.png"
}
