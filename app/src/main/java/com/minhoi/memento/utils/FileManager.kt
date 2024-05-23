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

    fun uriToMultipartBodyPart(uri: Uri, mimeType: String): MultipartBody.Part? {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val file = File(context.cacheDir, getFileName(context, uri))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
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
}