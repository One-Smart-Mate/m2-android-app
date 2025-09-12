package com.ih.osm.ui.components.card

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.ih.osm.R
import com.ih.osm.core.ui.functions.getColorFromHex
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.cardSiteTitle
import com.ih.osm.domain.model.cardTitle
import com.ih.osm.domain.model.enableAssignMechanic
import com.ih.osm.domain.model.enableDefinitiveSolution
import com.ih.osm.domain.model.enableProvisionalSolution
import com.ih.osm.domain.model.getCreationDate
import com.ih.osm.domain.model.getStatus
import com.ih.osm.domain.model.isClosed
import com.ih.osm.domain.model.isLocalCard
import com.ih.osm.domain.model.preclassifierValue
import com.ih.osm.domain.model.priorityValue
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.CustomTag
import com.ih.osm.ui.components.SectionTag
import com.ih.osm.ui.components.SpacerDirection
import com.ih.osm.ui.components.TagSize
import com.ih.osm.ui.components.TagType
import com.ih.osm.ui.components.buttons.ButtonType
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.components.card.actions.CardItemSheetAction
import com.ih.osm.ui.components.sheets.SolutionBottomSheet
import com.ih.osm.ui.extensions.getCurrentDateTimeUtc
import com.ih.osm.ui.extensions.isCardExpired
import com.ih.osm.ui.extensions.isExpired
import com.ih.osm.ui.extensions.orDefault
import com.ih.osm.ui.pages.createcard.CardItemIcon
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.PaddingSmall
import com.ih.osm.ui.theme.Size20

// @Composable
// fun CardItemList(
//    card: Card,
//    isActionsEnabled: Boolean = true,
//    onClick: () -> Unit,
//    onSolutionClick: (String) -> Unit
// ) {
//    var showSolutionBottomSheet by remember {
//        mutableStateOf(false)
//    }
//    Card(
//        modifier =
//        Modifier
//            .fillMaxWidth()
//            .padding(PaddingNormal),
//        onClick = {
//            onClick()
//        }
//    ) {
//        Column(
//            modifier = Modifier.padding(PaddingSmall)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = card.cardTitle(),
//                    style =
//                    MaterialTheme.typography.titleLarge
//                        .copy(fontWeight = FontWeight.Bold),
//                    textAlign = TextAlign.Center
//                )
//
//                Box(
//                    modifier =
//                    Modifier
//                        .padding(start = 5.dp)
//                        .size(20.dp)
//                        .background(
//                            color = card.getBorderColor(),
//                            shape = CircleShape
//                        )
//                )
//            }
//
//            CardItemEvidence(
//                showCamera = card.evidenceImageCreation > 0,
//                showVideo = card.evidenceVideoCreation > 0,
//                showVoice = card.evidenceAudioCreation > 0
//            )
//
//            AnimatedVisibility(visible = card.stored == STORED_LOCAL) {
//                CustomTag(
//                    title = stringResource(R.string.local_card),
//                    tagSize = TagSize.SMALL,
//                    tagType = TagType.OUTLINE
//                )
//            }
//
//            SectionTag(
//                title = stringResource(id = R.string.status),
//                value = card.getStatus()
//            )
//            SectionTag(
//                title = stringResource(id = R.string.type_card),
//                value = card.cardTypeName.orEmpty()
//            )
//            SectionTag(
//                title = stringResource(id = R.string.type_of_problem),
//                value = card.preclassifierValue()
//            )
//            SectionTag(
//                title = stringResource(R.string.priority),
//                value = card.priorityValue()
//            )
//            SectionTag(
//                title = stringResource(id = R.string.area),
//                value = card.areaName
//            )
//            SectionTag(
//                title = stringResource(id = R.string.created_by),
//                value = card.creatorName
//            )
//            SectionTag(
//                title = stringResource(id = R.string.date),
//                value = card.getCreationDate()
//            )
//            SectionTag(
//                title = stringResource(id = R.string.due_date),
//                value = card.dueDate
//            )
//
//            CustomSpacer()
//            AnimatedVisibility(visible = card.isClosed().not() && isActionsEnabled) {
//                CustomButton(
//                    text = stringResource(R.string.actions),
//                    buttonType = ButtonType.OUTLINE
//                ) {
//                    showSolutionBottomSheet = true
//                }
//            }
//        }
//    }
//    if (showSolutionBottomSheet) {
//        SolutionBottomSheet(
//            onSolutionClick = {
//                showSolutionBottomSheet = false
//                onSolutionClick(it)
//            },
//            onDismissRequest = {
//                showSolutionBottomSheet = false
//            },
//            showProvisionalSolution = card.enableProvisionalSolution(),
//            showDefinitiveSolution = card.enableDefinitiveSolution(),
//            showAssignCard = card.enableAssignMechanic()
//        )
//    }
// }

