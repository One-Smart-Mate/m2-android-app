package com.ih.osm.ui.pages.carddetail

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ih.osm.R
import com.ih.osm.core.ui.LCE
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.cardSiteTitle
import com.ih.osm.domain.model.cardTitle
import com.ih.osm.domain.model.getBorderColor
import com.ih.osm.domain.model.getCreationDate
import com.ih.osm.domain.model.getStatus
import com.ih.osm.domain.model.preclassifierValue
import com.ih.osm.domain.model.priorityValue
import com.ih.osm.domain.model.toAudiosAtCreation
import com.ih.osm.domain.model.toAudiosAtDefinitiveSolution
import com.ih.osm.domain.model.toAudiosAtProvisionalSolution
import com.ih.osm.domain.model.toImagesAtCreation
import com.ih.osm.domain.model.toImagesAtDefinitiveSolution
import com.ih.osm.domain.model.toImagesAtProvisionalSolution
import com.ih.osm.domain.model.toVideosAtCreation
import com.ih.osm.domain.model.toVideosAtDefinitiveSolution
import com.ih.osm.domain.model.toVideosAtProvisionalSolution
import com.ih.osm.domain.model.validateCloseDate
import com.ih.osm.domain.model.validateProvisionalDate
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.ExpandableCard
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.SectionTag
import com.ih.osm.ui.components.VideoPlayer
import com.ih.osm.ui.components.evidence.PreviewVideo
import com.ih.osm.ui.components.images.PreviewImage
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.extensions.isExpired
import com.ih.osm.ui.extensions.orDefault
import com.ih.osm.ui.pages.createcard.PhotoCardItem
import com.ih.osm.ui.pages.error.ErrorScreen
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingTiny
import com.ih.osm.ui.theme.PaddingToolbar
import com.ih.osm.ui.theme.Size120
import com.ih.osm.ui.theme.Size20
import com.ih.osm.ui.theme.Size200
import com.ih.osm.ui.theme.Size250
import com.ih.osm.ui.utils.EMPTY
import kotlinx.coroutines.launch

@Composable
fun CardDetailScreen(
    navController: NavController,
    uuid: String,
    viewModel: CardDetailViewModel = mavericksViewModel()
) {
    val state by viewModel.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    when (val screenState = state.card) {
        is LCE.Fail -> {
            ErrorScreen(
                navController = navController,
                errorMessage = screenState.error
            ) {
                viewModel.process(CardDetailViewModel.Action.GetCardDetail(uuid))
            }
        }

        is LCE.Loading, LCE.Uninitialized -> {
            LoadingScreen(text = stringResource(R.string.loading_card_details))
        }

        is LCE.Success -> {
            CardDetailContent(
                navController = navController,
                card = screenState.value
            )
        }
    }

    SnackbarHost(hostState = snackBarHostState) {
        Snackbar(
            snackbarData = it,
            containerColor = Color.Green,
            contentColor = Color.White,
            modifier = Modifier.padding(top = PaddingToolbar)
        )
    }

    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { state }
            .flowWithLifecycle(lifecycle)
            .collect {
                if (it.card !is LCE.Success) {
                    viewModel.process(CardDetailViewModel.Action.GetCardDetail(uuid))
                }

                if (state.message.isNotEmpty()) {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = state.message
                        )
                        viewModel.process(CardDetailViewModel.Action.ClearMessage)
                    }
                }
            }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardDetailContent(navController: NavController, card: Card) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding)
        ) {
            stickyHeader {
                CustomAppBar(
                    navController = navController,
                    content = { CardDetailHeader(card) }
                )
            }

            item {
                CardInformationContent(
                    card = card
                )
            }
        }
    }
}

