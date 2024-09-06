package com.osm.ui.components.card

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.ih.osm.R
import com.osm.domain.model.Card
import com.osm.domain.model.cardTitle
import com.osm.domain.model.enableDefinitiveSolution
import com.osm.domain.model.enableProvisionalSolution
import com.osm.domain.model.getCreationDate
import com.osm.domain.model.getStatus
import com.osm.domain.model.isClosed
import com.osm.domain.model.preclassifierValue
import com.osm.domain.model.priorityValue
import com.osm.ui.components.CustomSpacer
import com.osm.ui.components.CustomTag
import com.osm.ui.components.SectionTag
import com.osm.ui.components.TagSize
import com.osm.ui.components.TagType
import com.osm.ui.components.buttons.ButtonType
import com.osm.ui.components.buttons.CustomButton
import com.osm.ui.components.sheets.SolutionBottomSheet
import com.osm.ui.extensions.getInvertedColor
import com.osm.ui.pages.createcard.CardItemIcon
import com.osm.ui.theme.OsmAppTheme
import com.osm.ui.theme.PaddingNormal
import com.osm.ui.theme.PaddingSmall
import com.osm.ui.theme.PaddingTinySmall
import com.osm.ui.theme.Size1
import com.osm.ui.utils.STORED_LOCAL

@Composable
fun CardItemList(
    card: Card,
    onClick: () -> Unit,
    onSolutionClick: (String) -> Unit
) {
    var showSolutionBottomSheet by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingNormal),
        onClick = {
            onClick()
        }
    ) {
        Column(
            modifier = Modifier.padding(PaddingSmall)
        ) {
            Text(
                text = card.cardTitle(),
                style = MaterialTheme.typography.titleLarge
                    .copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            CardItemEvidence(
                showCamera = card.evidenceImageCreation > 0,
                showVideo = card.evidenceVideoCreation > 0,
                showVoice = card.evidenceAudioCreation > 0
            )

            AnimatedVisibility(visible = card.stored == STORED_LOCAL) {
                CustomTag(
                    title = stringResource(R.string.local_card),
                    tagSize = TagSize.SMALL,
                    tagType = TagType.OUTLINE,
                )
            }

            SectionTag(
                title = stringResource(id = R.string.status),
                value = card.getStatus(),
            )
            SectionTag(
                title = stringResource(id = R.string.type_card),
                value = card.cardTypeName.orEmpty(),
            )
            SectionTag(
                title = stringResource(id = R.string.type_of_problem),
                value = card.preclassifierValue(),
            )
            SectionTag(
                title = stringResource(R.string.priority),
                value = card.priorityValue(),
            )
            SectionTag(
                title = stringResource(id = R.string.area),
                value = card.areaName,
            )
            SectionTag(
                title = stringResource(id = R.string.created_by),
                value = card.creatorName,
            )
            SectionTag(
                title = stringResource(id = R.string.date),
                value = card.getCreationDate(),
            )
            SectionTag(
                title = stringResource(id = R.string.due_date),
                value = card.dueDate,
            )

            CustomSpacer()
            AnimatedVisibility(visible = card.isClosed().not()) {
                CustomButton(
                    text = stringResource(R.string.actions),
                    buttonType = ButtonType.OUTLINE,
                ) {
                    showSolutionBottomSheet = true
                }
            }
        }
    }
    if (showSolutionBottomSheet) {
        SolutionBottomSheet(
            onSolutionClick = {
                showSolutionBottomSheet = false
                onSolutionClick(it)
                              },
            onDismissRequest = {
                showSolutionBottomSheet = false
            },
            showProvisionalSolution = card.enableProvisionalSolution(),
            showDefinitiveSolution = card.enableDefinitiveSolution()
        )
    }
}

@Composable
fun CardItemEvidence(
    showCamera: Boolean,
    showVideo: Boolean,
    showVoice: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        if (showCamera) {
            CardItemIcon(
                icon = painterResource(id = R.drawable.ic_photo_camera),
            ) {}
        }
        if (showVoice) {
            CardItemIcon(
                icon = painterResource(id = R.drawable.ic_voice),
            ) {}
        }
        if (showVideo) {
            CardItemIcon(
                icon = painterResource(id = R.drawable.ic_videocam),
            ) {}
        }
    }
}

@Composable
fun CardSectionItemList(
    card: Card
) {
    Card(
        modifier = Modifier.padding(PaddingTinySmall),
        border = BorderStroke(Size1, getInvertedColor())
    ) {
        Column(
            modifier = Modifier.padding(PaddingSmall)
        ) {
            Text(
                text = "${card.cardTypeName} ${card.siteCardId}",
                style = MaterialTheme.typography.titleLarge
                    .copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            SectionTag(
                title = stringResource(id = R.string.area),
                value = card.areaName,
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun HomeCardItemListPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            Column {
                CardItemList(Card.mock(), {}) {}
                CustomSpacer()

            }
        }
    }
}