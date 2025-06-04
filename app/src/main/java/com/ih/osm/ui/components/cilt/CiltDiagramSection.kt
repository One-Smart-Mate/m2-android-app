package com.ih.osm.ui.components.cilt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.ih.osm.R
import com.ih.osm.ui.components.images.PreviewImage
import com.ih.osm.ui.pages.createcard.PhotoCardItem
import com.ih.osm.ui.theme.Size200
import com.ih.osm.ui.theme.Size250

@Composable
fun CiltDiagramSection(
    title: String = stringResource(R.string.machine_diagram),
    imageUrl: String?,
) {
    var openImage by remember { mutableStateOf(false) }

    if (!imageUrl.isNullOrBlank()) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                PhotoCardItem(
                    model = imageUrl,
                    showIcon = false,
                    modifier =
                        Modifier
                            .width(Size200)
                            .height(Size250)
                            .clickable {
                                openImage = true
                            },
                )
            }

            PreviewImage(openImage = openImage, model = imageUrl) {
                openImage = false
            }
        }
    }
}
