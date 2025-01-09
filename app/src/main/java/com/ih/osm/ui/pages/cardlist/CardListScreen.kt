package com.ih.osm.ui.pages.cardlist

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ih.osm.R
import com.ih.osm.domain.model.Card
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.card.CardItemListV2
import com.ih.osm.ui.components.sheets.FiltersBottomSheet
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.navigation.navigateToCardDetail
import com.ih.osm.ui.navigation.navigateToCardSolution
import com.ih.osm.ui.navigation.navigateToCreateCard
import com.ih.osm.ui.pages.cardlist.action.CardListAction
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.utils.EMPTY

@Composable
fun CardListScreen(
    navController: NavController,
    viewModel: CardListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
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
        )
    }

    LaunchedEffect(Unit) {
        viewModel.load()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardListContent(
    navController: NavController,
    cards: List<Card>,
    onAction: (CardListAction) -> Unit,
) {
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
            modifier = Modifier.defaultScreen(padding),
        ) {
            stickyHeader {
                Column(
                    modifier =
                        Modifier.background(
                            color = MaterialTheme.colorScheme.background,
                        ),
                ) {
                    CustomAppBar(navController = navController, title = stringResource(R.string.anomalies_cards))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
//                            CustomButton(
//                                text = stringResource(R.string.update_list),
//                                modifier = Modifier.width(Size120),
//                                buttonType = ButtonType.TEXT
//                            ) {
//                                //
//                            }
                            FiltersBottomSheet { filter ->
                                onAction(CardListAction.Filters(filter))
                            }
                        }
                    }
                    CustomSpacer(space = SpacerSize.SMALL)
                }
            }

            items(cards) { card ->
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
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
private fun CardListScreenScreenPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
//            CardListContent(
//                navController = rememberNavController(),
//                cards = emptyList(),
//                title = "Cards",
//                onSolutionClick = { _, _ -> },
//                onCreateCardClick = {},
//                onFilterChange = {},
//                isRefreshing = false,
//                {}
//            )
//        }
        }
    }
}
