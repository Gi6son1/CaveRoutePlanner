package com.majorproject.caverouteplanner.datasource.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


fun copyImageToInternalStorageFromAssets(
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

fun copyImageToInternalStorageFromTemp(
    context: Context,
    imageName: String,
    compressPercentage: Float = 0f
): String? {

    val destinationDirectory = File(context.filesDir, "surveys")
    if (!destinationDirectory.exists()) {
        if (!destinationDirectory.mkdirs()) {
            return null
        }
    }

    val destinationFile = File(destinationDirectory, imageName)
    val sourceFile = File(context.filesDir, "temp_images/temp_image.jpg")

    try {
        if (compressPercentage > 0f && compressPercentage <= 1f) {
            val bitmap = BitmapFactory.decodeFile(sourceFile.absolutePath)
            val outputStream = FileOutputStream(destinationFile)

            val newWidth = (bitmap.width * compressPercentage).toInt()
            val newHeight = (bitmap.height * compressPercentage).toInt()
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false)

            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            return destinationFile.absolutePath
        }
    } catch (e: IOException) {
        return null
    } finally {
    }
    return null
}


fun saveUploadedImageToTempStorage(
    bitmap: Uri,
    contentResolver: ContentResolver,
    context: Context
): String? {
    val source = ImageDecoder.createSource(contentResolver, bitmap)
    val bitmap = ImageDecoder.decodeBitmap(source)

    val destinationDirectory = File(context.filesDir, "temp_images")
    if (!destinationDirectory.exists()) {
        if (!destinationDirectory.mkdirs()) {
            return null
        }
    }

    val destinationFile = File(destinationDirectory, "temp_image.jpg")
    var outputStream: FileOutputStream? = null
    try {
        outputStream = FileOutputStream(destinationFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return destinationFile.absolutePath
    } catch (e: IOException) {
        return null
    } finally {
        try {
            outputStream?.close()
        } catch (e: IOException) {
        }
    }
}

fun getBitmapFromTempInternalStorage(context: Context): ImageBitmap? {
    var imageBitmap: ImageBitmap? = null
    var fileInputStream: FileInputStream? = null

    try {
        val destinationDirectory = File(context.filesDir, "temp_images")
        val file = File(destinationDirectory, "temp_image.jpg")

        if (file.exists()) {
            fileInputStream = FileInputStream(file)
            val bitmap = BitmapFactory.decodeStream(fileInputStream)
            imageBitmap = bitmap.asImageBitmap() // Convert to ImageBitmap
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

fun clearTempStorage(context: Context): Boolean {
    try {
        val destinationDirectory = File(context.filesDir, "temp_images")
        if (destinationDirectory.exists()) {
            val files = destinationDirectory.listFiles()
            files?.forEach { file ->
                if (!file.delete()) {
                    return false
                }
            }
        }
        return true
    } catch (e: IOException) {
        Log.e("MainActivity", "Error clearing temp storage: ${e.message}")
        return false
    }
}

fun getBitmapFromInternalStorage(filePath: String): ImageBitmap? {
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