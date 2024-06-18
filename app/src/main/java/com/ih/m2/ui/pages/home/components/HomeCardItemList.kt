package com.ih.m2.ui.pages.home.components

import android.annotation.SuppressLint
import android.content.res.Configuration
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.ih.m2.R
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.getStatus

import com.ih.m2.ui.components.CustomSpacer
import com.ih.m2.ui.components.SectionTag
import com.ih.m2.ui.components.buttons.ButtonType
import com.ih.m2.ui.components.buttons.CustomButton
import com.ih.m2.ui.pages.createcard.CardItemIcon
import com.ih.m2.ui.theme.M2androidappTheme
import com.ih.m2.ui.theme.PaddingNormal

@Composable
fun HomeCardItemList(
    card: Card,
    onClick: (card: Card) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingNormal),
        onClick = {
            onClick(card)
        }
    ) {
        Column(
            modifier = Modifier.padding(PaddingNormal)
        ) {
            Text(
                text = "${card.cardTypeName} ${card.siteCardID}",
                style = MaterialTheme.typography.titleLarge
                    .copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

           HomeCardItemEvidence(
               showCamera = card.evidenceImageCreation > 0,
               showVideo = card.evidenceVideoCreation > 0,
               showVoice = card.evidenceAudioCreation > 0
           )

            SectionTag(
                title = stringResource(id = R.string.status),
                value = card.getStatus(),
            )
            SectionTag(
                title = stringResource(id = R.string.type_card),
                value = card.cardTypeName,
            )
            SectionTag(
                title = stringResource(id = R.string.preclassifier),
                value = "${card.preclassifierCode} ${card.preclassifierDescription}",
            )
            SectionTag(
                title = stringResource(id = R.string.area),
                value = card.areaName,
            )
            SectionTag(
                title = stringResource(id = R.string.level),
                value = card.level.toString(),
            )
            SectionTag(
                title = stringResource(id = R.string.created_by),
                value = card.creatorName,
            )
            SectionTag(
                title = stringResource(id = R.string.date),
                value = card.cardCreationDate,
            )
            SectionTag(
                title = stringResource(id = R.string.due_date),
                value = card.cardDueDate,
            )

            CustomSpacer()
            CustomButton(
                text = stringResource(R.string.actions),
                buttonType = ButtonType.OUTLINE,
            ) {

            }
        }
    }
}

@Composable
fun HomeCardItemEvidence(
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun HomeCardItemListPreview() {
    M2androidappTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            HomeCardItemList(Card.mock()) {}
        }
    }
}