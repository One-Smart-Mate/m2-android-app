package com.osm.ui.pages.createcard

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.ih.osm.R
import com.osm.domain.model.Card
import com.osm.domain.model.Evidence
import com.osm.domain.model.EvidenceType
import com.osm.domain.model.NodeCardItem
import com.osm.domain.model.toAudios
import com.osm.domain.model.toImages
import com.osm.domain.model.toVideos
import com.osm.ui.components.CustomAppBar
import com.osm.ui.components.CustomSpacer
import com.osm.ui.components.CustomTextField
import com.osm.ui.components.ExpandableCard
import com.osm.ui.components.RadioGroup
import com.osm.ui.components.LoadingScreen
import com.osm.ui.components.SpacerSize
import com.osm.ui.components.buttons.CustomButton
import com.osm.ui.components.card.CardItemList
import com.osm.ui.components.card.SectionCardEvidence
import com.osm.ui.components.evidence.SectionAudiosEvidence
import com.osm.ui.components.evidence.SectionImagesEvidence
import com.osm.ui.components.evidence.SectionVideosEvidence
import com.osm.ui.extensions.defaultScreen
import com.osm.ui.extensions.getColor
import com.osm.ui.extensions.getIconColor
import com.osm.ui.extensions.getPrimaryColor
import com.osm.ui.components.card.CardSectionItemList
import com.osm.ui.theme.OsmAppTheme
import com.osm.ui.theme.PaddingNormal
import com.osm.ui.theme.PaddingTiny
import com.osm.ui.theme.PaddingToolbar
import com.osm.ui.theme.Size100
import com.osm.ui.theme.Size180
import com.osm.ui.utils.EMPTY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CreateCardScreen(
    navController: NavController,
    viewModel: CreateCardViewModel = mavericksViewModel(),
    filter: String = EMPTY
) {
    val state by viewModel.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val snackBarHostState = remember { SnackbarHostState() }
    val lazyState = rememberLazyListState()
    val scope = rememberCoroutineScope()


    if (state.isLoading) {
        LoadingScreen(state.message)
    } else {
        CreateCardContent(
            navController = navController,
            cardTypeList = state.cardTypeList,
            onCardTypeClick = {
                viewModel.process(CreateCardViewModel.Action.SetCardType(it.id))
            },
            selectedCardType = state.selectedCardType,
            preclassifierList = state.preclassifierList,
            onPreclassifierClick = {
                viewModel.process(CreateCardViewModel.Action.SetPreclassifier(it.id))
            },
            selectedPreclassifier = state.selectedPreclassifier,
            priorityList = state.priorityList,
            onPriorityClick = {
                viewModel.process(CreateCardViewModel.Action.SetPriority(it.id))
            },
            selectedPriority = state.selectedPriority,
            levelList = state.nodeLevelList,
            onLevelClick = { item, key ->
                viewModel.process(CreateCardViewModel.Action.SetLevel(item.id, key))
            },
            selectedLevelList = state.selectedLevelList,
            lastLevelCompleted = state.lastLevelCompleted,
            onCommentChange = {
                viewModel.process(CreateCardViewModel.Action.OnCommentChange(it))
            },
            isSecureCard = state.isSecureCard,
            selectedSecureOption = state.selectedSecureOption,
            onSecureOptionChange = {
                viewModel.process(CreateCardViewModel.Action.OnSecureOptionChange(it))
            },
            evidences = state.evidences,
            onAddEvidence = { uri, type ->
                viewModel.process(CreateCardViewModel.Action.OnAddEvidence(uri, type))
            },
            onDeleteEvidence = {
                viewModel.process(CreateCardViewModel.Action.OnDeleteEvidence(it))
            },
            onSaveCard = {
                viewModel.process(CreateCardViewModel.Action.OnSaveCard)
            },
            audioDuration = state.audioDuration,
            cardsZone = state.cardsZone,
            coroutineScope = scope,
            lazyColumState = lazyState
        )
    }

    SnackbarHost(hostState = snackBarHostState) {
        Snackbar(
            snackbarData = it,
            containerColor = Color.Red,
            contentColor = Color.White,
            modifier = Modifier.padding(top = PaddingToolbar)
        )
    }


    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { state }
            .flowWithLifecycle(lifecycle)
            .collect {
                if (filter.isNotEmpty()) {
                    viewModel.process(CreateCardViewModel.Action.GetCardTypes(filter))
                }
                if (state.isCardSuccess) {
                    navController.popBackStack()
                }
                if (state.message.isNotEmpty() && state.isLoading.not()) {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = state.message,
                        )
                        viewModel.process(CreateCardViewModel.Action.ClearMessage)
                    }
                }
            }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreateCardContent(
    navController: NavController,
    cardTypeList: List<NodeCardItem>,
    onCardTypeClick: (NodeCardItem) -> Unit,
    selectedCardType: String,
    preclassifierList: List<NodeCardItem>,
    onPreclassifierClick: (NodeCardItem) -> Unit,
    selectedPreclassifier: String,
    priorityList: List<NodeCardItem>,
    onPriorityClick: (NodeCardItem) -> Unit,
    selectedPriority: String,
    levelList: Map<Int, List<NodeCardItem>>,
    onLevelClick: (NodeCardItem, key: Int) -> Unit,
    selectedLevelList: Map<Int, String>,
    lastLevelCompleted: Boolean,
    onCommentChange: (String) -> Unit,
    isSecureCard: Boolean = false,
    selectedSecureOption: String = EMPTY,
    onSecureOptionChange: ((String) -> Unit)? = null,
    evidences: List<Evidence>,
    onAddEvidence: (Uri, EvidenceType) -> Unit,
    onDeleteEvidence: (Evidence) -> Unit,
    onSaveCard: () -> Unit,
    audioDuration: Int,
    cardsZone: List<Card>,
    coroutineScope: CoroutineScope,
    lazyColumState: LazyListState
) {


    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding),
            state = lazyColumState
        ) {
            stickyHeader {
                CustomAppBar(
                    navController = navController,
                    title = stringResource(R.string.create_card)
                )
            }
            item {
                CustomSpacer()
                CardTypeContent(cardTypeList, onCardTypeClick, selectedCardType)
                PreclassifierContent(preclassifierList, onPreclassifierClick, selectedPreclassifier)
                PriorityContent(priorityList, onPriorityClick, selectedPriority)
                LevelContent(levelList, onLevelClick = { item, key ->
                    onLevelClick(item, key)
                    coroutineScope.launch {
                        lazyColumState.scrollToItem(lazyColumState.layoutInfo.totalItemsCount)
                    }
                }, selectedLevelList)
                CustomSpacer(space = SpacerSize.EXTRA_LARGE)
            }

            item {
                if (lastLevelCompleted) {
                    CustomSpacer()
                    HorizontalDivider()
                    SectionCardEvidence(
                        audioDuration = audioDuration,
                        onAddEvidence = onAddEvidence,
                        imageType = EvidenceType.IMCR,
                        audioType = EvidenceType.AUCR,
                        videoType = EvidenceType.VICR
                    )
                    SectionImagesEvidence(imageEvidences = evidences.toImages()) {
                        onDeleteEvidence(it)
                    }
                    SectionVideosEvidence(videoEvidences = evidences.toVideos()) {
                        onDeleteEvidence(it)
                    }
                    SectionAudiosEvidence(audioEvidences = evidences.toAudios()) {
                        onDeleteEvidence(it)
                    }
                    CustomSpacer()
                }
            }

            item {
                HorizontalDivider()
                CustomSpacer(space = SpacerSize.EXTRA_LARGE)
                if (lastLevelCompleted) {
                    Text(
                        text = stringResource(R.string.comments),
                        style = MaterialTheme.typography.titleLarge
                            .copy(fontWeight = FontWeight.Bold)
                    )
                    CustomSpacer()
                    CustomTextField(
                        label = stringResource(R.string.comments),
                        icon = Icons.Filled.Create,
                        modifier = Modifier.fillParentMaxWidth(),
                        maxLines = 5
                    ) {
                        onCommentChange(it)
                    }
                    CustomSpacer()
//                    if (isSecureCard) {
//                        CustomSpacer()
//                        Text(
//                            text = stringResource(R.string.card_type),
//                            style = MaterialTheme.typography.titleLarge
//                                .copy(fontWeight = FontWeight.Bold)
//                        )
//                        RadioGroup(
//                            modifier = Modifier.fillParentMaxWidth(),
//                            items = listOf(
//                                stringResource(R.string.safe),
//                                stringResource(R.string.unsafe)
//                            ),
//                            selection = selectedSecureOption
//                        ) {
//                            if (onSecureOptionChange != null) {
//                                onSecureOptionChange(it)
//                            }
//                        }
//                        CustomSpacer()
//                    }

                    AnimatedVisibility(visible = cardsZone.isNotEmpty()) {
                        ExpandableCard(title = stringResource(R.string.existing_cards_zone)) {
                            cardsZone.map {
                                CardItemList(
                                    card = it,
                                    isActionsEnabled = false,
                                    onClick = {},
                                    onSolutionClick = {})
                            }
                        }
                    }
                    CustomSpacer()
                    CustomButton(text = stringResource(R.string.save)) {
                        onSaveCard()
                    }
                }
            }
        }
    }
}


