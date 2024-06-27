package com.ih.m2.ui.pages.createcard

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.ih.m2.MainActivity
import com.ih.m2.R
import com.ih.m2.domain.model.Evidence
import com.ih.m2.domain.model.EvidenceType
import com.ih.m2.domain.model.NodeCardItem
import com.ih.m2.domain.model.toAudios
import com.ih.m2.domain.model.toImages
import com.ih.m2.domain.model.toVideos
import com.ih.m2.ui.components.CustomAppBar
import com.ih.m2.ui.components.CustomSpacer
import com.ih.m2.ui.components.CustomTextField
import com.ih.m2.ui.components.RadioGroup
import com.ih.m2.ui.components.ScreenLoading
import com.ih.m2.ui.components.SpacerSize
import com.ih.m2.ui.components.VideoPlayer
import com.ih.m2.ui.components.buttons.CustomButton
import com.ih.m2.ui.components.card.SectionCardEvidence
import com.ih.m2.ui.components.evidence.SectionAudiosEvidence
import com.ih.m2.ui.components.evidence.SectionImagesEvidence
import com.ih.m2.ui.components.evidence.SectionVideosEvidence
import com.ih.m2.ui.components.launchers.AudioLauncher
import com.ih.m2.ui.components.launchers.CameraLauncher
import com.ih.m2.ui.components.launchers.VideoLauncher
import com.ih.m2.ui.components.sheets.RecordAudioBottomSheet
import com.ih.m2.ui.extensions.defaultScreen
import com.ih.m2.ui.extensions.getActivity
import com.ih.m2.ui.extensions.getColor
import com.ih.m2.ui.extensions.getIconColor
import com.ih.m2.ui.extensions.getPrimaryColor
import com.ih.m2.ui.extensions.runWorkRequest
import com.ih.m2.ui.navigation.navigateToHome
import com.ih.m2.ui.pages.carddetail.EvidenceImagesCardSection
import com.ih.m2.ui.theme.M2androidappTheme
import com.ih.m2.ui.theme.PaddingLarge
import com.ih.m2.ui.theme.PaddingNormal
import com.ih.m2.ui.theme.PaddingTiny
import com.ih.m2.ui.theme.PaddingToolbar
import com.ih.m2.ui.theme.Size100
import com.ih.m2.ui.theme.Size110
import com.ih.m2.ui.theme.Size120
import com.ih.m2.ui.theme.Size160
import com.ih.m2.ui.theme.Size170
import com.ih.m2.ui.theme.Size180
import com.ih.m2.ui.theme.Size200
import com.ih.m2.ui.theme.Size250
import com.ih.m2.ui.utils.EMPTY
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CreateCardScreen(
    navController: NavController,
    viewModel: CreateCardViewModel = mavericksViewModel()
) {
    val state by viewModel.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    if (state.isLoading) {
        ScreenLoading(state.message)
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
            comment = state.comment,
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
        )
    }
    if (state.message.isNotEmpty() && state.isLoading.not()) {
        val scope = rememberCoroutineScope()
        scope.launch {
            snackBarHostState.showSnackbar(
                message = state.message,
            )
        }

    }
    SnackbarHost(hostState = snackBarHostState) {
        Snackbar(
            snackbarData = it,
            containerColor = if (state.isCardSuccess) Color.Green else Color.Red,
            contentColor = Color.White,
            modifier = Modifier.padding(top = PaddingToolbar)
        )
    }


    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { state.isCardSuccess }
            .flowWithLifecycle(lifecycle)
            .collect {
                if (it) {
                    navController.popBackStack()
                    context.runWorkRequest()
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
    comment: String,
    onCommentChange: (String) -> Unit,
    isSecureCard: Boolean = false,
    selectedSecureOption: String = EMPTY,
    onSecureOptionChange: ((String) -> Unit)? = null,
    evidences: List<Evidence>,
    onAddEvidence: (Uri, EvidenceType) -> Unit,
    onDeleteEvidence: (Evidence) -> Unit,
    onSaveCard: () -> Unit,
    audioDuration: Int,
) {

    val lazyState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding),
            state = lazyState
        ) {
            stickyHeader {
                CustomAppBar(navController = navController, title = "Create card")
            }
            item {
                CustomSpacer()
                CardTypeContent(cardTypeList, onCardTypeClick, selectedCardType)
                PreclassifierContent(preclassifierList, onPreclassifierClick, selectedPreclassifier)
                PriorityContent(priorityList, onPriorityClick, selectedPriority)
                LevelContent(levelList, onLevelClick = { item, key ->
                    onLevelClick(item, key)
                    scope.launch {
                        lazyState.animateScrollToItem(lazyState.layoutInfo.totalItemsCount)
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
                        onAddEvidence = onAddEvidence
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
                        value = comment,
                        icon = Icons.Filled.Create,
                        modifier = Modifier.fillParentMaxWidth(),
                        maxLines = 5
                    ) {
                        onCommentChange(it)
                    }
                    CustomSpacer()
                    if (isSecureCard) {
                        CustomSpacer()
                        Text(
                            text = stringResource(R.string.card_type),
                            style = MaterialTheme.typography.titleLarge
                                .copy(fontWeight = FontWeight.Bold)
                        )
                        RadioGroup(
                            modifier = Modifier.fillParentMaxWidth(),
                            items = listOf(
                                stringResource(R.string.safe),
                                stringResource(R.string.unsafe)
                            ),
                            selection = selectedSecureOption
                        ) {
                            if (onSecureOptionChange != null) {
                                onSecureOptionChange(it)
                            }
                        }
                        CustomSpacer(space = SpacerSize.EXTRA_LARGE)
                    }
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
            text = stringResource(R.string.preclassifier),
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
            failure = placeholder(R.drawable.ic_launcher_background),
            loading = placeholder(R.drawable.ic_launcher_background),
            modifier = modifier.padding(PaddingTiny)
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
    M2androidappTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            CreateCardContent(
                navController = rememberNavController(),
                onCardTypeClick = {},
                cardTypeList = emptyList(),
                selectedCardType = "",
                preclassifierList = emptyList(),
                onPreclassifierClick = {},
                selectedPreclassifier = "",
                priorityList = emptyList(),
                selectedPriority = "",
                onPriorityClick = {},
                levelList = emptyMap(),
                onLevelClick = { _, _ -> },
                selectedLevelList = emptyMap(),
                lastLevelCompleted = true,
                comment = EMPTY,
                onCommentChange = {},
                evidences = emptyList(),
                onAddEvidence = { _, _ -> },
                onDeleteEvidence = {},
                onSaveCard = {},
                audioDuration = 60,
            )
        }
    }
}