package com.ih.m2.core.ui.functions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.ih.m2.BuildConfig

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    context.startActivity(intent)
}

@Composable
fun getContext(): Context {
    return LocalContext.current
}


fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )
}


fun Context.createVideoFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return File.createTempFile(
        "VIDEO_${timeStamp}_",
        ".mp4",
        externalCacheDir
    )
}

fun Context.createAudioFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return File.createTempFile(
        "AUDIO_${timeStamp}_",
        ".mp3",
        externalCacheDir
    )
}


fun Context.getUriForFile(fileType: FileType): Uri {
    val file = when(fileType) {
        FileType.IMAGE -> this.createImageFile()
        FileType.VIDEO -> this.createVideoFile()
        FileType.AUDIO -> this.createAudioFile()
    }
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(this),
        BuildConfig.APPLICATION_ID + ".provider", file
    )
    return uri
}

enum class FileType {
    IMAGE, VIDEO, AUDIO
}
