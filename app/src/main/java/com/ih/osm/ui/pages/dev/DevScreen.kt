package com.ih.osm.ui.pages.dev

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ih.osm.ui.components.launchers.CameraPreview

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DevScreen(navController: NavController) {
    Scaffold { paddingValues ->
//        LazyColumn(
//            modifier = Modifier.defaultScreen(paddingValues)
//        ) {
//            stickyHeader {
//                CustomAppBar(
//                    navController = navController,
//                    title = "Developer section"
//                )
//            }
//            item {
//                ListItem(
//                    headlineContent = {
//                        Text("App Url Service -> ${BuildConfig.SERVICE_URL}")
//                    }
//                )
//            }
//        }
        CameraPreview(modifier = Modifier.fillMaxSize())
    }
}
