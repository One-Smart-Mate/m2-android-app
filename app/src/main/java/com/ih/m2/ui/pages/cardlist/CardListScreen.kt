package com.ih.m2.ui.pages.cardlist

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ih.m2.R
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.enableDefinitiveSolution
import com.ih.m2.domain.model.enableProvisionalSolution
import com.ih.m2.ui.components.CustomAppBar
import com.ih.m2.ui.components.CustomSpacer
import com.ih.m2.ui.components.EmptyData
import com.ih.m2.ui.components.LoadingScreen
import com.ih.m2.ui.components.PullToRefreshLazyColumn
import com.ih.m2.ui.components.SpacerSize
import com.ih.m2.ui.components.sheets.SolutionBottomSheet
import com.ih.m2.ui.extensions.defaultIfNull
import com.ih.m2.ui.extensions.defaultScreen
import com.ih.m2.ui.navigation.navigateToCardDetail
import com.ih.m2.ui.navigation.navigateToCardSolution
import com.ih.m2.ui.components.card.CardItemList
import com.ih.m2.ui.components.sheets.FiltersBottomSheet
import com.ih.m2.ui.components.sheets.FiltersBottomSheetV2
import com.ih.m2.ui.navigation.navigateToCreateCard
import com.ih.m2.ui.theme.M2androidappTheme
import com.ih.m2.ui.utils.EMPTY

@Composable
fun CardListScreen(
    navController: NavController,
    viewModel: CardListViewModel = mavericksViewModel(),
    filter: String,
) {
    val state by viewModel.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    if (state.isLoading) {
        LoadingScreen(text = state.message)
    } else {
        CardListContent(
            navController = navController,
            cards = state.cards,
            title = state.title,
            onSolutionClick = { card, solutionType ->
                viewModel.process(CardListViewModel.Action.OnRefreshCards)
                navController.navigateToCardSolution(solutionType, card.id)
            },
            onCreateCardClick = {
                viewModel.process(CardListViewModel.Action.OnRefreshCards)
                navController.navigateToCreateCard(filter)
            },
            onFilterChange = {
                viewModel.process(CardListViewModel.Action.OnFilterChange(it))
            },
            onClickApply = {
                viewModel.process(CardListViewModel.Action.OnApplyFilterClick)
            },
            isRefreshing = state.isRefreshing,
            onRefresh = {
                viewModel.process(CardListViewModel.Action.OnRefreshCardList)
            }
        )
    }

    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { state }
            .flowWithLifecycle(lifecycle)
            .collect {
                if (state.refreshCards) {
                    viewModel.process(CardListViewModel.Action.GetCards(filter))
                }
            }
    }

}

@Composable
fun CardListContent(
    navController: NavController,
    cards: List<Card>,
    title: String,
    onSolutionClick: (Card, String) -> Unit,
    onCreateCardClick: () -> Unit,
    onFilterChange: (String) -> Unit,
    onClickApply: () -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onCreateCardClick()
            }) {
                Icon(Icons.Filled.Add, contentDescription = EMPTY)
            }
        }
    ) { padding ->
        PullToRefreshLazyColumn(
            items = cards,
            content = { card ->
                CardItemList(
                    card = card,
                    onClick = {
                        navController.navigateToCardDetail(card.id)
                    },
                    onSolutionClick = { solution ->
                        onSolutionClick(card, solution)
                    })
            },
            header = {
                CustomAppBar(navController = navController, title = title)
                AnimatedVisibility(visible = cards.isNotEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        FiltersBottomSheetV2(
                            onFilterChange = onFilterChange,
                            onClickApply = onClickApply
                        )
                    }
                }
                CustomSpacer(space = SpacerSize.SMALL)
            },
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.defaultScreen(padding)
        )
//        LazyColumn(
//            modifier = Modifier.defaultScreen(padding),
//        ) {
//            stickyHeader {
//                CustomAppBar(navController = navController, title = title)
//                AnimatedVisibility(visible = cards.isNotEmpty()) {
//                    Box(
//                        modifier = Modifier.fillParentMaxWidth(),
//                        contentAlignment = Alignment.CenterEnd
//                    ) {
//                        FiltersBottomSheetV2(
//                            onFilterChange = onFilterChange,
//                            onClickApply = onClickApply
//                        )
//                    }
//                }
//                CustomSpacer(space = SpacerSize.SMALL)
//            }
//            items(cards) { card ->
//                CardItemList(
//                    card = card,
//                    onClick = {
//                        navController.navigateToCardDetail(card.id)
//                    },
//                    onSolutionClick = { solution ->
//                        onSolutionClick(card, solution)
//                    })
//            }
//            item {
//                AnimatedVisibility(visible = cards.isEmpty()) {
//                    EmptyData(modifier = Modifier.fillMaxSize())
//                }
//            }
//        }
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
                title = "Cards",
                onSolutionClick = { _, _ -> },
                onCreateCardClick = {},
                onFilterChange = {},
                onClickApply = {},
                isRefreshing = false,
                {}
            )
        }
    }
}