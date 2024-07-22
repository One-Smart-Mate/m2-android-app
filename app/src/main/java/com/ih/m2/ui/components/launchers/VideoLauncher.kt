package com.ih.m2.ui.components.launchers

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
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
import com.ih.m2.R
import com.ih.m2.core.ui.functions.FileType
import com.ih.m2.core.ui.functions.getUriForFile
import com.ih.m2.core.ui.functions.openAppSettings
import com.ih.m2.ui.pages.createcard.CardItemIcon

@Composable
fun VideoLauncher(
    videoLimitDuration: Int = 120,
    onComplete: (uri: Uri) -> Unit
) {
    val context = LocalContext.current
    val uri = context.getUriForFile(fileType = FileType.VIDEO).first
    var capturedVideoUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val recordVideoLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.CaptureVideo().apply {
            createIntent(context, uri).also {
                it.putExtra(MediaStore.EXTRA_DURATION_LIMIT, videoLimitDuration)
            }
        },
            onResult = {
                if (it) {
                    capturedVideoUri = uri
                    onComplete(capturedVideoUri)
                }
            })

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            recordVideoLauncher.launch(uri)
        } else {
            openAppSettings(context)
        }
    }

    CardItemIcon(icon = painterResource(id = R.drawable.ic_videocam)) {
        val permissionCheckResult =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            recordVideoLauncher.launch(uri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}
