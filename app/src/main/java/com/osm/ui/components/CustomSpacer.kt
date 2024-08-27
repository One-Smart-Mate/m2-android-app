package com.osm.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomSpacer(
    direction: SpacerDirection = SpacerDirection.VERTICAL,
    space: SpacerSize = SpacerSize.NORMAL
) {
    val size = when (space) {
        SpacerSize.TINY -> 6.dp
        SpacerSize.SMALL -> 12.dp
        SpacerSize.NORMAL -> 16.dp
        SpacerSize.LARGE -> 24.dp
        SpacerSize.EXTRA_LARGE -> 32.dp
    }

    val modifier = when (direction) {
        SpacerDirection.VERTICAL -> Modifier.height(size)
        SpacerDirection.HORIZONTAL -> Modifier.width(size)
    }

    Spacer(modifier = modifier)
}

enum class SpacerDirection {
    VERTICAL, HORIZONTAL
}

enum class SpacerSize {
    TINY,SMALL, NORMAL, LARGE, EXTRA_LARGE
}