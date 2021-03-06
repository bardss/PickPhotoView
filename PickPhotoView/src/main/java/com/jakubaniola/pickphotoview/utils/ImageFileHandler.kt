package com.jakubaniola.pickphotoview.utils

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.net.toUri
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

typealias PictureSize = Pair<Int, Int>

internal class ImageFileHandler(private val compressQuality: Int = 60) {

    fun getPathFromUri(context: Context, uri: Uri): String? {
        return when {
            uri.toString().contains("content:") -> getRealPathFromURI(context, uri)
            uri.toString().contains("file:") -> uri.path
            else -> null
        }
    }

    private fun getRealPathFromURI(context: Context, uri: Uri): String? {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.applicationContext.contentResolver?.query(
                uri, proj, null, null, null
            )
            val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            return columnIndex?.let { cursor?.getString(it) }
        } finally {
            cursor?.close()
        }
    }

    fun saveOptimisedPictureFromPathAndReturnPath(
        path: String?,
        storageDir: File
    ): String? {
        val pictureBitmap = BitmapFactory.decodeFile(path)
        return if (pictureBitmap != null) {
            val file = File(path)
            val rotateBitmap = performRotation(file, pictureBitmap)
            val resizedBitmap = resizeBitmapWhenTooLarge(rotateBitmap)
            saveBitmap(resizedBitmap, compressQuality, storageDir)
        } else null
    }

    fun overwriteOptimisedPictureFromPathAndReturnPath(
        path: String?
    ): String? {
        val pictureBitmap = BitmapFactory.decodeFile(path)
        return if (pictureBitmap != null) {
            val file = File(path)
            val rotateBitmap = performRotation(file, pictureBitmap)
            val resizedBitmap = resizeBitmapWhenTooLarge(rotateBitmap)
            overwriteBitmap(resizedBitmap, compressQuality, file)
        } else null
    }

    fun getBitmapDrawableFromPath(context: Context, path: String?, cornerRadius: Float = 20f): RoundedBitmapDrawable? {
        val pictureBitmap = BitmapFactory.decodeFile(path)
        return if (pictureBitmap != null) {
            val drawable = RoundedBitmapDrawableFactory.create(
                context.resources,
                pictureBitmap
            )
            drawable.cornerRadius = cornerRadius
            drawable
        } else null
    }

    private fun resizeBitmapWhenTooLarge(
        source: Bitmap,
        maxWidth: Int = 1200,
        maxHeight: Int = 1200
    ): Bitmap {
        return if (source.width > maxWidth || source.height > maxHeight) {
            val reducedSize: PictureSize =
                getReducedSize(maxWidth, maxHeight, source.width, source.height)
            Bitmap.createScaledBitmap(
                source,
                reducedSize.first,
                reducedSize.second,
                true
            )
        } else source
    }

    private fun getReducedSize(
        maxWidth: Int,
        maxHeight: Int,
        sourceWidth: Int,
        sourceHeight: Int
    ): Pair<Int, Int> {
        var reducedSize: PictureSize = Pair(sourceWidth, sourceHeight)
        while (reducedSize.first > maxWidth && reducedSize.second > maxHeight) {
            reducedSize = reduceTheSizeBy10Percent(reducedSize)
        }
        return reducedSize
    }

    private fun reduceTheSizeBy10Percent(size: PictureSize): PictureSize {
        val width = (size.first * 0.9).toInt()
        val height = (size.second * 0.9).toInt()
        return Pair(width, height)
    }

    private fun performRotation(file: File, bitmap: Bitmap): Bitmap {
        val ei = ExifInterface(file.absolutePath)
        val orientation = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(
                bitmap,
                90f
            )
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(
                bitmap,
                180f
            )
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(
                bitmap,
                270f
            )
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }
    }

    private fun saveBitmap(
        bitmapToSave: Bitmap,
        quality: Int,
        storageDir: File
    ): String? {
        try {
            val fileToSaveTo = createFileWithTimeStamp(storageDir)
            val fileOutputStream = FileOutputStream(fileToSaveTo)
            bitmapToSave.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)
            fileOutputStream.close()
            return fileToSaveTo.absolutePath
        } catch (e: FileNotFoundException) {
            val message = e.message ?: "no error message"
            Log.e("File not found: ", message)
        } catch (e: IOException) {
            val message = e.message ?: "no error message"
            Log.e("Error accessing file: ", message)
        }
        return null
    }

    private fun overwriteBitmap(
        bitmapToSave: Bitmap,
        quality: Int,
        fileToOverwrite: File
    ): String? {
        try {
            val fileOutputStream = FileOutputStream(fileToOverwrite)
            bitmapToSave.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)
            fileOutputStream.close()
            return fileToOverwrite.absolutePath
        } catch (e: FileNotFoundException) {
            val message = e.message ?: "no error message"
            Log.e("File not found: ", message)
        } catch (e: IOException) {
            val message = e.message ?: "no error message"
            Log.e("Error accessing file: ", message)
        }
        return null
    }

    fun createFileAndGetURI(
        storageDir: File
    ): Uri {
        val photoFile = createFileWithTimeStamp(storageDir)
        return photoFile.toUri()
    }

    private fun createFileWithTimeStamp(storageDir: File): File {
        val fileNameWithTimeStamp = createFileNameWithTimeStamp()
        return File.createTempFile(fileNameWithTimeStamp, ".jpg", storageDir)
    }

    private fun createFileNameWithTimeStamp(): String {
        val stamp = System.currentTimeMillis()
        return "pick_photo_$stamp"
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(angle) }
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

}