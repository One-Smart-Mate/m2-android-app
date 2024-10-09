package com.ih.osm.ui.components.launchers

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.ih.osm.R
import com.ih.osm.core.ui.functions.FileType
import com.ih.osm.core.ui.functions.getUriForFile
import com.ih.osm.core.ui.functions.openAppSettings
import com.ih.osm.ui.pages.createcard.CardItemIcon
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.utils.AndroidAudioRecorder
import kotlinx.coroutines.delay

@Composable
fun AudioLauncher(maxRecordTime: Int, onComplete: (uri: Uri) -> Unit) {
    val context = LocalContext.current
    var file = context.getUriForFile(FileType.AUDIO)
    val androidAudioPlayer = AndroidAudioRecorder(context)

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                androidAudioPlayer.start(file.second)
            } else {
                openAppSettings(context)
            }
        }

    AudioContent(
        onStart = {
            val permissionCheckResult =
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                androidAudioPlayer.start(file.second)
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        },
        onStop = {
            androidAudioPlayer.stop()
            onComplete(file.first)
            file = context.getUriForFile(FileType.AUDIO)
        },
        maxRecordTime = maxRecordTime
    )
}

@Composable
fun AudioContent(maxRecordTime: Int, onStart: () -> Unit, onStop: () -> Unit) {
    var timeLeft by remember { mutableIntStateOf(maxRecordTime) }
    var start by remember { mutableStateOf(false) }

    LaunchedEffect(start) {
        if (start) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            if (timeLeft == 0) {
                onStop()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.recording_time, timeLeft),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            CardItemIcon(icon = painterResource(id = R.drawable.ic_smart_display)) {
                start = true
                onStart()
            }

            CardItemIcon(icon = painterResource(id = R.drawable.ic_stop)) {
                start = false
                timeLeft = maxRecordTime
                onStop()
            }
        }
    }
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
fun AudioContentPreview() {
    OsmAppTheme {
        Surface {
            AudioContent(maxRecordTime = 10, onStart = {}, onStop = {})
        }
    }
}
