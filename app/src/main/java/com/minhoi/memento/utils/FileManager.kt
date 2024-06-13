package com.minhoi.memento.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.minhoi.memento.data.network.SaveFileResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class FileManager @Inject constructor(
    private val context: Context,
) {

    fun getFileMimeType(uri: Uri): String? {
        val contentResolver = context.contentResolver
        return contentResolver.getType(uri)
    }

    fun uriToMultipartBodyPart(uri: Uri, mimeType: String, fileName: String): MultipartBody.Part? {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val file = File(context.cacheDir, getFileName(context, uri))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(fileName, file.name, requestFile)
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var name = ""
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        returnCursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }

    suspend fun uriToBitmap(context: Context, imageUrl: String): Bitmap {
        val bitmap = withContext(Dispatchers.IO) {
            Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get()
        }
        return bitmap
    }

    suspend fun saveImageToGallery(imageUrl: String): SaveFileResult {
        val fileName = "Memento ${System.currentTimeMillis()}"
        // 비트맵으로 이미지 다운로드
        try {
            val bitmap = uriToBitmap(context, imageUrl)

            // 갤러리에 저장
            withContext(Dispatchers.IO) {
                val contentResolver = context.contentResolver
                val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.jpg")
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/Memento")
                    }
                    contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                } else {
                    val imagesDir = context.getExternalFilesDir("Pictures/MyApp")
                    val imageFile = File(imagesDir, "$fileName.jpg")
                    FileOutputStream(imageFile).use {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    }
                    Uri.fromFile(imageFile)
                }

                imageUri?.let { uri ->
                    contentResolver.openOutputStream(uri)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    }
                }
            }
            return SaveFileResult.Success
        } catch (e: Exception) {
            return SaveFileResult.Failure(e)
        }
    }
}