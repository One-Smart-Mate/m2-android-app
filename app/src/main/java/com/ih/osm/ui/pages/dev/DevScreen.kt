package com.ih.osm.ui.pages.dev

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ih.osm.ui.extensions.defaultScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DevScreen(navController: NavController) {
    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier.defaultScreen(paddingValues)
        ) {
            item {
            }
        }
//        CameraPreview(modifier = Modifier.fillMaxSize()) {
//        }
    }
}