@Composable
fun LevelContent(
    levelList: Map<Int, List<NodeCardItem>>,
    onLevelClick: (NodeCardItem, key: Int) -> Unit,
    selectedLevelList: Map<Int, String>,
) {
    if (levelList.isNotEmpty()) {
        levelList.map { level ->
            if (level.value.isNotEmpty()) {
                Text(
                    text = "${stringResource(R.string.level)} ${level.key}",
                    style = MaterialTheme.typography.titleLarge
                        .copy(fontWeight = FontWeight.Bold)
                )
            }
            LazyRow {
                items(level.value) { item ->
                    SectionItemCard(
                        title = item.name,
                        description = item.description,
                        selected = item.id == selectedLevelList[level.key]
                    ) {
                        onLevelClick(item, level.key)
                    }
                }
            }
        }
    }
}


@Composable
fun PriorityContent(
    priorityList: List<NodeCardItem>,
    onPriorityClick: (NodeCardItem) -> Unit,
    selectedPriority: String,
) {
    if (priorityList.isNotEmpty()) {
        Text(
            text = stringResource(R.string.priority),
            style = MaterialTheme.typography.titleLarge
                .copy(fontWeight = FontWeight.Bold)
        )
        LazyRow {
            items(priorityList) {
                SectionItemCard(
                    title = it.name,
                    description = it.description,
                    selected = it.id == selectedPriority
                ) {
                    onPriorityClick(it)
                }
            }
        }
    }
}

