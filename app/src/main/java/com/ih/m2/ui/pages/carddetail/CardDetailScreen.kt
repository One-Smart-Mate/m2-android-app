package com.ih.m2.ui.pages.carddetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ih.m2.ui.components.CustomAppBar
import com.ih.m2.ui.extensions.defaultScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardDetailScreen(
    navController: NavController
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding)
        ) {
            stickyHeader {
                CustomAppBar(navController = navController, title = "Card 1")
            }
        }
    }
}