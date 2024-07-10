package com.ih.m2.ui.pages.cardlist

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ih.m2.domain.model.Card
import com.ih.m2.ui.components.CustomAppBar
import com.ih.m2.ui.components.ScreenLoading
import com.ih.m2.ui.extensions.defaultScreen
import com.ih.m2.ui.navigation.navigateToCardDetail
import com.ih.m2.ui.pages.home.components.CardItemList
import com.ih.m2.ui.theme.M2androidappTheme

@Composable
fun CardListScreen(
    navController: NavController,
    viewModel: CardListViewModel = mavericksViewModel(),
    filter: String,
) {
    val state by viewModel.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    if (state.isLoading) {
        ScreenLoading(text = state.message)
    } else {
        CardListContent(
            navController = navController,
            cards = state.cards,
            title = state.title
        )
    }

    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { state }
            .flowWithLifecycle(lifecycle)
            .collect {
                viewModel.process(CardListViewModel.Action.GetCards(filter))
            }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardListContent(navController: NavController, cards: List<Card>, title: String) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding),
        ) {
            stickyHeader {
                CustomAppBar(navController = navController, title = title)
            }
            items(cards) {
                CardItemList(
                    card = it,
                    onClick = {
                        navController.navigateToCardDetail(it.id)
                    },
                    onActionClick = {})
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
private fun CardListScreenScreenPreview() {
    M2androidappTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            CardListContent(
                navController = rememberNavController(),
                cards = emptyList(),
                title = "Cards"
            )
        }
    }
}