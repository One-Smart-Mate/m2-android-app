package com.ih.osm.ui.components.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.ih.osm.domain.model.Evidence
import com.ih.osm.ui.components.VideoPlayer
import com.ih.osm.ui.theme.PaddingTiny
import com.ih.osm.ui.theme.Size120

@Composable
fun CardAudioSection(
    title: String,
    evidences: List<Evidence>,
) {
    Column {
        if (evidences.isNotEmpty()) {
            Text(
                text = title,
                style =
                    MaterialTheme.typography.titleLarge
                        .copy(fontWeight = FontWeight.Bold),
            )
        }
        LazyRow {
            items(evidences) {
                VideoPlayer(
                    modifier =
                        Modifier
                            .size(Size120)
                            .padding(PaddingTiny),
                    url = it.url,
                )
            }
        }
    }
}
