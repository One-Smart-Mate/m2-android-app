package com.osm.ui.extensions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.osm.ui.theme.PaddingNormal
import com.osm.ui.theme.PaddingTiny

@Composable
fun Modifier.headerContent(paddingTop: Dp): Modifier {
    return this
        .fillMaxWidth()
        .background(
            color = MaterialTheme.colorScheme.primary
        )
        .padding(start = PaddingNormal, end = PaddingNormal, top = paddingTop)
}


@Composable
fun Modifier.scaffold(): Modifier {
    return this
        .fillMaxSize()
}

@Composable
fun Modifier.defaultScreen(padding: PaddingValues): Modifier {
    return this
        .fillMaxSize()
        .padding(vertical = padding.calculateTopPadding(), horizontal = PaddingTiny)
}
