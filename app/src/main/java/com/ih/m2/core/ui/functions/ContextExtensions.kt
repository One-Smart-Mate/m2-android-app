package com.ih.m2.core.ui.functions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.core.util.rangeTo
import com.ih.m2.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
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

@SuppressLint("SimpleDateFormat")
fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}


fun Context.getUriForPhoto(): Uri {
    val file = this.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(this),
        BuildConfig.APPLICATION_ID + ".provider", file
    )
    return uri
}