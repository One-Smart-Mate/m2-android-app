package com.ih.m2.ui.components.evidence

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
import com.ih.m2.R
import com.ih.m2.domain.model.Evidence
import com.ih.m2.ui.components.VideoPlayer
import com.ih.m2.ui.theme.Size200
import com.ih.m2.ui.theme.Size250

@Composable
fun SectionVideosEvidence(
    videoEvidences: List<Evidence>,
    onDeleteEvidence: (Evidence) -> Unit
) {
    if (videoEvidences.isNotEmpty()) {
        Column {
            Text(
                text = stringResource(R.string.videos),
                style = MaterialTheme.typography.titleLarge
                    .copy(fontWeight = FontWeight.Bold)
            )
            LazyRow {
                items(videoEvidences) {
                    VideoPlayer(
                        modifier = Modifier
                            .width(Size200)
                            .height(Size250),
                        url = it.url,
                        showIcon = true
                    ) {
                        onDeleteEvidence(it)
                    }
                }
            }
        }
    }
}