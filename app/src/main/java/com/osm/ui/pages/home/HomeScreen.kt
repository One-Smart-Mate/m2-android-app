package com.osm.ui.pages.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ih.osm.R
import com.osm.core.ui.LCE
import com.osm.domain.model.Card
import com.osm.domain.model.User
import com.osm.domain.model.enableDefinitiveSolution
import com.osm.domain.model.enableProvisionalSolution
import com.osm.ui.components.CustomSpacer
import com.osm.ui.components.CustomTag
import com.osm.ui.components.PullToRefreshLazyColumn
import com.osm.ui.components.LoadingScreen
import com.osm.ui.components.SpacerDirection
import com.osm.ui.components.TagSize
import com.osm.ui.components.TagType
import com.osm.ui.components.buttons.CustomIconButton
import com.osm.ui.components.images.CircularImage
import com.osm.ui.components.sheets.FiltersBottomSheet
import com.osm.ui.components.sheets.SolutionBottomSheet
import com.osm.ui.extensions.defaultIfNull
import com.osm.ui.extensions.getColor
import com.osm.ui.extensions.headerContent
import com.osm.ui.navigation.navigateToAccount
import com.osm.ui.navigation.navigateToCardDetail
import com.osm.ui.navigation.navigateToCardSolution
import com.osm.ui.navigation.navigateToCreateCard
import com.osm.ui.pages.error.ErrorScreen
import com.osm.ui.components.card.CardItemList
import com.osm.ui.theme.OsmAppTheme
import com.osm.ui.utils.EMPTY

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = mavericksViewModel(),
    syncCatalogs: String = EMPTY
) {
    val state by viewModel.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    when (val screenState = state.user) {
        is LCE.Fail -> {
            ErrorScreen(navController = navController, errorMessage = screenState.error) {
                viewModel.process(HomeViewModel.Action.GetUser)
            }
        }

        is LCE.Loading, LCE.Uninitialized -> {
            LoadingScreen(text = state.loadingMessage)
        }

        is LCE.Success -> {
            HomeContent(
                navController = navController,
                user = screenState.value,
                cards = state.cardList,
                showBottomSheet = state.showBottomSheet,
                showBottomSheetActions = state.showBottomSheetActions,
                selection = state.filterSelection,
                onFilterChange = {
                    viewModel.process(HomeViewModel.Action.OnFilterChange(it))
                },
                onApplyFilter = {
                    viewModel.process(HomeViewModel.Action.OnApplyFilter)
                },
                onOpenBottomSheet = {
                    viewModel.process(HomeViewModel.Action.HandleBottomSheet(true))
                },
                onDismissRequest = {
                    viewModel.process(HomeViewModel.Action.HandleBottomSheet(false))
                },
                onCleanFilers = {
                    viewModel.process(HomeViewModel.Action.OnCleanFilters)
                },
                onOpenBottomSheetActions = {
                    viewModel.process(HomeViewModel.Action.HandleBottomSheetActions(true, it))
                },
                onSolutionClick = { solutionType ->
                    state.selectedCard?.let { card ->
                        viewModel.process(
                            HomeViewModel.Action.HandleBottomSheetActions(
                                false,
                                card
                            )
                        )
                        navController.navigateToCardSolution(solutionType, card.id)
                        viewModel.process(HomeViewModel.Action.ShouldRefreshList(true))
                    }
                },
                onDismissRequestActions = {
                    viewModel.process(HomeViewModel.Action.ShouldRefreshList(false))
                    viewModel.process(HomeViewModel.Action.HandleBottomSheetActions(false))
                },
                isRefreshing = state.isRefreshing,
                onRefresh = {
                    viewModel.process(HomeViewModel.Action.OnRefresh(true))
                },
                onCreateCardClick = {
                    viewModel.process(HomeViewModel.Action.ShouldRefreshList(true))
                },
                showProvisionalSolution = state.selectedCard?.enableProvisionalSolution().defaultIfNull(false),
                showDefinitiveSolution = state.selectedCard?.enableDefinitiveSolution().defaultIfNull(false),
            )
        }
    }
    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { state }
            .flowWithLifecycle(lifecycle)
            .collect {
                if (state.syncCatalogs) {
                    viewModel.process(HomeViewModel.Action.SyncCatalogs(syncCatalogs))
                }
                Log.e("test", " LaunchedEeffect ${state.shouldRefreshList}")
                if (state.shouldRefreshList) {
                    viewModel.process(HomeViewModel.Action.OnRefresh(false))
                }
            }
    }
}