@Composable
fun CardDetailHeader(card: Card) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = card.cardSiteTitle(),
            style =
            MaterialTheme.typography.titleLarge
                .copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = card.cardTitle(),
            style =
            MaterialTheme.typography.titleLarge
                .copy(fontWeight = FontWeight.Bold)
        )
        Box(
            modifier =
            Modifier
                .size(Size20)
                .background(
                    color = card.getBorderColor(),
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun CardInformationContent(card: Card) {
    ExpandableCard(title = stringResource(R.string.information), expanded = true) {
        SectionTag(
            title = stringResource(R.string.status),
            value = card.getStatus()
        )

        SectionTag(
            title = stringResource(R.string.created_date),
            value = card.getCreationDate()
        )

        SectionTag(
            title = stringResource(id = R.string.due_date),
            value =
            if (card.dueDate.isExpired()) {
                stringResource(id = R.string.expired)
            } else {
                card.dueDate
            },
            isErrorEnabled = card.dueDate.isExpired()
        )
        SectionTag(
            title = stringResource(R.string.preclassifier),
            value = card.preclassifierValue()
        )
        SectionTag(
            title = stringResource(R.string.priority),
            value = card.priorityValue()
        )
        SectionTag(
            title = stringResource(R.string.card_location),
            value = card.cardLocation
        )
        SectionTag(
            title = stringResource(id = R.string.created_by),
            value = card.creatorName.orDefault()
        )
        SectionTag(
            title = stringResource(id = R.string.responsible),
            value = card.responsableName.orDefault()
        )
        SectionTag(
            title = stringResource(id = R.string.mechanic),
            value = card.mechanicName.orDefault()
        )
    }

    ExpandableCard(title = stringResource(id = R.string.comments)) {
        SectionTag(
            title = stringResource(id = R.string.comments),
            value = card.commentsAtCardCreation.orDefault()
        )
    }

    CardInformationEvidence(card = card)

    ExpandableCard(title = stringResource(R.string.provisional_solution)) {
        SectionTag(
            title = stringResource(R.string.date),
            value = card.validateProvisionalDate().orDefault()
        )
        SectionTag(
            title = stringResource(R.string.user),
            value = card.userProvisionalSolutionName.orDefault()
        )
        SectionTag(
            title = stringResource(R.string.comments),
            value = card.commentsAtCardProvisionalSolution.orDefault()
        )
    }

    ExpandableCard(title = stringResource(R.string.definitive_solution)) {
        SectionTag(
            title = stringResource(R.string.date),
            value = card.validateCloseDate().orDefault()
        )
        SectionTag(
            title = stringResource(R.string.user),
            value = card.userDefinitiveSolutionName.orDefault()
        )
        SectionTag(
            title = stringResource(R.string.comments),
            value = card.commentsAtCardDefinitiveSolution.orDefault()
        )
    }
}

@Composable
fun CardInformationEvidence(card: Card) {
    if (card.evidences.isNullOrEmpty().not()) {
        val evidences = card.evidences.orEmpty()
        ExpandableCard(title = stringResource(R.string.evidences)) {
            val imagesAtCreation = evidences.toImagesAtCreation()
            if (imagesAtCreation.isNotEmpty()) {
                EvidenceImagesCardSection(
                    title = stringResource(R.string.images),
                    evidences = imagesAtCreation
                )
            }
            val imagesAtProvisionalSolution = evidences.toImagesAtProvisionalSolution()
            if (imagesAtProvisionalSolution.isNotEmpty()) {
                EvidenceImagesCardSection(
                    title = stringResource(R.string.images_provisional_solution),
                    evidences = imagesAtProvisionalSolution
                )
            }
            val imagesAtDefinitiveSolution = evidences.toImagesAtDefinitiveSolution()
            if (imagesAtDefinitiveSolution.isNotEmpty()) {
                EvidenceImagesCardSection(
                    title = stringResource(R.string.images_definitive_solution),
                    evidences = imagesAtDefinitiveSolution
                )
            }
            val videosAtCreation = evidences.toVideosAtCreation()
            if (videosAtCreation.isNotEmpty()) {
                EvidenceVideoCardSection(
                    title = stringResource(R.string.videos),
                    evidences = videosAtCreation
                )
            }
            val videosAtProvisionalSolution = evidences.toVideosAtProvisionalSolution()
            if (videosAtProvisionalSolution.isNotEmpty()) {
                EvidenceVideoCardSection(
                    title = stringResource(R.string.videos_provisional_solution),
                    evidences = videosAtProvisionalSolution
                )
            }
            val videosAtDefinitiveSolution = evidences.toVideosAtDefinitiveSolution()
            if (videosAtDefinitiveSolution.isNotEmpty()) {
                EvidenceVideoCardSection(
                    title = stringResource(R.string.videos_definitive_solution),
                    evidences = videosAtDefinitiveSolution
                )
            }
            val audiosAtCreation = evidences.toAudiosAtCreation()
            if (audiosAtCreation.isNotEmpty()) {
                EvidenceAudioCardSection(
                    title = stringResource(R.string.audios),
                    evidences = audiosAtCreation
                )
            }
            val audiosAtProvisionalSolution = evidences.toAudiosAtProvisionalSolution()
            if (audiosAtProvisionalSolution.isNotEmpty()) {
                EvidenceAudioCardSection(
                    title = stringResource(R.string.audios_provisional_solution),
                    evidences = audiosAtProvisionalSolution
                )
            }
            val audiosAtDefinitiveSolution = evidences.toAudiosAtDefinitiveSolution()
            if (audiosAtDefinitiveSolution.isNotEmpty()) {
                EvidenceAudioCardSection(
                    title = stringResource(R.string.audios_definitive_solution),
                    evidences = audiosAtDefinitiveSolution
                )
            }
        }
    }
}

@Composable
fun EvidenceImagesCardSection(title: String, evidences: List<Evidence>) {
    var imageUrl by remember {
        mutableStateOf(EMPTY)
    }
    var openImage by remember {
        mutableStateOf(false)
    }
    Column {
        Text(
            text = title,
            style =
            MaterialTheme.typography.titleLarge
                .copy(fontWeight = FontWeight.Bold)
        )
        LazyRow {
            items(evidences) {
                PhotoCardItem(
                    model = it.url,
                    showIcon = false,
                    modifier =
                    Modifier
                        .width(Size200)
                        .height(Size250)
                        .clickable {
                            imageUrl = it.url
                            openImage = true
                        }
                )
            }
        }
        PreviewImage(openImage = openImage, model = imageUrl) {
            openImage = false
            imageUrl = EMPTY
        }
    }
}

@Composable
fun EvidenceVideoCardSection(title: String, evidences: List<Evidence>) {
    var videoUrl by remember {
        mutableStateOf(EMPTY)
    }
    var openVideo by remember {
        mutableStateOf(false)
    }
    Column {
        Text(
            text = title,
            style =
            MaterialTheme.typography.titleLarge
                .copy(fontWeight = FontWeight.Bold)
        )
        LazyRow {
            items(evidences) {
                PhotoCardItem(
                    model = it.url,
                    showIcon = false,
                    modifier =
                    Modifier
                        .width(Size200)
                        .height(Size250)
                        .clickable {
                            videoUrl = it.url
                            openVideo = true
                        }
                )
            }
        }
        PreviewVideo(openVideo = openVideo, url = videoUrl) {
            videoUrl = EMPTY
            openVideo = false
        }
    }
}

@Composable
fun EvidenceAudioCardSection(title: String, evidences: List<Evidence>) {
    Column {
        Text(
            text = title,
            style =
            MaterialTheme.typography.titleLarge
                .copy(fontWeight = FontWeight.Bold)
        )
        LazyRow {
            items(evidences) {
                VideoPlayer(
                    modifier =
                    Modifier
                        .size(Size120)
                        .padding(PaddingTiny),
                    url = it.url
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
private fun CardDetailScreenPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            CardDetailContent(
                navController = rememberNavController(),
                card = Card.mock()
            )
        }
    }
}
