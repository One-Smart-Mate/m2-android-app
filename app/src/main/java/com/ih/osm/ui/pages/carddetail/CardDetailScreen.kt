package com.ih.osm.ui.pages.carddetail

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ih.osm.R
import com.ih.osm.core.ui.LCE
import com.ih.osm.domain.model.Card
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
import com.ih.osm.ui.components.card.CardAudioSection
import com.ih.osm.ui.components.card.CardImageSection
import com.ih.osm.ui.components.card.CardVideoSection
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.extensions.isExpired
import com.ih.osm.ui.extensions.orDefault
import com.ih.osm.ui.pages.error.ErrorScreen
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.Size20

@Composable
fun CardDetailScreen(
    navController: NavController,
    viewModel: CardDetailViewModel = hiltViewModel()
) {
    val screenState = viewModel.state.collectAsStateWithLifecycle()

    when (val result = screenState.value.state) {
        is LCE.Fail -> {
            ErrorScreen(
                navController = navController,
                errorMessage = result.error
            ) {
                navController.popBackStack()
            }
        }

        is LCE.Loading -> {
            LoadingScreen(text = stringResource(R.string.loading_card_details))
        }

        is LCE.Success -> {
            CardDetailContent(
                navController = navController,
                card = result.value
            )
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

    ExpandableCard(title = stringResource(R.string.evidences)) {
        CardImageSection(
            title = stringResource(R.string.images),
            evidences = card.evidences?.toImagesAtCreation().orEmpty()
        )

        CardVideoSection(
            title = stringResource(R.string.videos),
            evidences = card.evidences?.toVideosAtCreation().orEmpty()
        )

        CardAudioSection(
            title = stringResource(R.string.audios),
            evidences = card.evidences?.toAudiosAtCreation().orEmpty()
        )
    }

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

        CardImageSection(
            title = stringResource(R.string.images_provisional_solution),
            evidences = card.evidences?.toImagesAtProvisionalSolution().orEmpty()
        )

        CardVideoSection(
            title = stringResource(R.string.videos_provisional_solution),
            evidences = card.evidences?.toVideosAtProvisionalSolution().orEmpty()
        )

        CardAudioSection(
            title = stringResource(R.string.audios_provisional_solution),
            evidences = card.evidences?.toAudiosAtProvisionalSolution().orEmpty()
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

        CardImageSection(
            title = stringResource(R.string.images_definitive_solution),
            evidences = card.evidences?.toImagesAtDefinitiveSolution().orEmpty()
        )

        CardVideoSection(
            title = stringResource(R.string.videos_definitive_solution),
            evidences = card.evidences?.toVideosAtDefinitiveSolution().orEmpty()
        )

        CardAudioSection(
            title = stringResource(R.string.audios_definitive_solution),
            evidences = card.evidences?.toAudiosAtDefinitiveSolution().orEmpty()
        )
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
