package com.ih.osm.ui.pages.createcard

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.ih.osm.R
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.EvidenceType
import com.ih.osm.domain.model.NodeCardItem
import com.ih.osm.domain.model.toAudios
import com.ih.osm.domain.model.toImages
import com.ih.osm.domain.model.toVideos
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.CustomTextField
import com.ih.osm.ui.components.ExpandableCard
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.MachineIdSearchField
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.components.card.CardItemListV2
import com.ih.osm.ui.components.card.SectionCardEvidence
import com.ih.osm.ui.components.evidence.SectionAudiosEvidence
import com.ih.osm.ui.components.evidence.SectionImagesEvidence
import com.ih.osm.ui.components.evidence.SectionVideosEvidence
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.extensions.getColor
import com.ih.osm.ui.extensions.getIconColor
import com.ih.osm.ui.extensions.getPrimaryColor
import com.ih.osm.ui.pages.createcard.action.CreateCardAction
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.PaddingTiny
import com.ih.osm.ui.theme.PaddingToolbar
import com.ih.osm.ui.theme.Size115
import com.ih.osm.ui.theme.Size180
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CreateCardScreen(
    navController: NavController,
    filter: String? = null,
    viewModel: CreateCardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val lazyState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    var searchText by remember { mutableStateOf("") }

    // Row states reactivos — guardamos en un MutableStateList para poder cambiar tamaño sin recrear Composable
    val rowStates = remember { mutableStateListOf<LazyListState>() }

    // Ajustar rowStates cuando cambie el número de niveles
    LaunchedEffect(state.filteredNodeLevelList.size) {
        val needed = state.filteredNodeLevelList.size
        // Añadir estados si faltan
        while (rowStates.size < needed) rowStates.add(LazyListState())
        // Quitar si sobran
        while (rowStates.size > needed) rowStates.removeLast()
    }

    // Handle cilt mode from filter
    LaunchedEffect(filter) {
        if (filter?.startsWith("cilt:") == true) {
            val superiorId = filter.removePrefix("cilt:")
            viewModel.setCiltMode(true)
            viewModel.setSuperiorIdCilt(superiorId)
        }
    }

    // Update search text to ViewModel (debounced)

    // Collect scrollTarget events from ViewModel
    LaunchedEffect(viewModel) {
        viewModel.scrollTarget.collect { target ->
            // Vertical scroll: clamp index y animar
            target.verticalIndex?.let { vIndex ->
                val verticalIndexClamped = vIndex.coerceAtLeast(0)
                scope.launch {
                    // clamp to available items in lazyState (safe)
                    val maxIndex = (lazyState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0)
                    val targetIndex = verticalIndexClamped.coerceAtMost(maxIndex)
                    lazyState.animateScrollToItem(targetIndex)
                }
            }

            // Horizontal (LazyRow) scroll: nivel y item
            val lvl = target.levelIndex
            val itm = target.itemIndex
            if (lvl != null && itm != null) {
                // asegurarnos de que rowStates tenga ese índice
                if (lvl in rowStates.indices) {
                    scope.launch {
                        // clamp against la cantidad de items en ese LazyRow
                        val listState = rowStates[lvl]
                        // layoutInfo puede estar vacío si aún no se ha medido; intentamos animar directamente
                        try {
                            val maxItem = (listState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0)
                            val targetItem = itm.coerceAtLeast(0).coerceAtMost(maxItem)
                            listState.animateScrollToItem(targetItem)
                        } catch (e: Exception) {
                            // Si falla por layout no listo, hacemos animate sin clamping (seguirá funcionando cuando se mida)
                            listState.animateScrollToItem(itm)
                        }
                    }
                }
            }
        }
    }

    if (state.isLoading) {
        LoadingScreen(state.message)
    } else {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    },
        ) {
            CreateCardContent(
                navController = navController,
                cardTypeList = state.cardTypeList,
                selectedCardType = state.selectedCardType,
                preclassifierList = state.preclassifierList,
                selectedPreclassifier = state.selectedPreclassifier,
                priorityList = state.priorityList,
                selectedPriority = state.selectedPriority,
                selectedLevelList = state.selectedLevelList,
                lastLevelCompleted = state.lastLevelCompleted,
                evidences = state.evidences,
                audioDuration = state.audioDuration,
                cardsZone = state.cardsZone,
                coroutineScope = scope,
                lazyColumState = lazyState,
                searchText = searchText,
                rowStates = rowStates, // new
                onSearchChange = {
                    searchText = it
                },
                onAction = { action -> viewModel.process(action) },
                filteredNodeLevelList = state.filteredNodeLevelList,
                viewModel = viewModel,
            )
        }
    }

    SnackbarHost(hostState = snackBarHostState) {
        Snackbar(
            snackbarData = it,
            containerColor = Color.Red,
            contentColor = Color.White,
            modifier = Modifier.padding(top = PaddingToolbar),
        )
    }

    // Navigation & SnackBar
    LaunchedEffect(viewModel) {
        snapshotFlow { state }
            .distinctUntilChanged()
            .collect {
                if (state.isCardSuccess) {
                    navController.popBackStack()
                }
                if (state.message.isNotEmpty() && state.isLoading.not()) {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = state.message,
                        )
                        viewModel.cleanMessage()
                    }
                }
            }
    }
}

