package com.ih.m2.ui.pages.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
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
import com.ih.m2.domain.model.NetworkStatus
import com.ih.m2.domain.model.User
import com.ih.m2.domain.model.toAnomaliesList
import com.ih.m2.domain.model.toBehaviorList
import com.ih.m2.domain.model.toLocalCards
import com.ih.m2.ui.components.CustomSpacer
import com.ih.m2.ui.components.CustomTag
import com.ih.m2.ui.components.LoadingScreen
import com.ih.m2.ui.components.SpacerDirection
import com.ih.m2.ui.components.SpacerSize
import com.ih.m2.ui.components.TagSize
import com.ih.m2.ui.components.TagType
import com.ih.m2.ui.components.card.NetworkCard
import com.ih.m2.ui.components.images.CircularImage
import com.ih.m2.ui.extensions.getColor
import com.ih.m2.ui.extensions.getPrimaryColor
import com.ih.m2.ui.extensions.headerContent
import com.ih.m2.ui.navigation.navigateToAccount
import com.ih.m2.ui.navigation.navigateToCardList
import com.ih.m2.ui.navigation.navigateToCreateCard
import com.ih.m2.ui.theme.M2androidappTheme
import com.ih.m2.ui.theme.PaddingNormal
import com.ih.m2.ui.theme.PaddingToolbar
import com.ih.m2.ui.utils.CARD_ANOMALIES
import com.ih.m2.ui.utils.CARD_BEHAVIOR
import com.ih.m2.ui.utils.EMPTY
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeScreenV2(
    navController: NavController,
    viewModel: HomeViewModelV2 = mavericksViewModel(),
    syncCatalogs: String = EMPTY
) {
    val state by viewModel.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (state.isLoading) {
        LoadingScreen(text = state.message)
    } else {
        HomeContentV2(
            navController = navController,
            user = (state.state as? LCE.Success)?.value,
            cards = state.cards,
            onSyncCardsClick = {
                viewModel.process(HomeViewModelV2.Action.SyncCards)
            },
            onCardClick = {
                navController.navigateToCardList(it)
            },
            networkStatus = state.networkStatus
        )
    }

    if (state.message.isNotEmpty() && state.isLoading.not()) {
        scope.launch {
            snackBarHostState.showSnackbar(
                message = state.message,
            )
            viewModel.process(HomeViewModelV2.Action.ClearMessage)
        }

    }
    SnackbarHost(hostState = snackBarHostState) {
        Snackbar(
            snackbarData = it,
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = Color.White,
            modifier = Modifier.padding(top = PaddingToolbar)
        )
    }

    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { state }
            .flowWithLifecycle(lifecycle)
            .collect {
                if (state.syncCatalogs) {
                    viewModel.process(HomeViewModelV2.Action.SyncCatalogs(syncCatalogs))
                }
                if (state.refreshCards) {
                    viewModel.process(HomeViewModelV2.Action.GetCards)
                }
            }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeContentV2(
    navController: NavController,
    user: User?,
    cards: List<Card>,
    onSyncCardsClick: () -> Unit,
    onCardClick: (String) -> Unit,
    networkStatus: NetworkStatus
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            stickyHeader {
                user?.let {
                    HomeAppBarV2(
                        navController = navController,
                        user = it,
                        padding = padding.calculateTopPadding(),
                        networkStatus = networkStatus
                    )
                }
            }
            item {
                HomeSectionCardItem(
                    title = stringResource(R.string.create_card),
                    icon = Icons.Outlined.Create
                ) {
                    navController.navigateToCreateCard()
                }
                HomeSectionCardItem(
                    title = stringResource(R.string.sync_cards),
                    icon = Icons.Outlined.Refresh,
                    description = if (cards.isNotEmpty()) stringResource(
                        R.string.local_cards,
                        cards.toLocalCards().size
                    ) else EMPTY
                ) {
                    onSyncCardsClick()
                }
                HomeSectionCardItem(
                    title = stringResource(R.string.anomalies_cards),
                    icon = Icons.Outlined.Build,
                    description = if (cards.isNotEmpty()) stringResource(
                        R.string.total_cards,
                        cards.toAnomaliesList().size
                    ) else EMPTY
                ) {
                    onCardClick(CARD_ANOMALIES)
                }
                HomeSectionCardItem(
                    title = stringResource(R.string.behaviour_cards),
                    icon = Icons.Outlined.Person,
                    description = if (cards.isNotEmpty()) stringResource(
                        R.string.total_cards,
                        cards.toBehaviorList().size
                    ) else EMPTY
                ) {
                    onCardClick(CARD_BEHAVIOR)
                }
            }
        }
    }
}

@Composable
private fun HomeSectionCardItem(
    title: String,
    icon: ImageVector,
    description: String = EMPTY,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingNormal),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomSpacer()
            Icon(icon, contentDescription = EMPTY, tint = getPrimaryColor())
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = getPrimaryColor()
                ),
                modifier = Modifier.padding(PaddingNormal)
            )
            AnimatedVisibility(visible = description.isNotEmpty()) {
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
                CustomSpacer()
            }
        }
    }
}

@Composable
private fun HomeAppBarV2(navController: NavController, user: User, padding: Dp, networkStatus: NetworkStatus) {
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
                CustomSpacer(space = SpacerSize.TINY)
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
        NetworkCard(networkStatus = networkStatus)
        CustomSpacer()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
private fun HomeScreenPreview() {
    M2androidappTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            HomeContentV2(
                navController = rememberNavController(),
                user = User.mockUser(),
                cards = emptyList(),
                onSyncCardsClick = {},
                onCardClick = {},
                networkStatus = NetworkStatus.WIFI_CONNECTED
            )
        }
    }
}