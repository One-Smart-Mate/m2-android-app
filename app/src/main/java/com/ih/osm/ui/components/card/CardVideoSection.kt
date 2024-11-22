package com.ih.osm.ui.components.card

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
import androidx.compose.ui.text.font.FontWeight
import com.ih.osm.domain.model.Evidence
import com.ih.osm.ui.components.evidence.PreviewVideo
import com.ih.osm.ui.pages.createcard.PhotoCardItem
import com.ih.osm.ui.theme.Size200
import com.ih.osm.ui.theme.Size250
import com.ih.osm.ui.utils.EMPTY

@Composable
fun CardVideoSection(title: String, evidences: List<Evidence>) {
    var videoUrl by remember {
        mutableStateOf(EMPTY)
    }
    var openVideo by remember {
        mutableStateOf(false)
    }
    Column {
        Text(
            text = title,
            style =
            MaterialTheme.typography.titleLarge
                .copy(fontWeight = FontWeight.Bold)
        )
        LazyRow {
            items(evidences) {
                PhotoCardItem(
                    model = it.url,
                    showIcon = false,
                    modifier =
                    Modifier
                        .width(Size200)
                        .height(Size250)
                        .clickable {
                            videoUrl = it.url
                            openVideo = true
                        }
                )
            }
        }
        PreviewVideo(openVideo = openVideo, url = videoUrl) {
            videoUrl = EMPTY
            openVideo = false
        }
    }
}
