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


/**
 *  This file holds the functions for dealing with internal storage and the storing of image bitmaps
 */


/**
 * This function copies an image from the assets folder to the internal storage of the device, used for the base LL example survey
 *
 * @param context The context of the activity
 * @param assetFileName The name of the file in the assets folder
 * @param imageName The name of the file to be saved in the internal storage
 *
 * @return The path of the file in the internal storage, or null if it failed
 */
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

/**
 * This function copies an image from the temp folder to the internal storage of the device, used for the user uploaded survey once the survey is ready to be properly saved
 *
 * @param context The context of the activity
 * @param imageName The name of the file to be saved in the internal storage
 *
 * @return The path of the file in the internal storage, or null if it failed
 */
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

/**
 * This function saves an image to the temp folder of the device, used for the user uploaded survey while it is being marked-up
 *
 * @param bitmap The bitmap URI to be used to access the image
 * @param contentResolver The content resolver to be used to access the image
 * @param context The context of the activity
 *
 * @return The path of the file in the internal storage, or null if it failed
 */
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


/**
 * This function gets an image from the temp folder of the device, used for the user uploaded survey to be accessed in the markup screen
 *
 * @param context The context of the activity
 *
 * @return The bitmap of the image, or null if it failed
 */
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


/**
 * This function clears the temp folder of the device, used once the temp survey has been saved to internal storage
 *
 * @param context The context of the activity
 *
 * @return True if the temp folder was cleared, false otherwise
 */
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

/**
 * This function gets an image from the internal storage of the device, used when the user wants to access a survey to navigate
 *
 * @param filePath The path of the file in the internal storage
 *
 * @return The bitmap of the image, or null if it failed
 */
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