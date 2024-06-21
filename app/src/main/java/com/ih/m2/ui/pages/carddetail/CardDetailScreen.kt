package com.ih.m2.ui.pages.carddetail

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ih.m2.R
import com.ih.m2.core.ui.LCE
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.Evidence
import com.ih.m2.domain.model.getStatus
import com.ih.m2.domain.model.toAudios
import com.ih.m2.domain.model.toImages
import com.ih.m2.domain.model.toVideos
import com.ih.m2.ui.components.CustomAppBar
import com.ih.m2.ui.components.ExpandableCard
import com.ih.m2.ui.components.ScreenLoading
import com.ih.m2.ui.components.SectionTag
import com.ih.m2.ui.components.VideoPlayer
import com.ih.m2.ui.extensions.defaultScreen
import com.ih.m2.ui.extensions.orDefault
import com.ih.m2.ui.extensions.toFormatDate
import com.ih.m2.ui.pages.createcard.PhotoCardItem
import com.ih.m2.ui.pages.error.ErrorScreen
import com.ih.m2.ui.theme.M2androidappTheme
import com.ih.m2.ui.theme.PaddingTiny
import com.ih.m2.ui.theme.Size120
import com.ih.m2.ui.theme.Size200
import com.ih.m2.ui.theme.Size250


@Composable
fun CardDetailScreen(
    navController: NavController,
    cardId: String,
    viewModel: CardDetailViewModel = mavericksViewModel()
) {
    val state by viewModel.collectAsState()

    when (val screenState = state.card) {
        is LCE.Fail -> {
            ErrorScreen(
                navController = navController,
                errorMessage = screenState.error
            )
        }

        is LCE.Loading, LCE.Uninitialized -> {
            ScreenLoading(text = stringResource(R.string.loading_card_details))
        }

        is LCE.Success -> {
            CardDetailContent(
                navController = navController,
                card = screenState.value
            )
        }
    }
    LaunchedEffect(viewModel) {
        viewModel.process(CardDetailViewModel.Action.GetCardDetail(cardId))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardDetailContent(
    navController: NavController,
    card: Card
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding)
        ) {
            stickyHeader {
                CustomAppBar(
                    navController = navController,
                    title = "${stringResource(R.string.card)} ${card.id}"
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
fun CardInformationContent(
    card: Card
) {
    ExpandableCard(title = stringResource(R.string.information)) {
        SectionTag(
            title = stringResource(R.string.created_date),
            value = card.cardCreationDate.toFormatDate(),
        )
        SectionTag(
            title = stringResource(R.string.due_date),
            value = card.cardDueDate,
        )
        SectionTag(
            title = stringResource(R.string.status),
            value = card.getStatus(),
        )
        SectionTag(
            title = stringResource(R.string.card_types),
            value = card.cardTypeName,
        )
        SectionTag(
            title = stringResource(R.string.preclassifier),
            value = "${card.preclassifierCode} - ${card.preclassifierDescription}",
        )
        SectionTag(
            title = stringResource(R.string.priority),
            value = "${card.priorityCode} - ${card.priorityDescription}",
        )
        SectionTag(
            title = stringResource(R.string.mechanic),
            value = card.mechanicName.orDefault(),
        )
        SectionTag(
            title = stringResource(R.string.creator),
            value = card.creatorName.orDefault(),
        )
        SectionTag(
            title = stringResource(R.string.comments),
            value = card.commentsAtCardCreation.orDefault(),
        )
    }

    CardInformationEvidence(card = card)

    ExpandableCard(title = stringResource(R.string.provisional_solution)) {
        SectionTag(
            title = stringResource(R.string.provisional_user),
            value = card.userProvisionalSolutionName.orDefault(),
        )
        SectionTag(
            title = stringResource(R.string.provisional_date),
            value = card.cardProvisionalSolutionDate?.toFormatDate().orDefault(),
        )
        SectionTag(
            title = stringResource(R.string.provisional_comments),
            value = card.commentsAtCardProvisionalSolution.orDefault(),
        )
    }

    ExpandableCard(title = stringResource(R.string.definitive_solution)) {
        SectionTag(
            title = stringResource(R.string.definitive_date),
            value = card.cardDefinitiveSolutionDate?.toFormatDate().orDefault(),
        )
        SectionTag(
            title = stringResource(R.string.definitive_user),
            value = card.userDefinitiveSolutionName.orDefault(),
        )
        SectionTag(
            title = stringResource(R.string.definitive_comments),
            value = card.commentsAtCardDefinitiveSolution.orDefault(),
        )
    }
}

@Composable
fun CardInformationEvidence(
    card: Card
) {
    card.evidences?.let { evidences ->
        ExpandableCard(title = "Evidences") {
            val imagesList = evidences.toImages()
            if (imagesList.isNotEmpty()) {
                EvidenceImagesCardSection(
                    title = stringResource(R.string.images),
                    evidences = imagesList
                )
            }
            val videoList = evidences.toVideos()
            if (videoList.isNotEmpty()) {
                EvidenceVideoCardSection(
                    title = stringResource(R.string.videos),
                    evidences = videoList
                )
            }
            val audioList = evidences.toAudios()
            if (audioList.isNotEmpty()) {
                EvidenceAudioCardSection(
                    title = stringResource(R.string.audios),
                    evidences = audioList
                )
            }
        }
    }
}

@Composable
fun EvidenceImagesCardSection(
    title: String,
    evidences: List<Evidence>
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
                .copy(fontWeight = FontWeight.Bold)
        )
        LazyRow {
            items(evidences) {
                PhotoCardItem(model = it.url, showIcon = false)
            }
        }
    }
}

@Composable
fun EvidenceVideoCardSection(
    title: String,
    evidences: List<Evidence>
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
                .copy(fontWeight = FontWeight.Bold)
        )
        LazyRow {
            items(evidences) {
                VideoPlayer(
                    modifier = Modifier
                        .width(Size200)
                        .height(Size250)
                        .padding(PaddingTiny),
                    url = it.url
                )
            }
        }
    }
}

@Composable
fun EvidenceAudioCardSection(
    title: String,
    evidences: List<Evidence>
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
                .copy(fontWeight = FontWeight.Bold)
        )
        LazyRow {
            items(evidences) {
                VideoPlayer(
                    modifier = Modifier
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
    M2androidappTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            CardDetailContent(
                navController = rememberNavController(),
                card = Card.mock()
            )
        }
    }
}