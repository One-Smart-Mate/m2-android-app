package com.ih.osm.ui.components.evidence

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ih.osm.ui.components.VideoPlayer
import com.ih.osm.ui.components.buttons.CustomIconButton
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.utils.EMPTY

@Composable
fun PreviewVideo(
    openVideo: Boolean,
    url: String,
    onDismissClick: () -> Unit,
) {
    if (openVideo) {
        Dialog(
            onDismissRequest = {
                onDismissClick()
            },
            properties =
                DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false,
                ),
        ) {
            Box {
                VideoPlayer(
                    modifier = Modifier.fillMaxSize(),
                    url = url,
                )
                Box(modifier = Modifier.padding(horizontal = PaddingNormal)) {
                    CustomIconButton(text = EMPTY, icon = Icons.Outlined.Close) {
                        onDismissClick()
                    }
                }
            }
        }
    }
}
