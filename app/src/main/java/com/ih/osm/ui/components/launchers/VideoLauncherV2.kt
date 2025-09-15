package com.ih.osm.ui.components.launchers

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.video.AudioConfig
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ih.osm.R
import com.ih.osm.core.ui.functions.FileType
import com.ih.osm.core.ui.functions.getUriForFile
import com.ih.osm.ui.theme.PaddingToolbarVertical
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CheckResult", "MissingPermission")
@Composable
fun VideoLauncherV2(
    modifier: Modifier = Modifier,
    onComplete: (uri: Uri) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var recording by remember { mutableStateOf<Recording?>(null) }
    val context = LocalContext.current
    var showStopButton by remember { mutableStateOf(false) }
    var fileInfo = context.getUriForFile(fileType = FileType.VIDEO)
    var uri = fileInfo.first
    var file = fileInfo.second
    val scope = rememberCoroutineScope()
    val timeCount =
        remember {
            mutableLongStateOf(0L)
        }

    val executeTimer =
        remember {
            mutableStateOf(false)
        }

    val controller =
        remember {
            LifecycleCameraController(context).apply {
                setEnabledUseCases(CameraController.VIDEO_CAPTURE)
            }
        }

    LaunchedEffect(executeTimer.value) {
        while (executeTimer.value) {
            delay(1000)
            timeCount.longValue++
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AndroidView(
            factory = { context ->
                PreviewView(context).apply {
                    this.controller = controller
                    controller.imageCaptureMode = ImageCapture.FLASH_MODE_AUTO
                    controller.videoCaptureQualitySelector = QualitySelector.from(Quality.SD)
                    controller.bindToLifecycle(lifecycleOwner)
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
        AnimatedVisibility(visible = showStopButton.not()) {
            Box(
                modifier =
                    Modifier
                        .padding(PaddingToolbarVertical)
                        .clickable {
                            showStopButton = true
                            executeTimer.value = true
                            if (recording != null) {
                                recording?.stop()
                                recording = null
                                return@clickable
                            }

                            recording =
                                controller.startRecording(
                                    FileOutputOptions
                                        .Builder(file)
                                        .build(),
                                    AudioConfig.create(true),
                                    ContextCompat.getMainExecutor(context),
                                ) { event ->
                                    when (event) {
                                        is VideoRecordEvent.Start -> {}
                                        is VideoRecordEvent.Pause -> {}
                                        is VideoRecordEvent.Resume -> {}
                                        is VideoRecordEvent.Finalize -> {
                                            if (event.hasError()) {
                                                recording?.close()
                                                recording = null
                                                Toast
                                                    .makeText(
                                                        context,
                                                        "Error: ${event.cause?.localizedMessage}",
                                                        Toast.LENGTH_SHORT,
                                                    ).show()
                                            } else {
                                                scope.launch {
                                                    Log.e(
                                                        "test",
                                                        "NewUri -> ${event.outputResults.outputUri}",
                                                    )
                                                    onComplete(event.outputResults.outputUri)
                                                    fileInfo =
                                                        context.getUriForFile(
                                                            fileType = FileType.VIDEO,
                                                        )
                                                    uri = fileInfo.first
                                                    file = fileInfo.second
                                                }
                                            }
                                        }
                                    }
                                }
                        },
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(70.dp)
                            .border(2.dp, Color.White, CircleShape)
                            .padding(10.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .padding(bottom = 50.dp),
                )
            }
        }
        AnimatedVisibility(visible = showStopButton) {
            Box(
                modifier =
                    Modifier
                        .padding(PaddingToolbarVertical)
                        .clickable {
                            showStopButton = false
                            executeTimer.value = false
                            recording?.stop()
                            recording = null
                        },
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(70.dp)
                            .border(2.dp, Color.White, CircleShape)
                            .padding(20.dp)
                            .clip(RectangleShape)
                            .background(Color.Red)
                            .padding(bottom = 50.dp),
                )
            }
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
            VideoRecordTime(time = timeCount.longValue)
        }
    }
}

@Composable
fun VideoRecordTime(time: Long) {
    Column(
        modifier =
            Modifier
                .padding(top = 30.dp)
                .padding(10.dp)
                .background(Color.Black, shape = RoundedCornerShape(8.dp)),
    ) {
        Text(
            "${stringResource(R.string.record_video)}: ${time.formatTime()}",
            style =
                MaterialTheme
                    .typography.bodyMedium
                    .copy(color = Color.White),
            modifier = Modifier.padding(10.dp),
        )
    }
}

@SuppressLint("DefaultLocale")
fun Long.formatTime(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val remainingSeconds = this % 60
    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}
