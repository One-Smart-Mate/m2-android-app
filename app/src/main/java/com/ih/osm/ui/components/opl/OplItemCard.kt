package com.ih.osm.ui.components.opl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ih.osm.R
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.model.OplDetail
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.CustomTag
import com.ih.osm.ui.components.SpacerDirection
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.TagSize
import com.ih.osm.ui.components.TagType
import com.ih.osm.ui.components.card.CardAudioSection
import com.ih.osm.ui.components.card.CardImageSection
import com.ih.osm.ui.components.card.CardPdfSection
import com.ih.osm.ui.components.card.CardVideoSection
import com.ih.osm.ui.extensions.getTextColor
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.PaddingSmall
import com.ih.osm.ui.theme.PaddingTiny
import com.ih.osm.ui.theme.Radius8
import com.ih.osm.ui.utils.AUDIO_CREATION
import com.ih.osm.ui.utils.IMG_CREATION
import com.ih.osm.ui.utils.STATUS_A
import com.ih.osm.ui.utils.VIDEO_CREATION

@Composable
fun OplItemCard(
    opl: Opl,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingNormal, vertical = PaddingSmall),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = {
            isExpanded = !isExpanded
            // Removed onClick() to prevent navigation
        },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(PaddingNormal)
                    .animateContentSize(
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                    ),
        ) {
            // Header with title and type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = opl.title,
                        style =
                            MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = getTextColor(),
                            ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    // Removed the OPL ID
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CustomTag(
                        title = opl.oplType.uppercase(),
                        tagSize = TagSize.SMALL,
                        tagType = TagType.OUTLINE,
                    )
                    IconButton(
                        onClick = {
                            isExpanded = !isExpanded
                        },
                        modifier = Modifier.padding(0.dp),
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription =
                                if (isExpanded) {
                                    stringResource(
                                        id = R.string.collapse,
                                    )
                                } else {
                                    stringResource(id = R.string.expand)
                                },
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }
            }

            CustomSpacer()

            // Objective
            Row(
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                CustomSpacer(direction = SpacerDirection.HORIZONTAL, space = SpacerSize.SMALL)
                Text(
                    text = opl.objetive,
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = getTextColor(),
                        ),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }

            CustomSpacer()

            // Creator and reviewer information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = getTextColor().copy(alpha = 0.6f),
                    )
                    CustomSpacer(direction = SpacerDirection.HORIZONTAL, space = SpacerSize.TINY)
                    Text(
                        text = stringResource(id = R.string.opl_created_by, opl.creatorName),
                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                color = getTextColor().copy(alpha = 0.6f),
                            ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            // Details count and date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(Radius8),
                            )
                            .padding(horizontal = PaddingSmall, vertical = PaddingTiny),
                ) {
                    Text(
                        text = "${opl.details.size} ${stringResource(id = R.string.steps)}",
                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium,
                            ),
                    )
                }

                Text(
                    text = opl.updatedAt,
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            color = getTextColor().copy(alpha = 0.6f),
                        ),
                )
            }

            // Reviewer indicator if exists
            if (opl.reviewerName.isNotEmpty()) {
                CustomSpacer(space = SpacerSize.TINY)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF4CAF50),
                        )
                        CustomSpacer(
                            direction = SpacerDirection.HORIZONTAL,
                            space = SpacerSize.TINY,
                        )
                        Text(
                            text = stringResource(id = R.string.opl_reviewed_by, opl.reviewerName),
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Medium,
                                ),
                        )
                    }
                }
            }

            // Expandable details section
            AnimatedVisibility(
                visible = isExpanded,
                enter =
                    expandVertically(
                        expandFrom = Alignment.Top,
                    ),
                exit =
                    shrinkVertically(
                        shrinkTowards = Alignment.Top,
                    ),
            ) {
                Column {
                    CustomSpacer()
                    HorizontalDivider(
                        color = getTextColor().copy(alpha = 0.2f),
                        thickness = 1.dp,
                    )
                    CustomSpacer()

                    if (opl.details.isNotEmpty()) {
                        Text(
                            text = stringResource(id = R.string.steps_of_opl),
                            style =
                                MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = getTextColor(),
                                ),
                        )
                        CustomSpacer(space = SpacerSize.SMALL)

                        opl.details.sortedBy { it.order }.forEachIndexed { index, detail ->
                            OplDetailItem(
                                detail = detail,
                                modifier =
                                    Modifier.padding(
                                        bottom = if (index < opl.details.size - 1) PaddingSmall else 0.dp,
                                    ),
                            )
                        }
                    } else {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(8.dp),
                                    )
                                    .padding(PaddingNormal),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_steps_defined),
                                style =
                                    MaterialTheme.typography.bodyMedium.copy(
                                        color = getTextColor().copy(alpha = 0.6f),
                                    ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OplDetailItem(
    detail: OplDetail,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp),
                    ),
            // .padding(PaddingSmall),
            verticalAlignment = Alignment.Top,
        ) {
            // Step number inside a circle

            CustomSpacer(direction = SpacerDirection.HORIZONTAL, space = SpacerSize.SMALL)

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = detail.text,
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                color = getTextColor(),
                            ),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        // Show media if exists based on type
        if (detail.mediaUrl.isNotEmpty() && detail.type != "texto") {
            CustomSpacer(space = SpacerSize.TINY)

            when (detail.type) {
                "imagen" -> {
                    // Show image using CardImageSection
                    val imageEvidences = createEvidencesFromMediaUrl(detail.mediaUrl, IMG_CREATION)
                    CardImageSection(
                        title = "",
                        evidences = imageEvidences,
                    )
                }

                "video" -> {
                    // Show video using CardVideoSection
                    val videoEvidences =
                        createEvidencesFromMediaUrl(detail.mediaUrl, VIDEO_CREATION)
                    CardVideoSection(
                        title = "",
                        evidences = videoEvidences,
                    )
                }

                "audio" -> {
                    // Show audio using CardAudioSection
                    val audioEvidences =
                        createEvidencesFromMediaUrl(detail.mediaUrl, AUDIO_CREATION)
                    CardAudioSection(
                        title = "",
                        evidences = audioEvidences,
                    )
                }

                "pdf" -> {
                    // Show PDF using CardPdfSection
                    val pdfEvidences = createEvidencesFromMediaUrl(detail.mediaUrl, "PDF")
                    CardPdfSection(
                        title = "",
                        evidences = pdfEvidences,
                    )
                }

                else -> {
                    // Other file type or unrecognized type
                    if (detail.mediaUrl.isNotEmpty()) {
                        // Use CardPdfSection as fallback for generic files
                        val fileEvidences = createEvidencesFromMediaUrl(detail.mediaUrl, "FILE")
                        CardPdfSection(
                            title = "",
                            evidences = fileEvidences,
                        )
                    }
                }
            }
        }
    }
}

// Helper function to create evidences from media URL
private fun createEvidencesFromMediaUrl(
    mediaUrl: String,
    evidenceType: String,
): List<Evidence> {
    if (mediaUrl.isEmpty()) return emptyList()

    return listOf(
        Evidence(
            id = mediaUrl.hashCode().toString(),
            cardId = "",
            siteId = "",
            url = mediaUrl,
            type = evidenceType,
            status = STATUS_A,
            createdAt = null,
            updatedAt = null,
            deletedAt = null,
        ),
    )
}
