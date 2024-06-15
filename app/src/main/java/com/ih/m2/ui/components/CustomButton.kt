package com.ih.m2.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.fillMaxWidth().height(56.dp),
        onClick = onClick
    ) {
        Text(text)
    }
}