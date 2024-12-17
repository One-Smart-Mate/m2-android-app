package com.ih.osm.ui.components.sheets

import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import com.ih.osm.ui.components.launchers.AudioLauncher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordAudioBottomSheet(
    onComplete: (Uri) -> Unit,
    onDismissRequest: () -> Unit,
    maxRecord: Int = 60,
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        AudioLauncher(
            maxRecord,
        ) {
            onComplete(it)
        }
    }
}
