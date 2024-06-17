package com.ih.m2.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.ih.m2.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CircularImage(
    image: String,
) {
    GlideImage(
        model = image,
        contentDescription = "",
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape),
        failure = placeholder(R.drawable.ic_launcher_background),
        loading = placeholder(R.drawable.ic_launcher_background),
    )
}