@Composable
fun PreclassifierContent(
    preclassifierList: List<NodeCardItem>,
    onPreclassifierClick: (NodeCardItem) -> Unit,
    selectedPreclassifier: String,
) {
    if (preclassifierList.isNotEmpty()) {
        Text(
            text = stringResource(R.string.type_of_problem),
            style = MaterialTheme.typography.titleLarge
                .copy(fontWeight = FontWeight.Bold)
        )
        LazyRow {
            items(preclassifierList) {
                SectionItemCard(
                    title = it.name,
                    description = it.description,
                    selected = it.id == selectedPreclassifier
                ) {
                    onPreclassifierClick(it)
                }
            }
        }
    }
}


@Composable
fun CardTypeContent(
    cardTypeList: List<NodeCardItem>,
    onCardTypeClick: (NodeCardItem) -> Unit,
    selectedCardType: String,

    ) {
    Text(
        text = stringResource(R.string.card_types),
        style = MaterialTheme.typography.titleLarge
            .copy(fontWeight = FontWeight.Bold)
    )
    LazyRow {
        items(cardTypeList) {
            SectionItemCard(
                title = it.name,
                description = it.description,
                selected = it.id == selectedCardType
            ) {
                onCardTypeClick(it)
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
    onClick: (() -> Unit?)? = null
) {
    Box(
        contentAlignment = Alignment.BottomCenter
    ) {
        GlideImage(
            model = model, contentDescription = stringResource(id = R.string.empty),
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
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.padding(8.dp),
        onClick = onClick
    ) {
        Icon(
            painter = icon,
            contentDescription = stringResource(id = R.string.empty),
            tint = color,
            modifier = Modifier.padding(8.dp)
        )
    }
}


@Composable
fun SectionItemCard(
    title: String,
    description: String,
    selected: Boolean = false,
    onItemClick: () -> Unit
) {
    val color = if (selected) {
        CardDefaults.cardColors(
            contentColor = getColor(),
            containerColor = getPrimaryColor()
        )
    } else {
        CardDefaults.cardColors()
    }
    Card(
        modifier = Modifier
            .padding(PaddingTiny)
            .width(Size180)
            .height(Size100),
        colors = color,
        onClick = {
            onItemClick()
        }
    ) {
        Column(
            modifier = Modifier
                .padding(PaddingNormal)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium
                    .copy(fontWeight = FontWeight.W700)
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


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun CreateCardPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            CreateCardContent(
                navController = rememberNavController(),
                cardTypeList = emptyList(),
                onCardTypeClick = {},
                selectedCardType = "",
                preclassifierList = emptyList(),
                onPreclassifierClick = {},
                selectedPreclassifier = "",
                priorityList = emptyList(),
                onPriorityClick = {},
                selectedPriority = "",
                levelList = emptyMap(),
                onLevelClick = { _, _ -> },
                selectedLevelList = emptyMap(),
                lastLevelCompleted = true,
                onCommentChange = {},
                evidences = emptyList(),
                onAddEvidence = { _, _ -> },
                onDeleteEvidence = {},
                onSaveCard = {},
                audioDuration = 60,
                cardsZone = emptyList(),
                coroutineScope = rememberCoroutineScope(),
                lazyColumState = rememberLazyListState()
            )
        }
    }
}