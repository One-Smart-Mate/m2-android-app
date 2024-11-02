package com.ih.osm.ui.pages.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.twotone.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ih.osm.MainActivity
import com.ih.osm.R
import com.ih.osm.core.ui.LCE
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.NetworkStatus
import com.ih.osm.domain.model.User
import com.ih.osm.domain.model.toAnomaliesList
import com.ih.osm.domain.model.toLocalCards
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.CustomTag
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.TagSize
import com.ih.osm.ui.components.TagType
import com.ih.osm.ui.components.card.NetworkCard
import com.ih.osm.ui.components.images.CircularImage
import com.ih.osm.ui.extensions.getActivity
import com.ih.osm.ui.extensions.getPrimaryColor
import com.ih.osm.ui.extensions.getTextColor
import com.ih.osm.ui.extensions.headerContent
import com.ih.osm.ui.navigation.navigateToAccount
import com.ih.osm.ui.navigation.navigateToCardList
import com.ih.osm.ui.navigation.navigateToQrScanner
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.PaddingToolbar
import com.ih.osm.ui.theme.Size2
import com.ih.osm.ui.utils.CARD_ANOMALIES
import com.ih.osm.ui.utils.EMPTY
import com.ih.osm.ui.utils.LOAD_CATALOGS
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeScreenV2(
    navController: NavController,
    viewModel: HomeViewModel = mavericksViewModel(),
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
        HomeContent(
            navController = navController,
            user = (state.state as? LCE.Success)?.value,
            cardList = state.cards,
            onSyncCardsClick = {
                viewModel.process(HomeViewModel.Action.SyncCards(context))
            },
            onCardClick = {
                navController.navigateToCardList(it)
            },
            networkStatus = state.networkStatus,
            lastSyncUpdateDate = state.lastSyncUpdate,
            showSyncCards = state.showSyncCards,
            showSyncCatalogs = state.showSyncCatalogsCard,
            onSyncCatalogsClick = {
                viewModel.process(HomeViewModel.Action.SyncCatalogs(LOAD_CATALOGS))
            },
            showSyncRemoteCards = state.showSyncRemoteCards,
            onSyncRemoteCardsClick = {
                viewModel.process(HomeViewModel.Action.SyncRemoteCards)
            }
        )
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
                    viewModel.process(HomeViewModel.Action.SyncCatalogs(syncCatalogs))
                } else if (state.isSyncing.not()) {
                    viewModel.process(HomeViewModel.Action.GetCards)
                }
                if (state.message.isNotEmpty() && state.isLoading.not()) {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = state.message
                        )
                        viewModel.process(HomeViewModel.Action.ClearMessage)
                    }
                }
                if (state.updateApp) {
                    context.getActivity<MainActivity>()
                        ?.showUpdateDialog()
                }
            }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeContent(
    navController: NavController,
    user: User?,
    cardList: List<Card>,
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
    Scaffold(
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = {
                        navController.navigateToQrScanner()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_qr_scan),
                        contentDescription = EMPTY
                    )
                }
                CustomSpacer(space = SpacerSize.TINY)
                FloatingActionButton(
                    onClick = {
                        navController.navigateToAccount()
                    }
                ) {
                    Icon(
                        Icons.Outlined.Settings,
                        contentDescription = EMPTY
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            stickyHeader {
                user?.let {
                    HomeAppBarV2(
                        user = it,
                        padding = padding.calculateTopPadding(),
                        networkStatus = networkStatus
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
                        subText =
                        stringResource(
                            R.string.last_update,
                            lastSyncUpdateDate
                        ),
                        description =
                        stringResource(
                            R.string.local_cards,
                            cardList.toLocalCards().size
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
                    description =
                    if (cardList.isNotEmpty()) {
                        stringResource(
                            R.string.total_cards,
                            cardList.toAnomaliesList().size
                        )
                    } else {
                        EMPTY
                    }
                ) {
                    onCardClick(CARD_ANOMALIES)
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
    subText: String = EMPTY,
    subTextColor: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    Card(
        modifier =
        Modifier
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
                style =
                MaterialTheme.typography.titleLarge.copy(
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
                        text = description,
                        style =
                        MaterialTheme.typography.bodyMedium
                            .copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(bottom = Size2)
                    )
                    AnimatedVisibility(visible = subText.isNotEmpty()) {
                        Text(
                            text = subText,
                            style =
                            MaterialTheme.typography.bodySmall
                                .copy(color = subTextColor)
                        )
                    }
                    CustomSpacer()
                }
            }
        }
    }
}

@Composable
private fun HomeAppBarV2(user: User, padding: Dp, networkStatus: NetworkStatus) {
    Column(
        modifier = Modifier.headerContent(padding, false),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularImage(image = user.logo)
            NetworkCard(networkStatus = networkStatus, textColor = getTextColor())
        }
        CustomSpacer(space = SpacerSize.SMALL)
        Column {
            Text(
                text = "Good morning,\nFausto Camano.",
                style =
                MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = getTextColor()
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user.companyName,
                    style =
                    MaterialTheme.typography.bodyLarge.copy(
                        color = getTextColor()
                    )
                )
                Icon(
                    Icons.TwoTone.CheckCircle,
                    contentDescription = user.companyName,
                    tint = Color(0XFF048574),
                    modifier = Modifier.size(16.dp)

                )
            }
            CustomSpacer()
            LazyRow {
                items(user.roles) {
                    Box(modifier = Modifier.padding(horizontal = 2.dp)) {
                        CustomTag(
                            title = it,
                            tagSize = TagSize.SMALL,
                            tagType = TagType.OUTLINE,
                            invertedColors = false
                        )
                    }
                }
            }
        }
        CustomSpacer(space = SpacerSize.TINY)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
private fun HomeScreenPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            HomeContent(
                navController = rememberNavController(),
                user = User.mockUser(),
                cardList = emptyList(),
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
