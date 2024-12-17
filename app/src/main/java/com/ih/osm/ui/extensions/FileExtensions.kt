package com.ih.osm.ui.extensions

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

fun getFileFromUri(
    context: Context,
    uri: Uri,
): File? {
    return when (uri.scheme) {
        "file" -> {
            // Directly handle file:// URI
            File(uri.path)
        }
        "content" -> {
            // Handle content:// URI (e.g., from content providers)
            getFileFromContentUri(context, uri)
        }
        else -> null
    }
}

fun getFileFromContentUri(
    context: Context,
    uri: Uri,
): File? {
    var inputStream: InputStream? = null
    var outputStream: FileOutputStream? = null
    return try {
        // Open input stream for content URI
        inputStream = context.contentResolver.openInputStream(uri)
        val fileName = getFileNameFromUri(context, uri)
        val outputFile = File(context.cacheDir, fileName)

        outputStream = FileOutputStream(outputFile)
        inputStream?.copyTo(outputStream)
        outputFile
    } catch (e: IOException) {
        e.printStackTrace()
        null
    } finally {
        inputStream?.close()
        outputStream?.close()
    }
}

fun getFileNameFromUri(
    context: Context,
    uri: Uri,
): String {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    var fileName = "tempfile"
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                fileName = it.getString(nameIndex)
            }
        }
    }
    return fileName
}
