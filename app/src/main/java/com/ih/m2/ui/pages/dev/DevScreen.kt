package com.ih.m2.ui.pages.dev

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings.Global
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

import com.google.firebase.storage.FirebaseStorage
import com.ih.m2.MainActivity
import com.ih.m2.R
import com.ih.m2.core.ui.functions.createAudioFile
import com.ih.m2.core.ui.functions.openAppSettings
import com.ih.m2.ui.components.VideoPlayer
import com.ih.m2.ui.components.buttons.CustomButton
import com.ih.m2.ui.components.buttons.CustomIconButton
import com.ih.m2.ui.components.images.PreviewImage
import com.ih.m2.ui.components.launchers.AudioContent
import com.ih.m2.ui.components.launchers.AudioLauncher
import com.ih.m2.ui.components.launchers.CameraLauncher
import com.ih.m2.ui.components.launchers.VideoLauncher
import com.ih.m2.ui.components.sheets.RecordAudioBottomSheet
import com.ih.m2.ui.extensions.defaultScreen
import com.ih.m2.ui.extensions.getActivity
import com.ih.m2.ui.pages.createcard.CardItemIcon
import com.ih.m2.ui.pages.createcard.PhotoCardItem
import com.ih.m2.ui.theme.Size200
import com.ih.m2.ui.theme.Size250
import com.ih.m2.ui.utils.AndroidAudioPlayer
import com.ih.m2.ui.utils.AndroidAudioRecorder
import com.ih.m2.ui.utils.EMPTY
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.coroutineContext


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DevScreen() {

    var showButton by remember {

        mutableStateOf(false)
    }
    val context = LocalContext.current
    Scaffold { padding ->
        LazyRow(
            modifier = Modifier.defaultScreen(padding)
        ) {
            item {

                PhotoCardItem(
                    model = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTABbXr4i-QODqhy7tofHYmTYh05rYPktzacw&s",
                    showIcon = false,
                    modifier = Modifier
                        .width(Size200)
                        .height(Size250)
                        .clickable {
                            showButton = true
                        }
                )

                if (showButton) {
                    Dialog(
                        onDismissRequest = {
                            showButton = false
                        },
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = false,
                            usePlatformDefaultWidth = false
                        )
                    ) {
                        VideoPlayer(
                            modifier = Modifier.fillMaxSize(),
                            url = "https://firebasestorage.googleapis.com/v0/b/android-m2-app.appspot.com/o/evidence%2Fcreated%2Fvideos%2F1c6e286c-a897-4504-aed6-d62659996ef1%2FVIDEO_CR_20240707_012558.mp4?alt=media&token=b077ec04-7a05-4f86-a60f-9ca6b53a8ee1"
                        )
                    }
                }

//               PreviewImage(openImage = showButton,
//                   model = "https://firebasestorage.googleapis.com/v0/b/android-m2-app.appspot.com/o/evidence%2Fcreated%2Fimages%2F1c6e286c-a897-4504-aed6-d62659996ef1%2FIMAGE_CR_20240707_012554.jpg?alt=media&token=10c35a28-cc22-4415-9b1d-1f7b67a559ad") {
//                   showButton = false
//               }
//                CameraLauncher {
//                    Log.e("Test", "Result uri image $it")
//                    GlobalScope.launch {
//                        uploadEvidence(it)
//                    }
//                }
//
//                VideoLauncher(videoLimitDuration = 10) {
//                    Log.e("Test", "Result uri video $it")
//                    GlobalScope.launch {
//                        uploadEvidenceVideo(it)
//                    }
//                }
//
//                CardItemIcon(icon = painterResource(id = R.drawable.ic_voice)) {
//                    showButton = true
//                }
//
//                if (showButton) {
//                    RecordAudioBottomSheet(
//                        onComplete = {
//                            showButton = false
//                        },
//                        onDismissRequest = {
//                            showButton = false
//                        })
//                }
            }
        }
    }
}

suspend fun uploadEvidence(uri: Uri) {
    val store = FirebaseStorage.getInstance()
    val name = getImageFile()
    val ref = store.reference.child("evidence/created/images/1/$name")
    ref.putFile(uri).await()
    val url = ref.downloadUrl.await()
    Log.e("test", "Result image ${url}")
}


suspend fun uploadEvidenceVideo(uri: Uri) {
    val store = FirebaseStorage.getInstance()
    val name = getVideoFile()
    val ref = store.reference.child("evidence/created/videos/1/$name")
    ref.putFile(uri).await()
    val url = ref.downloadUrl.await()
    Log.e("test", "Result video ${url}")
}

suspend fun uploadEvidenceAudio(uri: Uri) {
    val store = FirebaseStorage.getInstance()
    val name = getAudioFile()
    val ref = store.reference.child("evidence/created/audios/1/$name")
    ref.putFile(uri).await()
    val url = ref.downloadUrl.await()
    Log.e("test", "Result audio ${url}")
}

fun getImageFile(): String {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return "IMAGE_${timeStamp}.jpg"
}

fun getVideoFile(): String {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return "VIDEO_${timeStamp}.mp4"
}

fun getAudioFile(): String {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return "AUDIO_${timeStamp}.mp3"
}