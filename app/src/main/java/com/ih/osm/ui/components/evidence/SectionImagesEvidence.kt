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
import com.ih.osm.ui.components.images.PreviewImage
import com.ih.osm.ui.pages.createcard.PhotoCardItem
import com.ih.osm.ui.theme.Size200
import com.ih.osm.ui.theme.Size250

@Composable
fun SectionImagesEvidence(
    imageEvidences: List<Evidence>,
    onDeleteEvidence: (Evidence) -> Unit,
) {
    var openImage by remember { mutableStateOf<Pair<Boolean, String?>>(false to null) }

    AnimatedVisibility(visible = imageEvidences.isNotEmpty()) {
        Column {
            Text(
                text = stringResource(R.string.images),
                style =
                    MaterialTheme.typography.titleLarge
                        .copy(fontWeight = FontWeight.Bold),
            )
            LazyRow {
                items(imageEvidences) { evidence ->
                    PhotoCardItem(
                        model = evidence.url,
                        showIcon = true,
                        modifier =
                            Modifier
                                .width(Size200)
                                .height(Size250)
                                .clickable {
                                    openImage = true to evidence.url
                                },
                    ) {
                        onDeleteEvidence(evidence)
                    }
                }
            }

            openImage.first.takeIf { it }?.let {
                PreviewImage(
                    openImage = openImage.first,
                    model = openImage.second ?: "",
                    onDismissClick = { openImage = false to null },
                )
            }
        }
    }
}
