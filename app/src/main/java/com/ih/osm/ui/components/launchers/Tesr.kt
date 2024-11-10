package com.ih.osm.ui.components.launchers

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ih.osm.ui.components.buttons.CustomButton
import java.io.File

@SuppressLint("CheckResult", "MissingPermission")
@Composable
fun CameraPreview(modifier: Modifier = Modifier) {
    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var recording by remember { mutableStateOf<Recording?>(null) }
    val context = LocalContext.current
    var showStopButton by remember { mutableStateOf(false) }
    // val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.VIDEO_CAPTURE)
        }
    }
    // Create an AndroidView to display the camera preview
    Box(modifier = modifier.padding(20.dp)) {
        Column {
            if (showStopButton) {
                CustomButton(text = "Stop") {
                    recording?.stop()
                    recording = null
                    showStopButton = false
                }
            }

            CustomButton(text = "Record") {
                showStopButton = true
                if (recording != null) {
                    recording?.stop()
                    recording = null
                    return@CustomButton
                }

                val outputFile = File(context.filesDir, "my-record-video.mp4")
                recording = controller.startRecording(
                    FileOutputOptions.Builder(outputFile).build(),
                    AudioConfig.create(true),
                    ContextCompat.getMainExecutor(context)
                ) { event ->
                    when (event) {
                        is VideoRecordEvent.Start -> {
                            Log.e("test", "VideoRecordEvent start")
                        }
                        is VideoRecordEvent.Pause -> {
                            Log.e("test", "VideoRecordEvent pause")
                        }
                        is VideoRecordEvent.Resume -> {
                            Log.e("test", "VideoRecordEvent resume")
                        }
                        is VideoRecordEvent.Finalize -> {
                            Log.e("test", "VideoRecordEvent finalize")

                            if (event.hasError()) {
                                recording?.close()
                                recording = null
                                Log.e(
                                    "test",
                                    "VideoRecordEvent Finalize with errors " +
                                        "${event?.cause} -- ${event?.error}"
                                )
                            } else {
                                Log.e("test", "VideoRecordEvent Finalize good")
                            }
                        }
                    }
                }

//                Log.e("test", "VideoRecording $videoCapture")
//
//                // Stop the current recording session if there is an ongoing recording
//                if (recording != null) {
//                    recording?.stop()
//                    recording = null
//                }
//                val appRelativePath = context.getExternalFilesDir(
//                    Environment.DIRECTORY_DCIM
//                )?.absolutePath
//
//                // Create ContentValues for saving video metadata
//                val contentValues = ContentValues().apply {
//                    put(MediaStore.MediaColumns.DISPLAY_NAME, "testrecordvideo")
//                    put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        // Scoped storage: Use the app's specific folder
//                        put(
//                            MediaStore.Video.Media.RELATIVE_PATH,
//                            "Movies/Osm"
//                        ) // Use this line for scoped storage
//                    } else {
//                        // For older Android versions, use the absolute path
//                        put(MediaStore.Video.Media.DATA, "$appRelativePath/test_video.mp4")
//                    }
//                }
//
//                // MediaStore output options
//                val mediaStoreOutputOptions = MediaStoreOutputOptions
//                    .Builder(context.contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
//                    .setContentValues(contentValues)
//                    .build()
//
//                // Start video recording
//                videoCapture?.output
//                    ?.prepareRecording(context, mediaStoreOutputOptions)
//                    ?.start(cameraExecutor) { recordEvent ->
//                        when (recordEvent) {
//                            is VideoRecordEvent.Start -> {
//                                Log.e("test", "VideoRecording 6")
//                                showStopButton = true
//                            }
//                            is VideoRecordEvent.Finalize -> {
//                                if (recordEvent.hasError()) {
//                                    Log.e(
//                                        "Test",
//                                        "Recording failed with error:" +
//                                            " ${recordEvent.error}"
//                                    )
//                                    recording?.close()
//                                    recording = null
//                                } else {
//                                    // Successfully finalized recording
//                                    val msg = "Video capture succeeded: " +
//                                        "${recordEvent.outputResults.outputUri}"
//                                    Log.e("Test", msg)
//                                }
//                            }
//                        }
//                    }
            }
            val life = LocalLifecycleOwner.current
            AndroidView(
                factory = { context ->
                    PreviewView(context).apply {
                        this.controller = controller
                        controller.bindToLifecycle(life)
                    }

//                    // Create a PreviewView instance for CameraX
//                    val previewView = PreviewView(context)
//
//                    // Start camera once PreviewView is ready
//                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
//                    cameraProviderFuture.addListener({
//                        // Get the camera provider
//                        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//
//                        // Create a Preview instance and link it to the PreviewView
//                        val preview = Preview.Builder()
//                            .build()
//                            .also {
//                                it.surfaceProvider = previewView.surfaceProvider
//                            }
//
//                        // Initialize VideoCapture
//                        val recorder = Recorder.Builder().build()
//                        videoCapture = VideoCapture.withOutput(recorder)
//
//                        // Select the default back camera
//                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//                        try {
//                            // Unbind any previous use cases
//                            cameraProvider.unbindAll()
//
//                            // Bind the use cases to the camera
//                            cameraProvider.bindToLifecycle(
//                                context as LifecycleOwner,
//                                cameraSelector,
//                                preview,
//                                videoCapture
//                            )
//                        } catch (exc: Exception) {
//                            Log.e("Camera", "Use case binding failed", exc)
//                        }
//                    }, ContextCompat.getMainExecutor(context))
//                    previewView
                },
                modifier = Modifier.fillMaxWidth().height(500.dp).padding(top = 200.dp)
            )
        }
    }

    // Clean up the executor when the composable is disposed
//    DisposableEffect(Unit) {
//        onDispose {
//            cameraExecutor.shutdown()
//        }
//    }
}
