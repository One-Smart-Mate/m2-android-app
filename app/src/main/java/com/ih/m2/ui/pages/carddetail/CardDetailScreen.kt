package com.ih.m2.ui.pages.carddetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.ih.m2.ui.components.CustomAppBar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardDetailScreen(
    navController: NavController
) {
    LazyColumn {
        stickyHeader {
            CustomAppBar(navController = navController, title = "Card 1")
        }
    }
}