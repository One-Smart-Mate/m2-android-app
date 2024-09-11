package com.ih.osm.ui.components.card

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.ih.osm.R
import com.ih.osm.domain.model.EvidenceType
import com.ih.osm.ui.components.launchers.CameraLauncher
import com.ih.osm.ui.components.launchers.VideoLauncher
import com.ih.osm.ui.components.sheets.RecordAudioBottomSheet
import com.ih.osm.ui.pages.createcard.CardItemIcon

@Composable
fun SectionCardEvidence(
    audioDuration: Int,
    onAddEvidence: (Uri, EvidenceType) -> Unit,
    imageType: EvidenceType,
    audioType: EvidenceType,
    videoType: EvidenceType
) {

    var audioBottomSheet by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {

        CameraLauncher {
            onAddEvidence(it, imageType)
        }
        VideoLauncher {
            onAddEvidence(it, videoType)
        }
        if (audioDuration > 0) {
            CardItemIcon(icon = painterResource(id = R.drawable.ic_voice)) {
                audioBottomSheet = true
            }
        }

        if (audioBottomSheet) {
            RecordAudioBottomSheet(
                onComplete = {
                    audioBottomSheet = false
                    onAddEvidence(it, audioType)
                },
                onDismissRequest = {
                    audioBottomSheet = false
                },
                maxRecord = audioDuration
            )
        }
    }
}