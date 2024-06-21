package com.ih.m2.ui.pages.dev

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings.Global
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner

import com.google.firebase.storage.FirebaseStorage
import com.ih.m2.core.ui.functions.createAudioFile
import com.ih.m2.core.ui.functions.openAppSettings
import com.ih.m2.ui.components.VideoPlayer
import com.ih.m2.ui.components.launchers.AudioContent
import com.ih.m2.ui.components.launchers.AudioLauncher
import com.ih.m2.ui.components.launchers.CameraLauncher
import com.ih.m2.ui.components.launchers.VideoLauncher
import com.ih.m2.ui.extensions.defaultScreen
import com.ih.m2.ui.utils.AndroidAudioPlayer
import com.ih.m2.ui.utils.AndroidAudioRecorder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.coroutineContext


@Composable
fun DevScreen() {


    Scaffold { padding ->
        LazyRow(
            modifier = Modifier.defaultScreen(padding)
        ) {
            item {
                CameraLauncher {
                    Log.e("Test","Result uri image $it")
                    GlobalScope.launch {
                        uploadEvidence(it)
                    }
                }

                VideoLauncher(videoLimitDuration = 10) {
                    Log.e("Test","Result uri video $it")
                    GlobalScope.launch {
                        uploadEvidenceVideo(it)
                    }
                }


                AudioLauncher(
                    10
                ) {
                    Log.e("Test","Result uri audio $it")
                    GlobalScope.launch {
                        uploadEvidenceAudio(it)
                    }
                }
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
    Log.e("test","Result image ${url}")
}


suspend fun uploadEvidenceVideo(uri: Uri) {
    val store = FirebaseStorage.getInstance()
    val name = getVideoFile()
    val ref = store.reference.child("evidence/created/videos/1/$name")
    ref.putFile(uri).await()
    val url = ref.downloadUrl.await()
    Log.e("test","Result video ${url}")
}

suspend fun uploadEvidenceAudio(uri: Uri) {
    val store = FirebaseStorage.getInstance()
    val name = getAudioFile()
    val ref = store.reference.child("evidence/created/audios/1/$name")
    ref.putFile(uri).await()
    val url = ref.downloadUrl.await()
    Log.e("test","Result audio ${url}")
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