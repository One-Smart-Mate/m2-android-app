package com.ih.osm.ui.components.images

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.ih.osm.ui.components.buttons.CustomIconButton
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.utils.EMPTY

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PreviewImage(
    openImage: Boolean,
    model: String,
    onDismissClick: () -> Unit,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    if (openImage) {
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
                GlideImage(
                    model = model,
                    contentDescription = EMPTY,
                    modifier =
                        Modifier
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    scale *= zoom
                                    scale = scale.coerceIn(0.5f, 3f)
                                    offset =
                                        if (scale == 1f) Offset(0f, 0f) else offset + pan
                                }
                            }.graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y,
                            ).fillMaxSize(),
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