@Composable
fun CardItemEvidence(
    showCamera: Boolean,
    showVideo: Boolean,
    showVoice: Boolean,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
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

// @Composable
// fun CardSectionItemList(card: Card) {
//    Card(
//        modifier = Modifier.padding(PaddingTinySmall),
//        border = BorderStroke(Size1, getInvertedColor())
//    ) {
//        Column(
//            modifier = Modifier.padding(PaddingSmall)
//        ) {
//            Text(
//                text = "${card.cardTypeName} ${card.siteCardId}",
//                style =
//                MaterialTheme.typography.titleLarge
//                    .copy(fontWeight = FontWeight.Bold),
//                textAlign = TextAlign.Center,
//                modifier = Modifier.fillMaxWidth()
//            )
//            SectionTag(
//                title = stringResource(id = R.string.area),
//                value = card.areaName
//            )
//        }
//    }
// }

@Composable
fun CardItemListV2(
    card: Card,
    isActionsEnabled: Boolean = true,
    onClick: () -> Unit,
    onAction: (CardItemSheetAction) -> Unit,
) {
    var showSolutionBottomSheet by remember {
        mutableStateOf(false)
    }
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(PaddingNormal),
        onClick = {
            onClick()
        },
    ) {
        Column(
            modifier = Modifier.padding(PaddingSmall),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = card.cardSiteTitle(),
                    style =
                        MaterialTheme.typography.titleLarge
                            .copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = card.cardTitle(),
                    style =
                        MaterialTheme.typography.titleLarge
                            .copy(fontWeight = FontWeight.Bold),
                )
                Box(
                    modifier =
                        Modifier
                            .size(Size20)
                            .background(
                                color = getColorFromHex(card.cardTypeColor),
                                shape = CircleShape,
                            ),
                )
            }

            CardItemEvidence(
                showCamera = card.evidenceImageCreation > 0,
                showVideo = card.evidenceVideoCreation > 0,
                showVoice = card.evidenceAudioCreation > 0,
            )

            Row {
                AnimatedVisibility(visible = card.isLocalCard()) {
                    CustomTag(
                        title = stringResource(R.string.local_card),
                        tagSize = TagSize.SMALL,
                        tagType = TagType.OUTLINE,
                    )
                }
                CustomSpacer(direction = SpacerDirection.HORIZONTAL)
                AnimatedVisibility(visible = card.hasLocalSolutions) {
                    CustomTag(
                        title = stringResource(R.string.local_solutions),
                        tagSize = TagSize.SMALL,
                        tagType = TagType.OUTLINE,
                    )
                }
            }

            SectionTag(
                title = stringResource(id = R.string.status),
                value = card.getStatus(),
            )
            SectionTag(
                title = stringResource(id = R.string.date),
                value = card.getCreationDate(),
            )
            val referenceDateString =
                if (card.cardDefinitiveSolutionDate.isNullOrEmpty()) {
                    getCurrentDateTimeUtc()
                } else {
                    card.cardDefinitiveSolutionDate
                }

            val isExpired = card.dueDate.isCardExpired(referenceDateString, card.status)

            SectionTag(
                title = stringResource(id = R.string.due_date),
                value =
                    if (isExpired) {
                        "${card.dueDate} ${stringResource(id = R.string.expired)}"
                    } else {
                        card.dueDate
                    },
                isErrorEnabled = isExpired,
            )
            SectionTag(
                title = stringResource(R.string.preclassifier),
                value = card.preclassifierValue(),
            )
            SectionTag(
                title = stringResource(R.string.priority),
                value = card.priorityValue(),
            )
            SectionTag(
                title = stringResource(R.string.card_location),
                value = card.cardLocation,
            )
            SectionTag(
                title = stringResource(id = R.string.created_by),
                value = card.creatorName.orDefault(),
            )
            SectionTag(
                title = stringResource(id = R.string.mechanic),
                value = card.mechanicName.orDefault(),
            )
            SectionTag(
                title = stringResource(id = R.string.comments),
                value = card.commentsAtCardCreation.orDefault(),
            )
            CustomSpacer()
            AnimatedVisibility(visible = card.isClosed().not() && isActionsEnabled) {
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
            onAction = {
                showSolutionBottomSheet = false
                onAction(it)
            },
            onDismissRequest = {
                showSolutionBottomSheet = false
            },
            showProvisionalSolution = card.enableProvisionalSolution(),
            showDefinitiveSolution = card.enableDefinitiveSolution(),
            showAssignCard = card.enableAssignMechanic(),
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun HomeCardItemListPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            Column {
                CardItemListV2(Card.mock(), true, {}) {}
                CustomSpacer()
                CardItemListV2(
                    Card.mock().copy(
                        dueDate = "2024-12-12",
                    ),
                    true,
                    {},
                ) {}
                CustomSpacer()
            }
        }
    }
}
