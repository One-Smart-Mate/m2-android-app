package com.ih.osm.ui.components.launchers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.ih.osm.R
import com.ih.osm.core.ui.functions.openAppSettings
import com.ih.osm.ui.pages.createcard.CardItemIcon
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoLauncher(onComplete: (uri: Uri) -> Unit) {
    var showVideoLauncher by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) {
            if (checkPermission(context, Manifest.permission.RECORD_AUDIO) &&
                checkPermission(context, Manifest.permission.CAMERA)
            ) {
                scope.launch {
                    showVideoLauncher = true
                    state.expand()
                }
            } else {
                openAppSettings(context)
            }
        }

    CardItemIcon(icon = painterResource(id = R.drawable.ic_videocam)) {
        val permissionCameraCheckResult =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

        val permissionVideoCheckResult =
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)

        if (permissionCameraCheckResult == PackageManager.PERMISSION_GRANTED &&
            permissionVideoCheckResult == PackageManager.PERMISSION_GRANTED
        ) {
            scope.launch {
                showVideoLauncher = true
                state.expand()
            }
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
            )
        }
    }
    AnimatedVisibility(visible = showVideoLauncher) {
        ModalBottomSheet(onDismissRequest = {
            showVideoLauncher = false
        }, sheetState = state) {
            VideoLauncherV2(
                modifier = Modifier.fillMaxSize(),
            ) { uri ->
                onComplete(uri)
                showVideoLauncher = false
            }
        }
    }
}

private fun checkPermission(
    context: Context,
    permission: String,
): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) ==
        PackageManager.PERMISSION_GRANTED
}
