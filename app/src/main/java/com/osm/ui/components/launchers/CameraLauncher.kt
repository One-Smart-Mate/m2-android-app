package com.osm.ui.components.launchers

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.osm.R
import com.osm.core.ui.functions.FileType
import com.osm.core.ui.functions.getUriForFile
import com.osm.core.ui.functions.openAppSettings
import com.osm.ui.pages.createcard.CardItemIcon

@Composable
fun CameraLauncher(
    onComplete: (uri: Uri) -> Unit
) {
    val context = LocalContext.current
    val uri = context.getUriForFile(fileType = FileType.IMAGE).first
    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            capturedImageUri = uri
            onComplete(capturedImageUri)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            cameraLauncher.launch(uri)
        } else {
            openAppSettings(context)
        }
    }

    CardItemIcon(icon = painterResource(id = R.drawable.ic_photo_camera)) {
        val permissionCheckResult =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(uri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}