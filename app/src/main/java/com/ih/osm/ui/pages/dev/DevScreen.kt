package com.ih.osm.ui.pages.dev

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.ih.osm.core.ui.functions.FileType
import com.ih.osm.core.ui.functions.getUriForFile

@Composable
fun DevScreen(navController: NavController) {
    Column {
//        CustomSpacer()
//        CustomSpacer()
//        ModernAudioRecorderComponent(LocalContext.current)
        Text("Components under development")
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CameraScreenTest() {
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    // Register the result for taking the photo
    val takePhotoLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let {
                    Log.e("test", "Dataa -> $it")
                    photoUri = it // Capture the URI of the photo
                }
            }
        }

    // Register the camera permission request
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) {
                // Permissions granted, launch camera
                openCamera(
                    context,
                    takePhotoLauncher,
                )
            } else {
                // Show an error or snackbar that permission is denied
                println("Permission Denied")
            }
        }

    Scaffold(
        topBar = { Text("Capture Photo") },
        content = {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = {
                        // Request permission before launching camera
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA,
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            // Request camera permission
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        } else {
                            // Launch camera if permissions are already granted
                            openCamera(context, takePhotoLauncher)
                        }
                    },
                ) {
                    Text("Take Photo")
                }

                // Show the URI of the captured photo
                photoUri?.let {
                    Text("Captured Photo URI: $it")
                }
            }
        },
    )
}

private fun openCamera(
    context: Context,
    takePhotoLauncher: ActivityResultLauncher<Intent>,
) {
    val intent =
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, context.getUriForFile(FileType.IMAGE).first)
        }
    takePhotoLauncher.launch(intent)
}
