package com.ih.osm.ui.pages.dev

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DevScreen(navController: NavController) {
    ScrollableTitleScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollableTitleScreen() {
    val scrollState = rememberLazyListState() // Tracks scroll position
    val appBarHeight = 56.dp
    val maxOffset = with(LocalDensity.current) { 32.dp.toPx() } // Adjust the collapse threshold

    // Derived state for scroll offset (1f is fully collapsed, 0f is fully expanded)
    val scrollOffset by remember {
        derivedStateOf {
            min(1f, max(0f, scrollState.firstVisibleItemScrollOffset / maxOffset))
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Collapsible Title",
                        fontSize = lerp(24.sp, 18.sp, scrollOffset),
                        maxLines = 1,
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White
                ),
                modifier = Modifier.heightIn(min = appBarHeight, max = appBarHeight)
            )
        },
        content = { padding ->
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(top = appBarHeight)
            ) {
                items(50) { index ->
                    ListItem(
                        headlineContent = { Text("Item #$index") }
                    )
                }
            }
        }
    )
}
