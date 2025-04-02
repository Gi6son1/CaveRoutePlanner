package com.majorproject.caverouteplanner.datasource.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


fun copyImageToInternalStorage(
    context: Context,
    assetFileName: String,
    imageName: String
): String? {

    val destinationDirectory = File(context.filesDir, "surveys")
    if (!destinationDirectory.exists()) {
        if (!destinationDirectory.mkdirs()) {
            return null
        }
    }

    val destinationFile = File(destinationDirectory, imageName)
    var inputStream: InputStream? = null
    var outputStream: FileOutputStream? = null
    try {
        inputStream = context.assets.open(assetFileName)
        outputStream = FileOutputStream(destinationFile)

        val buffer = ByteArray(4096)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } > 0) {
            outputStream.write(buffer, 0, bytesRead)
        }

        return destinationFile.absolutePath
    } catch (e: IOException) {
        return null
    } finally {
        try {
            inputStream?.close()
            outputStream?.close()
        } catch (e: IOException) {
        }
    }
}

fun getBitmapFromInternalStorage(context: Context, filePath: String): ImageBitmap? {
    var imageBitmap: ImageBitmap? = null
    var fileInputStream: FileInputStream? = null

    try {
        val file = File(filePath)
        if (file.exists()) {
            fileInputStream = FileInputStream(file)
            val bitmap = BitmapFactory.decodeStream(fileInputStream)
            imageBitmap = bitmap.asImageBitmap() // Convert to ImageBitmap
        } else {
        }
    } catch (e: IOException) {
    } finally {
        try {
            fileInputStream?.close()
        } catch (e: IOException) {
        }
    }

    return imageBitmap
}