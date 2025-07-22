package com.ih.osm.ui.pages.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.icu.util.Calendar
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.twotone.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.ih.osm.MainActivity
import com.ih.osm.R
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.NetworkStatus
import com.ih.osm.domain.model.User
import com.ih.osm.domain.model.toLocalCards
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.CustomTag
import com.ih.osm.ui.components.CustomTextField
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
import com.ih.osm.ui.navigation.navigateToCiltRoutine
import com.ih.osm.ui.navigation.navigateToOplList
import com.ih.osm.ui.navigation.navigateToQrScanner
import com.ih.osm.ui.pages.home.action.HomeAction
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.PaddingTiny
import com.ih.osm.ui.theme.PaddingToolbar
import com.ih.osm.ui.theme.Radius8
import com.ih.osm.ui.theme.Size2
import com.ih.osm.ui.theme.Size64
import com.ih.osm.ui.utils.CARD_ANOMALIES
import com.ih.osm.ui.utils.EMPTY
import com.ih.osm.ui.utils.LOAD_CATALOGS
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeScreenV2(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (state.isLoading) {
        LoadingScreen(text = state.message)
    } else {
        HomeContent(
            navController = navController,
            user = state.user,
            cardList = state.cards,
            networkStatus = state.networkStatus,
            lastSyncUpdateDate = state.lastSyncUpdate,
            showSyncLocalCards = state.showSyncLocalCards,
            showSyncCatalogs = state.showSyncCatalogs,
            showSyncRemoteCards = state.showSyncRemoteCards,
            onClick = { action ->
                when (action) {
                    HomeActionClick.CATALOGS -> {
                        viewModel.process(HomeAction.SyncCatalogs(LOAD_CATALOGS))
                    }

                    HomeActionClick.LOCAL_CARDS -> {
                        viewModel.process(HomeAction.SyncLocalCards(context))
                    }

                    HomeActionClick.REMOTE_CARDS -> {
                        viewModel.process(HomeAction.SyncRemoteCards)
                    }

                    HomeActionClick.NAVIGATION -> {
                        navController.navigateToCardList(CARD_ANOMALIES)
                    }

                    HomeActionClick.CILT_ROUTINE -> {
                        navController.navigateToCiltRoutine()
                    }

                    HomeActionClick.OPL_NAVIGATION -> {
                        navController.navigateToOplList()
                    }
                }
            },
        )
    }

    SnackbarHost(hostState = snackBarHostState) {
        Snackbar(
            snackbarData = it,
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = Color.White,
            modifier = Modifier.padding(top = PaddingToolbar),
        )
    }

    LaunchedEffect(Unit) {
        if (state.isSyncing.not()) {
            viewModel.process(HomeAction.GetCards)
        }
    }

    LaunchedEffect(viewModel) {
        snapshotFlow { state }
            .flowWithLifecycle(lifecycle)
            .distinctUntilChanged()
            .collect {
                if (state.message.isNotEmpty() && state.isLoading.not()) {
                    scope.launch {
                        snackBarHostState.showSnackbar(message = state.message)
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
    networkStatus: NetworkStatus,
    lastSyncUpdateDate: String,
    showSyncLocalCards: Boolean,
    showSyncCatalogs: Boolean,
    showSyncRemoteCards: Boolean,
    onClick: (HomeActionClick) -> Unit,
) {
    var showFastPasswordDialog by remember { mutableStateOf(false) }
    var fastPassword by remember { mutableStateOf(EMPTY) }

    Scaffold(
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = {
                        navController.navigateToQrScanner()
                    },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_qr_scan),
                        contentDescription = EMPTY,
                    )
                }
                CustomSpacer()
                FloatingActionButton(
                    onClick = {
                        navController.navigateToAccount()
                    },
                ) {
                    Icon(
                        Icons.Outlined.Settings,
                        contentDescription = EMPTY,
                    )
                }
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            if (user != null) {
                stickyHeader {
                    HomeAppBarV2(
                        user = user,
                        padding = padding.calculateTopPadding(),
                        networkStatus = networkStatus,
                    )
                }
            }
            item {
                CustomSpacer(space = SpacerSize.LARGE)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        stringResource(R.string.cards),
                        style =
                            MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.W500,
                                color = getTextColor(),
                            ),
                        modifier = Modifier.padding(horizontal = PaddingNormal),
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(end = PaddingNormal),
                    )
                }
                CustomSpacer()
            }
            item {
                AnimatedVisibility(visible = showSyncRemoteCards) {
                    HomeSectionCardItem(
                        title = stringResource(R.string.sync_remote_cards),
                        icon = Icons.Outlined.Refresh,
                        description = stringResource(R.string.sync_remote_cards_description),
                        subText = stringResource(R.string.important),
                        subTextColor = MaterialTheme.colorScheme.error,
                    ) {
                        onClick(HomeActionClick.REMOTE_CARDS)
                    }
                }

                AnimatedVisibility(visible = showSyncLocalCards) {
                    HomeSectionCardItem(
                        title = stringResource(R.string.sync_cards),
                        icon = Icons.Outlined.Refresh,
                        subText =
                            stringResource(
                                R.string.last_update,
                                lastSyncUpdateDate,
                            ),
                        description =
                            stringResource(
                                R.string.local_cards,
                                cardList.toLocalCards().size,
                            ),
                    ) {
                        onClick(HomeActionClick.LOCAL_CARDS)
                    }
                }
                AnimatedVisibility(visible = showSyncCatalogs) {
                    HomeSectionCardItem(
                        title = stringResource(R.string.sync_remote_catalogs),
                        icon = Icons.Outlined.Refresh,
                        description = stringResource(R.string.you_have_new_catalogs_to_sync),
                        subText = stringResource(R.string.important),
                        subTextColor = MaterialTheme.colorScheme.error,
                    ) {
                        onClick(HomeActionClick.CATALOGS)
                    }
                }
                HomeSectionCardItem(
                    title = stringResource(R.string.anomalies_cards),
                    icon = Icons.Outlined.Build,
                    description =
                        if (cardList.isNotEmpty()) {
                            stringResource(
                                R.string.total_cards,
                                cardList.size,
                            )
                        } else {
                            EMPTY
                        },
                ) {
                    onClick(HomeActionClick.NAVIGATION)
                }

                HomeSectionCardItem(
                    title = stringResource(R.string.cilt_routine),
                    icon = Icons.Outlined.CheckCircle,
                    description = stringResource(R.string.view_cilt_routines),
                ) {
                    onClick(HomeActionClick.CILT_ROUTINE)
                }

                // OPL Section
                HomeSectionCardItem(
                    title = stringResource(R.string.docs_title),
                    icon = Icons.Outlined.Settings,
                    description = stringResource(R.string.opl_sop_documents),
                ) {
                    onClick(HomeActionClick.OPL_NAVIGATION)
                }

                HomeSectionCardItem(
                    title = stringResource(R.string.fast_password),
                    icon = Icons.Outlined.Lock,
                    description = stringResource(R.string.change_user),
                ) {
                    showFastPasswordDialog = true
                }
            }
        }
        if (showFastPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showFastPasswordDialog = false },
                confirmButton = {
                    /*
                    TextButton(
                        onClick = {
                            showFastPasswordDialog = false
                        },
                    ) {
                        Text(
                            text = "Aceptar",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                     */
                },
                dismissButton = {
                    /*
                    TextButton(
                        onClick = { showFastPasswordDialog = false },
                    ) {
                        Text(
                            text = "Cancelar",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                     */
                },
                title = {
                    Text(
                        text = stringResource(R.string.fast_password),
                    )
                },
                text = {
                    CustomTextField(
                        label = stringResource(R.string.enter_fast_password),
                        icon = Icons.Outlined.Lock,
                        isPassword = true,
                        onChange = { fastPassword = it },
                    )
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.surface,
            )
        }
    }
}

