package com.ih.osm.ui.pages.dev

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi

import com.google.firebase.storage.FirebaseStorage
import com.ih.osm.core.notifications.NotificationManager
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.pages.createcard.PhotoCardItem
import com.ih.osm.ui.theme.Size200
import com.ih.osm.ui.theme.Size250
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DevScreen() {

    var showButton by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val notificationManager = NotificationManager(context)
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

                    }
                }

                Column {

                    Button(onClick = {
                        notificationManager.buildNotification("test","test")
                    }) {
                        Text( "Test Push")
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