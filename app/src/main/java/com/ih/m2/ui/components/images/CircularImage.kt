package com.ih.m2.ui.components.images

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.ih.m2.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CircularImage(
    image: String,
    size: Dp = 54.dp
) {
    GlideImage(
        model = image,
        contentDescription = stringResource(id = R.string.empty),
        modifier = Modifier
            .size(size)
            .clip(CircleShape),
        failure = placeholder(R.drawable.ic_launcher_background),
        loading = placeholder(R.drawable.ic_launcher_background),
    )
}