package com.osm.ui.pages.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import com.ih.osm.R
import com.osm.core.ui.LCE
import com.osm.domain.model.Card
import com.osm.domain.model.NetworkStatus
import com.osm.domain.model.User
import com.osm.domain.model.toAnomaliesList
import com.osm.domain.model.toLocalCards
import com.osm.ui.components.CustomSpacer
import com.osm.ui.components.CustomTag
import com.osm.ui.components.LoadingScreen
import com.osm.ui.components.SpacerDirection
import com.osm.ui.components.SpacerSize
import com.osm.ui.components.TagSize
import com.osm.ui.components.TagType
import com.osm.ui.components.card.NetworkCard
import com.osm.ui.components.images.CircularImage
import com.osm.ui.extensions.getColor
import com.osm.ui.extensions.getPrimaryColor
import com.osm.ui.extensions.headerContent
import com.osm.ui.navigation.navigateToAccount
import com.osm.ui.navigation.navigateToCardList
import com.osm.ui.navigation.navigateToQrScanner
import com.osm.ui.theme.OsmAppTheme
import com.osm.ui.theme.PaddingNormal
import com.osm.ui.theme.PaddingToolbar
import com.osm.ui.theme.Size2
import com.osm.ui.utils.CARD_ANOMALIES
import com.osm.ui.utils.EMPTY
import com.osm.ui.utils.LOAD_CATALOGS
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
    val context = LocalContext.current

    if (state.isLoading) {
        LoadingScreen(text = state.message)
    } else {
        HomeContentV2(
            navController = navController,
            user = (state.state as? LCE.Success)?.value,
            cards = state.cards,
            onSyncCardsClick = {
                viewModel.process(HomeViewModelV2.Action.SyncCards(context))
            },
            onCardClick = {
                navController.navigateToCardList(it)
            },
            networkStatus = state.networkStatus,
            lastSyncUpdateDate = state.lastSyncUpdate,
            showSyncCards = state.showSyncCards,
            showSyncCatalogs = state.showSyncCatalogsCard,
            onSyncCatalogsClick = {
                viewModel.process(HomeViewModelV2.Action.SyncCatalogs(LOAD_CATALOGS))
            },
            showSyncRemoteCards = state.showSyncRemoteCards,
            onSyncRemoteCardsClick = {
                viewModel.process(HomeViewModelV2.Action.SyncRemoteCards)
            }
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
                if (state.syncCatalogs && state.syncCompleted.not()) {
                    viewModel.process(HomeViewModelV2.Action.SyncCatalogs(syncCatalogs))
                } else if (state.isSyncing.not()) {
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
    networkStatus: NetworkStatus,
    lastSyncUpdateDate: String,
    showSyncCards: Boolean,
    showSyncCatalogs: Boolean,
    onSyncCatalogsClick: () -> Unit,
    showSyncRemoteCards: Boolean,
    onSyncRemoteCardsClick: () -> Unit
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
                        networkStatus = networkStatus,
                    )
                }
            }


            item {
                AnimatedVisibility(visible = showSyncRemoteCards) {
                    HomeSectionCardItem(
                        title = stringResource(R.string.sync_remote_cards),
                        icon = Icons.Outlined.Refresh,
                        description = stringResource(R.string.sync_remote_cards_description),
                        subText = stringResource(R.string.important),
                        subTextColor = MaterialTheme.colorScheme.error
                    ) {
                        onSyncRemoteCardsClick()
                    }
                }

                AnimatedVisibility(visible = showSyncCards) {
                    HomeSectionCardItem(
                        title = stringResource(R.string.sync_cards),
                        icon = Icons.Outlined.Refresh,
                        subText = stringResource(
                            R.string.last_update, lastSyncUpdateDate
                        ),
                        description = stringResource(
                            R.string.local_cards,
                            cards.toLocalCards().size
                        )
                    ) {
                        onSyncCardsClick()
                    }
                }
                AnimatedVisibility(visible = showSyncCatalogs) {
                    HomeSectionCardItem(
                        title = stringResource(R.string.sync_remote_catalogs),
                        icon = Icons.Outlined.Refresh,
                        description = stringResource(R.string.you_have_new_catalogs_to_sync),
                        subText = stringResource(R.string.important),
                        subTextColor = MaterialTheme.colorScheme.error
                    ) {
                        onSyncCatalogsClick()
                    }
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
//                HomeSectionCardItem(
//                    title = stringResource(R.string.behaviour_cards),
//                    icon = Icons.Outlined.Person,
//                    description = if (cards.isNotEmpty()) stringResource(
//                        R.string.total_cards,
//                        cards.toBehaviorList().size
//                    ) else EMPTY
//                ) {
//                    onCardClick(CARD_BEHAVIOR)
//                }
            }
        }
    }
}

@Composable
private fun HomeSectionCardItem(
    title: String,
    icon: ImageVector,
    description: String = EMPTY,
    subText: String = EMPTY,
    subTextColor: Color = Color.Unspecified,
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = description, style = MaterialTheme.typography.bodyMedium
                            .copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(bottom = Size2)
                    )
                    AnimatedVisibility(visible = subText.isNotEmpty()) {
                        Text(text = subText, style = MaterialTheme.typography.bodySmall
                            .copy(color = subTextColor))
                    }
                    CustomSpacer()
                }
            }
        }
    }
}

@Composable
private fun HomeAppBarV2(
    navController: NavController,
    user: User,
    padding: Dp,
    networkStatus: NetworkStatus,
) {
    Column(
        modifier = Modifier.headerContent(padding),
        horizontalAlignment = Alignment.End
    ) {
        Icon(
            Icons.Outlined.Settings,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NetworkCard(networkStatus = networkStatus)
            Icon(
                painter = painterResource(id = R.drawable.ic_qr_scan),
                contentDescription = EMPTY,
                tint = getColor(),
                modifier = Modifier.clickable {
                    navController.navigateToQrScanner()
                }
            )
        }
        CustomSpacer()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
private fun HomeScreenPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            HomeContentV2(
                navController = rememberNavController(),
                user = User.mockUser(),
                cards = emptyList(),
                onSyncCardsClick = {},
                onCardClick = {},
                networkStatus = NetworkStatus.WIFI_CONNECTED,
                lastSyncUpdateDate = "",
                showSyncCards = true,
                showSyncCatalogs = true,
                onSyncCatalogsClick = {},
                showSyncRemoteCards = true,
                onSyncRemoteCardsClick = {}
            )
        }
    }
}