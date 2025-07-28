package com.ih.osm.ui.components.evidence

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.ih.osm.R
import com.ih.osm.domain.model.Evidence
import com.ih.osm.ui.components.VideoPlayer
import com.ih.osm.ui.theme.Size200
import com.ih.osm.ui.theme.Size250

@Composable
fun SectionVideosEvidence(
    videoEvidences: List<Evidence>,
    onDeleteEvidence: (Evidence) -> Unit,
) {
    var openVideo by remember { mutableStateOf<Pair<Boolean, String?>>(false to null) }

    AnimatedVisibility(visible = videoEvidences.isNotEmpty()) {
        Column {
            Text(
                text = stringResource(R.string.videos),
                style =
                    MaterialTheme.typography.titleLarge
                        .copy(fontWeight = FontWeight.Bold),
            )
            LazyRow {
                items(videoEvidences) { evidence ->
                    VideoPlayer(
                        modifier =
                            Modifier
                                .width(Size200)
                                .height(Size250)
                                .clickable {
                                    openVideo = true to evidence.url
                                },
                        url = evidence.url,
                        showIcon = true,
                    ) {
                        onDeleteEvidence(evidence)
                    }
                }
            }

            openVideo.first.takeIf { it }?.let {
                PreviewVideo(
                    openVideo = openVideo.first,
                    url = openVideo.second ?: "",
                    onDismissClick = { openVideo = false to null },
                )
            }
        }
    }
}
