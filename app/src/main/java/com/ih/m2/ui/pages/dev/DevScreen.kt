package com.ih.m2.ui.pages.dev

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ih.m2.ui.components.VideoPlayer

@Composable
fun DevScreen() {
    VideoPlayer(
        Modifier
            .fillMaxWidth()
            .height(100.dp),
        "https://firebasestorage.googleapis.com/v0/b/maut-c62d6.appspot.com/o/evidence%2Fcreated%2Fvideos%2FVIDEO_20211017_193456.mp4?alt=media&token=b756f840-8154-459e-94e9-a5ce68cfed54"
    )
}