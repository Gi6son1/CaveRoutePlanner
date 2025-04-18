package com.majorproject.caverouteplanner.datasource.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


fun copyImageToInternalStorageFromAssets(
    context: Context,
    assetFileName: String,
    imageName: String
): String? {

    val destinationDirectory = File(context.filesDir, "surveys")
    if (!destinationDirectory.exists() && !destinationDirectory.mkdirs()) {
        return null
    }

    val destinationFile = File(destinationDirectory, imageName)
    try {
        var inputStream = context.assets.open(assetFileName)
        var outputStream = FileOutputStream(destinationFile)

        val buffer = ByteArray(4096)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } > 0) {
            outputStream.write(buffer, 0, bytesRead)
        }

        inputStream.close()
        outputStream.close()

        return destinationFile.absolutePath
    } catch (e: IOException) {
        return null
    }
}

fun copyImageToInternalStorageFromTemp(
    context: Context,
    imageName: String,
): String? {

    val destinationDirectory = File(context.filesDir, "surveys")
    if (!destinationDirectory.exists() && !destinationDirectory.mkdirs()) {
        return null
    }

    val destinationFile = File(destinationDirectory, imageName)
    val sourceFile = File(context.filesDir, "temp_images/temp_image.jpg")

    try {
        val bitmap = BitmapFactory.decodeFile(sourceFile.absolutePath)
        val outputStream = FileOutputStream(destinationFile)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        outputStream.close()
        return destinationFile.absolutePath
    } catch (e: IOException) {
        return null
    }
}


fun saveUploadedImageToTempStorage(
    bitmap: Uri,
    contentResolver: ContentResolver,
    context: Context
): String? {
    val source = ImageDecoder.createSource(contentResolver, bitmap)
    val bitmap = ImageDecoder.decodeBitmap(source)

    val destinationDirectory = File(context.filesDir, "temp_images")
    if (!destinationDirectory.exists() && !destinationDirectory.mkdirs()) {
        return null
    }

    val destinationFile = File(destinationDirectory, "temp_image.jpg")
    try {
        val outputStream = FileOutputStream(destinationFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        return destinationFile.absolutePath
    } catch (e: IOException) {
        return null
    }
}

fun getBitmapFromTempInternalStorage(context: Context): ImageBitmap? {
    var imageBitmap: ImageBitmap? = null

    try {
        val destinationDirectory = File(context.filesDir, "temp_images")
        val file = File(destinationDirectory, "temp_image.jpg")

        if (file.exists()) {
            val fileInputStream = FileInputStream(file)
            val bitmap = BitmapFactory.decodeStream(fileInputStream)
            imageBitmap = bitmap.asImageBitmap() // Convert to ImageBitmap

            fileInputStream.close()
        }
    } catch (e: IOException) {
        return null
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
        return false
    }
}

fun getBitmapFromInternalStorage(filePath: String): ImageBitmap? {
    var imageBitmap: ImageBitmap?

    try {
        val file = File(filePath)
        if (file.exists()) {
            val fileInputStream = FileInputStream(file)
            val bitmap = BitmapFactory.decodeStream(fileInputStream)
            imageBitmap = bitmap.asImageBitmap() // Convert to ImageBitmap

            fileInputStream.close()
        } else {
            return null
        }
    } catch (e: IOException) {
        return null
    }

    return imageBitmap
}