package com.ih.osm.ui.pages.cardlist

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ih.osm.R
import com.ih.osm.domain.model.Card
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.PullToRefreshLazyColumn
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.buttons.ButtonType
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.components.card.CardItemListV2
import com.ih.osm.ui.components.sheets.FiltersBottomSheet
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.navigation.navigateToCardDetail
import com.ih.osm.ui.navigation.navigateToCardSolution
import com.ih.osm.ui.navigation.navigateToCreateCard
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingToolbar
import com.ih.osm.ui.theme.Size120
import com.ih.osm.ui.utils.EMPTY
import kotlinx.coroutines.launch

@Composable
fun CardListScreen(
    navController: NavController,
    viewModel: CardListViewModel = mavericksViewModel(),
    filter: String
) {
    val state by viewModel.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

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
            isRefreshing = state.isRefreshing,
            onRefresh = {
                viewModel.process(CardListViewModel.Action.OnRefreshCardList)
            }
        )
    }

    SnackbarHost(hostState = snackBarHostState) {
        Snackbar(
            snackbarData = it,
            containerColor = Color.Red,
            contentColor = Color.White,
            modifier = Modifier.padding(top = PaddingToolbar)
        )
    }

    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { state }
            .flowWithLifecycle(lifecycle)
            .collect {
                if (state.refreshCards) {
                    viewModel.process(CardListViewModel.Action.GetCards(filter))
                }
                if (state.isLoading.not() &&
                    state.isRefreshing.not() &&
                    state.message.isNotEmpty()
                ) {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = state.message
                        )
                        viewModel.process(CardListViewModel.Action.ClearMessage)
                    }
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
                CardItemListV2(
                    card = card,
                    onClick = {
                        navController.navigateToCardDetail(card.id)
                    },
                    onSolutionClick = { solution ->
                        onSolutionClick(card, solution)
                    }
                )
            },
            header = {
                CustomAppBar(navController = navController, title = title)
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        CustomButton(
                            text = stringResource(R.string.update_list),
                            modifier = Modifier.width(Size120),
                            buttonType = ButtonType.TEXT
                        ) {
                            onRefresh()
                        }
                        FiltersBottomSheet(
                            onFilterChange
                        )
                    }
                }
                CustomSpacer(space = SpacerSize.SMALL)
            },
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.defaultScreen(padding)
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
private fun CardListScreenScreenPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            CardListContent(
                navController = rememberNavController(),
                cards = emptyList(),
                title = "Cards",
                onSolutionClick = { _, _ -> },
                onCreateCardClick = {},
                onFilterChange = {},
                isRefreshing = false,
                {}
            )
        }
    }
}