@Composable
fun CreateCardContent(
    navController: NavController,
    cardTypeList: List<NodeCardItem>,
    selectedCardType: String,
    preclassifierList: List<NodeCardItem>,
    selectedPreclassifier: String,
    priorityList: List<NodeCardItem>,
    selectedPriority: String,
    filteredNodeLevelList: Map<Int, List<NodeCardItem>>,
    selectedLevelList: Map<Int, String>,
    lastLevelCompleted: Boolean,
    evidences: List<Evidence>,
    audioDuration: Int,
    cardsZone: List<Card>,
    coroutineScope: CoroutineScope,
    lazyColumState: LazyListState,
    searchText: String,
    rowStates: List<LazyListState>, // new param
    onSearchChange: (String) -> Unit,
    viewModel: CreateCardViewModel,
    onAction: (CreateCardAction) -> Unit,
) {
    var localText by remember { mutableStateOf("") }
    Scaffold { padding ->
        LazyColumn(
            modifier =
                Modifier
                    .defaultScreen(padding)
                    .imePadding(),
            state = lazyColumState,
        ) {
            stickyHeader {
                CustomAppBar(navController = navController, title = stringResource(R.string.create_card))
            }

            item {
                CustomSpacer()
                CardTypeContent(cardTypeList, onAction, selectedCardType)
                PreclassifierContent(preclassifierList, onAction, selectedPreclassifier)
                PriorityContent(priorityList, onAction, selectedPriority)

                AnimatedVisibility(selectedPriority.isEmpty().not()) {
                    CustomSpacer()
                    MachineIdSearchField(
                        modifier = Modifier.fillParentMaxWidth(),
                        searchQuery = localText,
                        onSearchQueryChange = {
                            localText = it
                        },
                        onSearch = {
                            viewModel.updateSearchText(localText)
                        },
                        isSearching = false,
                        errorMessage = "",
                        successMessage = false,
                    )
                    CustomSpacer()
                }

                AnimatedVisibility(selectedPriority.isEmpty().not()) {
                    LevelContent(
                        levelList = filteredNodeLevelList,
                        selectedLevelList = selectedLevelList,
                        rowStates = rowStates, // pass states
                        onLevelClick = { item, key ->
                            onAction(CreateCardAction.SetLevel(item.id, key))
                        },
                    )
                }

                CustomSpacer(space = SpacerSize.EXTRA_LARGE)
            }

            // Evidence Section
            item {
                AnimatedVisibility(visible = lastLevelCompleted) {
                    Column {
                        CustomSpacer()
                        HorizontalDivider()
                        SectionCardEvidence(
                            audioDuration = audioDuration,
                            onAddEvidence = { uri, type ->
                                onAction(CreateCardAction.AddEvidence(uri, type))
                            },
                            imageType = EvidenceType.IMCR,
                            audioType = EvidenceType.AUCR,
                            videoType = EvidenceType.VICR,
                        )
                        SectionImagesEvidence(imageEvidences = evidences.toImages()) {
                            onAction(CreateCardAction.DeleteEvidence(it))
                        }
                        SectionVideosEvidence(videoEvidences = evidences.toVideos()) {
                            onAction(CreateCardAction.DeleteEvidence(it))
                        }
                        SectionAudiosEvidence(audioEvidences = evidences.toAudios()) {
                            onAction(CreateCardAction.DeleteEvidence(it))
                        }
                        CustomSpacer()
                    }
                }
            }

            // Comments & Save Button
            item {
                AnimatedVisibility(visible = lastLevelCompleted) {
                    Column {
                        HorizontalDivider()
                        CustomSpacer(space = SpacerSize.EXTRA_LARGE)
                        Text(
                            text = stringResource(R.string.comments),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        )
                        CustomSpacer()
                        CustomTextField(
                            label = stringResource(R.string.comments),
                            icon = Icons.Filled.Create,
                            modifier = Modifier.fillParentMaxWidth(),
                            maxLines = 5,
                        ) { onAction(CreateCardAction.SetComment(it)) }
                        CustomSpacer()
                        AnimatedVisibility(visible = cardsZone.isNotEmpty()) {
                            ExpandableCard(title = stringResource(R.string.existing_cards_zone)) {
                                cardsZone.forEach {
                                    CardItemListV2(
                                        card = it,
                                        isActionsEnabled = false,
                                        onClick = {},
                                        onAction = {},
                                    )
                                }
                            }
                        }
                        CustomSpacer()
                        CustomButton(text = stringResource(R.string.save)) {
                            onAction(CreateCardAction.Save)
                        }
                        CustomSpacer(space = SpacerSize.EXTRA_LARGE)
                    }
                }
            }
        }
    }
}

