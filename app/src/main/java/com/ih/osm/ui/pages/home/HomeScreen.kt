package com.ih.osm.ui.pages.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
}

// @SuppressLint("CoroutineCreationDuringComposition")
// @Composable
// fun HomeScreen(
//    navController: NavController,
//    viewModel: HomeViewModel = mavericksViewModel(),
//    syncCatalogs: String = EMPTY
// ) {
//    val state by viewModel.collectAsState()
//    val lifecycle = LocalLifecycleOwner.current.lifecycle
//    val snackBarHostState = remember { SnackbarHostState() }
//    val scope = rememberCoroutineScope()
//    val context = LocalContext.current
//
//    if (state.isLoading) {
//        LoadingScreen(text = state.message)
//    } else {
//        HomeContent(
//            navController = navController,
//            user = (state.state as? LCE.Success)?.value,
//            cardList = state.cards,
//            onSyncCardsClick = {
//                viewModel.process(HomeViewModel.Action.SyncCards(context))
//            },
//            onCardClick = {
//                navController.navigateToCardList(it)
//            },
//            networkStatus = state.networkStatus,
//            lastSyncUpdateDate = state.lastSyncUpdate,
//            showSyncCards = state.showSyncCards,
//            showSyncCatalogs = state.showSyncCatalogsCard,
//            onSyncCatalogsClick = {
//                viewModel.process(HomeViewModel.Action.SyncCatalogs(LOAD_CATALOGS))
//            },
//            showSyncRemoteCards = state.showSyncRemoteCards,
//            onSyncRemoteCardsClick = {
//                viewModel.process(HomeViewModel.Action.SyncRemoteCards)
//            }
//        )
//    }
//
//    SnackbarHost(hostState = snackBarHostState) {
//        Snackbar(
//            snackbarData = it,
//            containerColor = MaterialTheme.colorScheme.error,
//            contentColor = Color.White,
//            modifier = Modifier.padding(top = PaddingToolbar)
//        )
//    }
//
//    LaunchedEffect(viewModel, lifecycle) {
//        snapshotFlow { state }
//            .flowWithLifecycle(lifecycle)
//            .collect {
//                if (state.syncCatalogs && state.syncCompleted.not()) {
//                    viewModel.process(HomeViewModel.Action.SyncCatalogs(syncCatalogs))
//                } else if (state.isSyncing.not()) {
//                    viewModel.process(HomeViewModel.Action.GetCards)
//                }
//                if (state.message.isNotEmpty() && state.isLoading.not()) {
//                    scope.launch {
//                        snackBarHostState.showSnackbar(
//                            message = state.message
//                        )
//                        viewModel.process(HomeViewModel.Action.ClearMessage)
//                    }
//                }
//                if (state.updateApp) {
//                    context.getActivity<MainActivity>()
//                        ?.showUpdateDialog()
//                }
//            }
//    }
// }
//
// @OptIn(ExperimentalFoundationApi::class)
// @Composable
// private fun HomeContent(
//    navController: NavController,
//    user: User?,
//    cardList: List<Card>,
//    onSyncCardsClick: () -> Unit,
//    onCardClick: (String) -> Unit,
//    networkStatus: NetworkStatus,
//    lastSyncUpdateDate: String,
//    showSyncCards: Boolean,
//    showSyncCatalogs: Boolean,
//    onSyncCatalogsClick: () -> Unit,
//    showSyncRemoteCards: Boolean,
//    onSyncRemoteCardsClick: () -> Unit
// ) {
//    Scaffold { padding ->
//        LazyColumn(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            stickyHeader {
//                user?.let {
//                    HomeAppBar(
//                        navController = navController,
//                        user = it,
//                        padding = padding.calculateTopPadding(),
//                        networkStatus = networkStatus
//                    )
//                }
//            }
//
//            item {
//                AnimatedVisibility(visible = showSyncRemoteCards) {
//                    HomeSectionCardItem(
//                        title = stringResource(R.string.sync_remote_cards),
//                        icon = Icons.Outlined.Refresh,
//                        description = stringResource(R.string.sync_remote_cards_description),
//                        subText = stringResource(R.string.important),
//                        subTextColor = MaterialTheme.colorScheme.error
//                    ) {
//                        onSyncRemoteCardsClick()
//                    }
//                }
//
//                AnimatedVisibility(visible = showSyncCards) {
//                    HomeSectionCardItem(
//                        title = stringResource(R.string.sync_cards),
//                        icon = Icons.Outlined.Refresh,
//                        subText =
//                        stringResource(
//                            R.string.last_update,
//                            lastSyncUpdateDate
//                        ),
//                        description =
//                        stringResource(
//                            R.string.local_cards,
//                            cardList.toLocalCards().size
//                        )
//                    ) {
//                        onSyncCardsClick()
//                    }
//                }
//                AnimatedVisibility(visible = showSyncCatalogs) {
//                    HomeSectionCardItem(
//                        title = stringResource(R.string.sync_remote_catalogs),
//                        icon = Icons.Outlined.Refresh,
//                        description = stringResource(R.string.you_have_new_catalogs_to_sync),
//                        subText = stringResource(R.string.important),
//                        subTextColor = MaterialTheme.colorScheme.error
//                    ) {
//                        onSyncCatalogsClick()
//                    }
//                }
//                HomeSectionCardItem(
//                    title = stringResource(R.string.anomalies_cards),
//                    icon = Icons.Outlined.Build,
//                    description =
//                    if (cardList.isNotEmpty()) {
//                        stringResource(
//                            R.string.total_cards,
//                            cardList.toAnomaliesList().size
//                        )
//                    } else {
//                        EMPTY
//                    }
//                ) {
//                    onCardClick(CARD_ANOMALIES)
//                }
//            }
//        }
//    }
// }
//
// @Composable
// private fun HomeSectionCardItem(
//    title: String,
//    icon: ImageVector,
//    description: String = EMPTY,
//    subText: String = EMPTY,
//    subTextColor: Color = Color.Unspecified,
//    onClick: () -> Unit
// ) {
//    Card(
//        modifier =
//        Modifier
//            .fillMaxWidth()
//            .padding(PaddingNormal),
//        onClick = onClick
//    ) {
//        Column(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            CustomSpacer()
//            Icon(icon, contentDescription = EMPTY, tint = getPrimaryColor())
//            Text(
//                text = title,
//                style =
//                MaterialTheme.typography.titleLarge.copy(
//                    color = getPrimaryColor()
//                ),
//                modifier = Modifier.padding(PaddingNormal)
//            )
//            AnimatedVisibility(visible = description.isNotEmpty()) {
//                Column(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = description,
//                        style =
//                        MaterialTheme.typography.bodyMedium
//                            .copy(fontWeight = FontWeight.SemiBold),
//                        modifier = Modifier.padding(bottom = Size2)
//                    )
//                    AnimatedVisibility(visible = subText.isNotEmpty()) {
//                        Text(
//                            text = subText,
//                            style =
//                            MaterialTheme.typography.bodySmall
//                                .copy(color = subTextColor)
//                        )
//                    }
//                    CustomSpacer()
//                }
//            }
//        }
//    }
// }
//
// @Composable
// private fun HomeAppBar(
//    navController: NavController,
//    user: User,
//    padding: Dp,
//    networkStatus: NetworkStatus
// ) {
//    Column(
//        modifier = Modifier.headerContent(padding),
//        horizontalAlignment = Alignment.End
//    ) {
//        Icon(
//            Icons.Outlined.Settings,
//            contentDescription = stringResource(R.string.empty),
//            tint = getColor(),
//            modifier =
//            Modifier.clickable {
//                navController.navigateToAccount()
//            }
//        )
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            CircularImage(image = user.logo)
//            CustomSpacer(direction = SpacerDirection.HORIZONTAL)
//            Column {
//                Text(
//                    text = stringResource(R.string.welcome_back, user.name),
//                    style =
//                    MaterialTheme.typography.titleMedium.copy(
//                        fontWeight = FontWeight.Bold,
//                        color = getColor()
//                    )
//                )
//                Text(
//                    text = user.companyName,
//                    style =
//                    MaterialTheme.typography.bodyMedium.copy(
//                        color = getColor()
//                    )
//                )
//                CustomSpacer(space = SpacerSize.TINY)
//                LazyRow {
//                    items(user.roles) {
//                        Box(modifier = Modifier.padding(horizontal = 2.dp)) {
//                            CustomTag(
//                                title = it,
//                                tagSize = TagSize.SMALL,
//                                tagType = TagType.OUTLINE,
//                                invertedColors = true
//                            )
//                        }
//                    }
//                }
//            }
//        }
//        CustomSpacer()
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            NetworkCard(networkStatus = networkStatus)
//            Icon(
//                painter = painterResource(id = R.drawable.ic_qr_scan),
//                contentDescription = EMPTY,
//                tint = getColor(),
//                modifier =
//                Modifier.clickable {
//                    navController.navigateToQrScanner()
//                }
//            )
//        }
//        CustomSpacer()
//    }
// }

// @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
// @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
// @Preview(showBackground = true, name = "light")
// @Composable
// private fun HomeScreenPreview() {
//    OsmAppTheme {
//        Scaffold(modifier = Modifier.fillMaxSize()) {
//            HomeContent(
//                navController = rememberNavController(),
//                user = User.mockUser(),
//                cardList = emptyList(),
//                onSyncCardsClick = {},
//                onCardClick = {},
//                networkStatus = NetworkStatus.WIFI_CONNECTED,
//                lastSyncUpdateDate = "",
//                showSyncCards = true,
//                showSyncCatalogs = true,
//                onSyncCatalogsClick = {},
//                showSyncRemoteCards = true,
//                onSyncRemoteCardsClick = {}
//            )
//        }
//    }
// }
