package com.ih.m2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ih.m2.ui.extensions.getColor

@Composable
fun CustomTag(
    title: String
) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
            )
            .padding(vertical = 2.dp, horizontal = 6.dp)
    ) {
        Text(
            text = title, style = MaterialTheme.typography.bodyLarge
                .copy(
                    color = getColor()
                )
        )
    }
}