@Composable
fun HomeContent(
    navController: NavController,
    user: User,
    cards: List<Card>,
    showBottomSheet: Boolean = false,
    showBottomSheetActions: Boolean = false,
    selection: String,
    onFilterChange: (String) -> Unit,
    onApplyFilter: () -> Unit,
    onOpenBottomSheet: () -> Unit,
    onDismissRequest: () -> Unit,
    onCleanFilers: () -> Unit,
    onOpenBottomSheetActions: (card: Card) -> Unit,
    onDismissRequestActions: () -> Unit,
    onSolutionClick: (String) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onCreateCardClick: () -> Unit,
    showProvisionalSolution: Boolean,
    showDefinitiveSolution: Boolean
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onCreateCardClick()
                navController.navigateToCreateCard()
            }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.empty))
            }
        }
    ) { paddingValues ->

        PullToRefreshLazyColumn(
            items = cards,
            content = { card ->
                CardItemList(
                    card = card,
                    onClick = {
                        navController.navigateToCardDetail(card.id)
                    },
                    onSolutionClick = {}
                )
            },
            header = {
                HomeAppBar(
                    navController = navController,
                    padding = paddingValues.calculateTopPadding(),
                    user = user,
                    onFilterClick = {
                        onOpenBottomSheet()
                    })
            },
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
        )

        if (showBottomSheet) {
            FiltersBottomSheet(
                selection = selection,
                onFilterChange = onFilterChange,
                onApply = onApplyFilter,
                onDismissRequest = onDismissRequest,
                onCleanFilers = onCleanFilers
            )
        }
        if (showBottomSheetActions) {
            SolutionBottomSheet(
                onSolutionClick = onSolutionClick,
                onDismissRequest = onDismissRequestActions,
                showProvisionalSolution = showProvisionalSolution,
                showDefinitiveSolution = showDefinitiveSolution
            )
        }
    }
}

@Composable
fun HomeAppBar(navController: NavController, user: User, padding: Dp, onFilterClick: () -> Unit) {
    Column(
        modifier = Modifier.headerContent(padding),
        horizontalAlignment = Alignment.End
    ) {
        Icon(
            Icons.Default.AccountCircle,
            contentDescription = stringResource(R.string.empty),
            tint = getColor(),
            modifier = Modifier.clickable {
                navController.navigateToAccount()
            })
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularImage(image = user.logo)
            CustomSpacer(direction = SpacerDirection.HORIZONTAL)
            Column {
                Text(
                    text = stringResource(R.string.welcome_back, user.name),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = getColor()
                    )
                )
                Text(
                    text = user.companyName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = getColor()
                    )
                )
                LazyRow {
                    items(user.roles) {
                        Box(modifier = Modifier.padding(horizontal = 2.dp)) {
                            CustomTag(
                                title = it,
                                tagSize = TagSize.SMALL,
                                tagType = TagType.OUTLINE,
                                invertedColors = true,
                            )
                        }
                    }
                }
            }
        }
        CustomSpacer()
        CustomIconButton(text = stringResource(R.string.filters), icon = Icons.Default.Menu) {
            onFilterClick()
        }
        CustomSpacer()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun HomePreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            HomeContent(
                rememberNavController(),
                User.mockUser(),
                listOf(Card.mock()),
                false,
                false,
                "", {}, {}, {}, {}, {}, {}, {}, {}, false, {}, {}, true,true
            )
        }
    }
}