@Composable
fun LevelContent(
    levelList: Map<Int, List<NodeCardItem>>,
    selectedLevelList: Map<Int, String>,
    rowStates: List<LazyListState>, // new
    onLevelClick: (NodeCardItem, key: Int) -> Unit,
) {
    AnimatedVisibility(visible = levelList.isNotEmpty()) {
        Column {
            // Convert to sorted list of pairs and iterate with index
            levelList.toSortedMap().toList().forEachIndexed { index, entry ->
                val (levelNumber, items) = entry

                if (items.isNotEmpty()) {
                    Text(
                        text = "${stringResource(R.string.level)} $levelNumber",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 4.dp),
                    )
                }

                // Use safe index for rowStates; if missing, fallback to a local state
                val lazyStateForRow = rowStates.getOrNull(index) ?: remember { LazyListState() }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    state = lazyStateForRow,
                ) {
                    items(items) { item ->
                        SectionItemCard(
                            title = item.name,
                            description = item.description,
                            selected = item.id == selectedLevelList[levelNumber],
                        ) {
                            onLevelClick(item, levelNumber)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PriorityContent(
    priorityList: List<NodeCardItem>,
    onAction: (CreateCardAction) -> Unit,
    selectedPriority: String,
) {
    AnimatedVisibility(visible = priorityList.isNotEmpty()) {
        Column {
            Text(
                text = stringResource(R.string.priority),
                style =
                    MaterialTheme.typography.titleLarge
                        .copy(fontWeight = FontWeight.Bold),
            )
            LazyRow {
                items(priorityList) {
                    SectionItemCard(
                        title = it.name,
                        description = it.description,
                        selected = it.id == selectedPriority,
                    ) {
                        onAction(CreateCardAction.SetPriority(it.id))
                    }
                }
            }
        }
    }
}

@Composable
fun PreclassifierContent(
    preclassifierList: List<NodeCardItem>,
    onAction: (CreateCardAction) -> Unit,
    selectedPreclassifier: String,
) {
    AnimatedVisibility(visible = preclassifierList.isNotEmpty()) {
        Column {
            Text(
                text = stringResource(R.string.type_of_problem),
                style =
                    MaterialTheme.typography.titleLarge
                        .copy(fontWeight = FontWeight.Bold),
            )
            LazyRow {
                items(preclassifierList) {
                    SectionItemCard(
                        title = it.name,
                        description = it.description,
                        selected = it.id == selectedPreclassifier,
                    ) {
                        onAction(CreateCardAction.SetPreclassifier(it.id))
                    }
                }
            }
        }
    }
}

@Composable
fun CardTypeContent(
    cardTypeList: List<NodeCardItem>,
    onAction: (CreateCardAction) -> Unit,
    selectedCardType: String,
) {
    Text(
        text = stringResource(R.string.card_types),
        style =
            MaterialTheme.typography.titleLarge
                .copy(fontWeight = FontWeight.Bold),
    )
    LazyRow {
        items(cardTypeList) {
            SectionItemCard(
                title = it.name,
                description = it.description,
                selected = it.id == selectedCardType,
            ) {
                onAction(CreateCardAction.SetCardType(it.id))
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PhotoCardItem(
    modifier: Modifier = Modifier,
    model: Any,
    showIcon: Boolean = true,
    onClick: (() -> Unit?)? = null,
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
    ) {
        GlideImage(
            model = model,
            contentDescription = stringResource(id = R.string.empty),
            failure = placeholder(R.drawable.loading_image),
            loading = placeholder(R.drawable.loading_image),
            modifier = modifier.padding(PaddingTiny),
        )
        if (showIcon) {
            Box {
                CardItemIcon(icon = painterResource(id = R.drawable.ic_delete)) {
                    if (onClick != null) {
                        onClick()
                    }
                }
            }
        }
    }
}

@Composable
fun CardItemIcon(
    icon: Painter,
    modifier: Modifier = Modifier,
    color: Color = getIconColor(),
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.padding(8.dp),
        onClick = onClick,
    ) {
        Icon(
            painter = icon,
            contentDescription = stringResource(id = R.string.empty),
            tint = color,
            modifier = Modifier.padding(8.dp),
        )
    }
}

@Composable
fun SectionItemCard(
    title: String,
    description: String,
    selected: Boolean = false,
    onItemClick: () -> Unit,
) {
    val color =
        if (selected) {
            CardDefaults.cardColors(
                contentColor = getColor(),
                containerColor = getPrimaryColor(),
            )
        } else {
            CardDefaults.cardColors()
        }
    Card(
        modifier =
            Modifier
                .padding(PaddingTiny)
                .sizeIn(minWidth = Size180, maxWidth = Size180, minHeight = Size115),
        colors = color,
        onClick = {
            onItemClick()
        },
    ) {
        Column(
            modifier =
                Modifier
                    .padding(PaddingNormal)
                    .fillMaxWidth(),
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style =
                    MaterialTheme.typography.bodyMedium
                        .copy(fontWeight = FontWeight.W700),
            )
            CustomSpacer(space = SpacerSize.TINY)
            Text(
                text = description,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
