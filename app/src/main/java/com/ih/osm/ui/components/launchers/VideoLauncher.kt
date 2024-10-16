package com.ih.osm.ui.components.launchers

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.ih.osm.R
import com.ih.osm.core.ui.functions.FileType
import com.ih.osm.core.ui.functions.getUriForFile
import com.ih.osm.core.ui.functions.openAppSettings
import com.ih.osm.ui.pages.createcard.CardItemIcon
import com.ih.osm.ui.utils.COMPRESS_QUALITY
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.launch

@Composable
fun VideoLauncher(videoLimitDuration: Int = 120, onComplete: (uri: Uri) -> Unit) {
    val context = LocalContext.current
    var fileInfo = context.getUriForFile(fileType = FileType.VIDEO)
    var uri = fileInfo.first
    var file = fileInfo.second
    val scope = rememberCoroutineScope()

    val recordVideoLauncher =
        rememberLauncherForActivityResult(
            contract =
            ActivityResultContracts.CaptureVideo().apply {
                createIntent(context, uri).also {
                    it.putExtra(MediaStore.EXTRA_DURATION_LIMIT, videoLimitDuration)
                }
            },
            onResult = {
                if (it) {
                    scope.launch {
                        val compressedImage = Compressor.compress(context, file) {
                            quality(COMPRESS_QUALITY)
                        }
                        val newUri = compressedImage.toUri()
                        onComplete(newUri)
                        fileInfo = context.getUriForFile(fileType = FileType.VIDEO)
                        uri = fileInfo.first
                        file = fileInfo.second
                    }
                }
            }
        )

    val permissionLauncher =
        rememberLauncherForActivityResult(
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
