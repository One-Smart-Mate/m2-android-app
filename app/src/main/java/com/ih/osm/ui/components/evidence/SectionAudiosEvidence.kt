package com.ih.osm.ui.components.evidence

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.ih.osm.R
import com.ih.osm.domain.model.Evidence
import com.ih.osm.ui.components.VideoPlayer
import com.ih.osm.ui.theme.Size120
import com.ih.osm.ui.theme.Size160

@Composable
fun SectionAudiosEvidence(
    audioEvidences: List<Evidence>,
    onDeleteEvidence: (Evidence) -> Unit,
) {
    AnimatedVisibility(visible = audioEvidences.isNotEmpty()) {
        Column {
            Text(
                text = stringResource(R.string.audios),
                style =
                    MaterialTheme.typography.titleLarge
                        .copy(fontWeight = FontWeight.Bold),
            )
            LazyRow {
                items(audioEvidences) {
                    VideoPlayer(
                        modifier =
                            Modifier
                                .width(Size120)
                                .height(Size160),
                        url = it.url,
                        showIcon = true,
                    ) {
                        onDeleteEvidence(it)
                    }
                }
            }
        }
    }
}
