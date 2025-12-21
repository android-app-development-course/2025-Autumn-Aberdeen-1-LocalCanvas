package com.example.mylocalcanvas.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.annotation.DrawableRes
import java.io.File
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.InputStream

fun encodeDrawableToBase64(
    context: Context,
    @DrawableRes resId: Int
): String {
    val bitmap = BitmapFactory.decodeResource(context.resources, resId)
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    return "data:image/png;base64,$base64"
}

fun encodeUriToBase64(context: Context, uri: Uri): String {
    val inputStream: InputStream = context.contentResolver.openInputStream(uri)
        ?: error("Unable to open image uri.")
    val bytes = inputStream.use { it.readBytes() }
    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
    val mimeType = context.contentResolver.getType(uri) ?: "image/png"
    return "data:$mimeType;base64,$base64"
}

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "capture_${System.currentTimeMillis()}.png")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    return Uri.fromFile(file)
}
