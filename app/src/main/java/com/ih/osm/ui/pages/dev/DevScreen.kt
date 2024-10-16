package com.ih.osm.ui.pages.dev

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ih.osm.BuildConfig
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.extensions.defaultScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DevScreen(navController: NavController) {
    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier.defaultScreen(paddingValues)
        ) {
            stickyHeader {
                CustomAppBar(
                    navController = navController,
                    title = "Developer section"
                )
            }
            item {
                ListItem(
                    headlineContent = {
                        Text("App Url Service -> ${BuildConfig.SERVICE_URL}")
                    }
                )
            }
        }
    }
}
