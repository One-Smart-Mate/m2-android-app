package com.ih.osm.ui.extensions

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun getColor(): Color {
    return if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.onSecondary
    } else {
        MaterialTheme.colorScheme.onPrimary
    }
}

@Composable
fun getTextColor(): Color {
    return if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }
}

@Composable
fun getIconColor(): Color {
    return if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }
}

@Composable
fun getPrimaryColor(): Color {
    return MaterialTheme.colorScheme.primary
}

@Composable
fun getInvertedColor(): Color {
    return if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.primary
    }
}
