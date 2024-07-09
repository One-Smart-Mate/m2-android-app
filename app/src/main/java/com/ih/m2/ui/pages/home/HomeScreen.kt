package com.ih.m2.ui.pages.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
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
import com.ih.m2.R
import com.ih.m2.core.ui.LCE
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.User
import com.ih.m2.domain.model.enableDefinitiveSolution
import com.ih.m2.domain.model.enableProvisionalSolution
import com.ih.m2.ui.components.CustomSpacer
import com.ih.m2.ui.components.CustomTag
import com.ih.m2.ui.components.PullToRefreshLazyColumn
import com.ih.m2.ui.components.ScreenLoading
import com.ih.m2.ui.components.SpacerDirection
import com.ih.m2.ui.components.TagSize
import com.ih.m2.ui.components.TagType
import com.ih.m2.ui.components.buttons.CustomIconButton
import com.ih.m2.ui.components.images.CircularImage
import com.ih.m2.ui.components.sheets.FiltersBottomSheet
import com.ih.m2.ui.components.sheets.SolutionBottomSheet
import com.ih.m2.ui.extensions.defaultIfNull
import com.ih.m2.ui.extensions.getColor
import com.ih.m2.ui.extensions.headerContent
import com.ih.m2.ui.extensions.runWorkRequest
import com.ih.m2.ui.navigation.navigateToAccount
import com.ih.m2.ui.navigation.navigateToCardDetail
import com.ih.m2.ui.navigation.navigateToCardSolution
import com.ih.m2.ui.navigation.navigateToCreateCard
import com.ih.m2.ui.pages.error.ErrorScreen
import com.ih.m2.ui.pages.home.components.CardItemList
import com.ih.m2.ui.theme.M2androidappTheme
import com.ih.m2.ui.utils.EMPTY

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
            ScreenLoading(text = state.loadingMessage)
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
                    onActionClick = {
                        onOpenBottomSheetActions(card)
                    },
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
    M2androidappTheme {
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