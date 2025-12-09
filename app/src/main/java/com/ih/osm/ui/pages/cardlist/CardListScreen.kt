package com.ih.osm.ui.pages.cardlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ih.osm.R
import com.ih.osm.domain.model.Card
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.buttons.ButtonType
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.components.card.CardItemListV2
import com.ih.osm.ui.components.sheets.FiltersBottomSheet
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.navigation.navigateToCardDetail
import com.ih.osm.ui.navigation.navigateToCardSolution
import com.ih.osm.ui.navigation.navigateToCreateCard
import com.ih.osm.ui.pages.cardlist.action.CardListAction
import com.ih.osm.ui.theme.Size120
import com.ih.osm.ui.utils.EMPTY

@Composable
fun CardListScreen(
    navController: NavController,
    viewModel: CardListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading && state.cards.isEmpty()) {
        LoadingScreen(text = state.message)
    } else {
        CardListContent(
            navController = navController,
            cards = state.cards,
            onAction = { action ->
                when (action) {
                    is CardListAction.Action -> {
                        navController.navigateToCardSolution(action.action, action.id)
                    }
                    is CardListAction.Create -> {
                        navController.navigateToCreateCard()
                    }
                    is CardListAction.Detail -> {
                        navController.navigateToCardDetail(action.id)
                    }
                    is CardListAction.Filters -> {
                        viewModel.handleFilterCards(action.filter)
                    }
                }
            },
            viewModel = viewModel,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardListContent(
    navController: NavController,
    cards: List<Card>,
    onAction: (CardListAction) -> Unit,
    viewModel: CardListViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // ----------------------------
    // 🔥 DETECTAR SCROLL AL FINAL
    // ----------------------------
    LaunchedEffect(listState) {
        snapshotFlow {
            val lastVisible =
                listState.layoutInfo.visibleItemsInfo
                    .lastOrNull()
                    ?.index
            val total = listState.layoutInfo.totalItemsCount
            lastVisible to total
        }.collect { (lastVisible, total) ->
            if (
                lastVisible != null &&
                lastVisible >= total - 3 &&
                // Cuando faltan 3 items
                !state.isLoadingMore &&
                state.hasMorePages &&
                !state.isLoading
            ) {
                viewModel.loadMore()
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onAction(CardListAction.Create)
            }) {
                Icon(Icons.Filled.Add, contentDescription = EMPTY)
            }
        },
    ) { padding ->

        LazyColumn(
            state = listState,
            modifier = Modifier.defaultScreen(padding),
        ) {
            // -------------------
            // 🔝 STICKY HEADER
            // -------------------
            stickyHeader {
                Column(
                    modifier =
                        Modifier.background(
                            color = MaterialTheme.colorScheme.background,
                        ),
                ) {
                    CustomAppBar(
                        navController = navController,
                        title = stringResource(R.string.anomalies_cards),
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            CustomButton(
                                text = stringResource(R.string.update_list),
                                modifier = Modifier.width(Size120),
                                buttonType = ButtonType.TEXT,
                            ) {
                                viewModel.handleUpdateRemoteCardsAndSave()
                            }

                            FiltersBottomSheet { filter ->
                                onAction(CardListAction.Filters(filter))
                            }
                        }
                    }

                    CustomSpacer(space = SpacerSize.SMALL)
                }
            }

            // -------------------
            // 📄 ITEMS DE LA LISTA
            // -------------------
            items(
                items = cards,
                key = { it.uuid },
            ) { card ->
                CardItemListV2(
                    card = card,
                    onClick = {
                        onAction(CardListAction.Detail(card.uuid))
                    },
                    onAction = {
                        onAction(CardListAction.Action(it, card.uuid))
                    },
                )
            }

            // -------------------
            // 🔄 FOOTER DE LOADING
            // -------------------
            if (state.isLoadingMore) {
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
