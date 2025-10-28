package com.eunhye.storeassistant


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object ImageUtils {

    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDirectory = File(context.filesDir, "product_images")

        if (!storageDirectory.exists()) {
            storageDirectory.mkdirs()
        }

        return File(storageDirectory, "PRODUCT_${timeStamp}.jpg")
    }

    fun createImageUri(context: Context): Uri {
        val imageFile = createImageFile(context)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.file provider",
            imageFile
        )
    }

    fun saveImageToInternalStorage (context: Context, sourceUri: Uri): String? {
        try {
            val inputStream = context.contentResolver.openInputStream(sourceUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            //save
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val storageDirectory = File(context.filesDir, "product_images")

            if (!storageDirectory.exists()) {
                storageDirectory.mkdirs()
            }

            val imageFile = File(storageDirectory, "PRODUCT_${timeStamp}.jpg")
            val outputStream = FileOutputStream(imageFile)

            //compress
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.flush()
            outputStream.close()

            return imageFile.absolutePath
        } catch (e: IOException){
            e.printStackTrace()
            return null
        }
    }

    fun deleteImage(imagePath: String): Boolean{
        if (imagePath == null) return false

        return try {
            val file = File(imagePath)
            if (file.exists()) {
                file.delete()
            } else {false}
        } catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    fun getAllProductImages(context: Context): List<File> {
        val storageDirectory = File(context.filesDir, "product_images")
        return if (storageDirectory.exists()) {
            storageDirectory.listFiles()?.toList() ?: emptyList()
        } else {
            emptyList()
        }
    }
}