enum class HomeActionClick {
    CATALOGS,
    LOCAL_CARDS,
    REMOTE_CARDS,
    NAVIGATION,
    CILT_ROUTINE,
    OPL_NAVIGATION,
}

@Composable
private fun HomeSectionCardItem(
    title: String,
    icon: ImageVector,
    description: String = EMPTY,
    subText: String = EMPTY,
    subTextColor: Color = Color.Unspecified,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(PaddingNormal),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CustomSpacer()
            Icon(icon, contentDescription = EMPTY, tint = getPrimaryColor())
            Text(
                text = title,
                style =
                    MaterialTheme.typography.titleLarge.copy(
                        color = getPrimaryColor(),
                    ),
                modifier = Modifier.padding(PaddingNormal),
            )
            AnimatedVisibility(visible = description.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = description,
                        style =
                            MaterialTheme.typography.bodyMedium
                                .copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(bottom = Size2),
                    )
                    AnimatedVisibility(visible = subText.isNotEmpty()) {
                        Text(
                            text = subText,
                            style =
                                MaterialTheme.typography.bodySmall
                                    .copy(color = subTextColor),
                        )
                    }
                    CustomSpacer()
                }
            }
        }
    }
}

@Composable
private fun HomeAppBarV2(
    user: User,
    padding: Dp,
    networkStatus: NetworkStatus,
) {
    Column(
        modifier = Modifier.headerContent(padding, false),
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularImage(image = user.logo, size = Size64)
            NetworkCard(
                networkStatus = networkStatus,
                textColor = getTextColor(),
                modifier =
                    Modifier
                        .background(
                            shape = RoundedCornerShape(Radius8),
                            color = Color.Gray.copy(alpha = 0.1f),
                        )
                        .padding(PaddingTiny),
            )
        }
        CustomSpacer(space = SpacerSize.SMALL)
        Column {
            Text(
                text = "${getTimeText()},\n${user.name}.",
                style =
                    MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = getTextColor(),
                    ),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = user.siteName,
                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            color = getTextColor(),
                        ),
                )
                Icon(
                    Icons.TwoTone.CheckCircle,
                    contentDescription = user.siteName,
                    tint = Color(0XFF048574),
                    modifier = Modifier.size(16.dp),
                )
            }
            CustomSpacer()
            if (user.roles.isNotEmpty()) {
                LazyRow {
                    items(user.roles) {
                        Box(modifier = Modifier.padding(horizontal = 2.dp)) {
                            CustomTag(
                                title = it,
                                tagSize = TagSize.SMALL,
                                tagType = TagType.OUTLINE,
                                invertedColors = false,
                            )
                        }
                    }
                }
            }
        }
        CustomSpacer(space = SpacerSize.TINY)
    }
}

@Composable
private fun getTimeText(): String {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    Log.e("test", "Hour -> $hour")
    return when (hour) {
        in 0..11 -> stringResource(R.string.good_morning)
        in 12..19 -> stringResource(R.string.good_evening)
        in 20..24 -> stringResource(R.string.good_night)
        else -> stringResource(R.string.welcome_back)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
private fun HomeScreenPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
//            HomeContent(
//                navController = rememberNavController(),
//                user = User.mockUser(),
//                cardList = emptyList(),
//                networkStatus = NetworkStatus.WIFI_CONNECTED,
//                lastSyncUpdateDate = "",
//                showSyncCards = true,
//                showSyncCatalogs = true,
//                showSyncRemoteCards = true,
//                onClick = {}
//            )
        }
    }
}
