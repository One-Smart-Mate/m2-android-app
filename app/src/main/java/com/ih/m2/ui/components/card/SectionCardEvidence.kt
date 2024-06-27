package com.ih.m2.ui.components.card

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
import com.ih.m2.R
import com.ih.m2.domain.model.EvidenceType
import com.ih.m2.ui.components.launchers.CameraLauncher
import com.ih.m2.ui.components.launchers.VideoLauncher
import com.ih.m2.ui.components.sheets.RecordAudioBottomSheet
import com.ih.m2.ui.pages.createcard.CardItemIcon

@Composable
fun SectionCardEvidence(
    audioDuration: Int,
    onAddEvidence: (Uri, EvidenceType) -> Unit
) {

    var audioBottomSheet by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {

        CameraLauncher {
            onAddEvidence(it, EvidenceType.IMCR)
        }
        VideoLauncher {
            onAddEvidence(it, EvidenceType.VICR)
        }
        CardItemIcon(icon = painterResource(id = R.drawable.ic_voice)) {
            audioBottomSheet = true
        }

        if (audioBottomSheet) {
            RecordAudioBottomSheet(
                onComplete = {
                    audioBottomSheet = false
                    onAddEvidence(it, EvidenceType.AUCR)
                },
                onDismissRequest = {
                    audioBottomSheet = false
                },
                maxRecord = audioDuration
            )
        }
    }
}