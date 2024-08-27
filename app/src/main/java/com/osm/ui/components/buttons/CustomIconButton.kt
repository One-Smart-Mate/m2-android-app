package com.osm.ui.components.buttons

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import com.osm.ui.utils.EMPTY


@Composable
fun CustomIconButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val color = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.onSecondary
    } else {
        MaterialTheme.colorScheme.onPrimary
    }
    Button(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = color,
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = EMPTY)
            Text(text = text)
        }
    }
}

