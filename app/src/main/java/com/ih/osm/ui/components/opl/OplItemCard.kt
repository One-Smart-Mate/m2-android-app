package com.ih.osm.ui.components.opl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.model.OplDetail
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.CustomTag
import com.ih.osm.ui.components.SpacerDirection
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.TagSize
import com.ih.osm.ui.components.TagType
import com.ih.osm.ui.components.evidence.PreviewVideo
import com.ih.osm.ui.components.images.PreviewImage
import com.ih.osm.ui.extensions.getTextColor
import com.ih.osm.ui.pages.createcard.PhotoCardItem
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.PaddingSmall
import com.ih.osm.ui.theme.PaddingTiny
import com.ih.osm.ui.theme.Radius8
import com.ih.osm.ui.theme.Size100
import com.ih.osm.ui.theme.Size150
import com.ih.osm.ui.utils.EMPTY

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
            // Removido onClick() para evitar navegación
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
            // Header con título y tipo
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
                    // Removí el ID del OPL
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
                            contentDescription = if (isExpanded) "Contraer" else "Expandir",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }
            }

            CustomSpacer()

            // Objetivo
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

            // Información del creador y revisor
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
                        text = "Creado por: ${opl.creatorName}",
                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                color = getTextColor().copy(alpha = 0.6f),
                            ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            // Detalles count y fecha
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
                        text = "${opl.details.size} pasos",
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

            // Indicador de revisor si existe
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
                        CustomSpacer(direction = SpacerDirection.HORIZONTAL, space = SpacerSize.TINY)
                        Text(
                            text = "Revisado por: ${opl.reviewerName}",
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Medium,
                                ),
                        )
                    }
                }
            }

            // Sección expandible de detalles
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
                            text = "Pasos del OPL:",
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
                                text = "No hay pasos definidos para este OPL",
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
    var imageUrl by remember { mutableStateOf(EMPTY) }
    var openImage by remember { mutableStateOf(false) }
    var videoUrl by remember { mutableStateOf(EMPTY) }
    var openVideo by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    Column(modifier = modifier) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(PaddingSmall),
            verticalAlignment = Alignment.Top,
        ) {
// Número del paso en un círculo
            Box(
                modifier =
                    Modifier
                        .size(28.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(14.dp),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${detail.order}",
                    style =
                        MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                        ),
                )
            }

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

                    if (detail.type.isNotEmpty()) {
                        CustomSpacer(direction = SpacerDirection.HORIZONTAL, space = SpacerSize.SMALL)
                        CustomTag(
                            title = detail.type.uppercase(),
                            tagSize = TagSize.SMALL,
                            tagType = TagType.OUTLINE,
                        )
                    }
                }
            }
        }

// Mostrar media si existe
        if (detail.mediaUrl.isNotEmpty()) {
            CustomSpacer(space = SpacerSize.TINY)

            when {
                detail.mediaUrl.endsWith(".jpg", true) ||
                    detail.mediaUrl.endsWith(".jpeg", true) ||
                    detail.mediaUrl.endsWith(".png", true) ||
                    detail.mediaUrl.endsWith(".webp", true) -> {
// Mostrar imagen
                    PhotoCardItem(
                        model = detail.mediaUrl,
                        showIcon = false,
                        modifier =
                            Modifier
                                .width(Size150)
                                .height(Size100)
                                .clickable {
                                    imageUrl = detail.mediaUrl
                                    openImage = true
                                },
                    )
                }
                detail.mediaUrl.endsWith(".mp4", true) ||
                    detail.mediaUrl.endsWith(".mov", true) ||
                    detail.mediaUrl.endsWith(".avi", true) -> {
                    // Mostrar video
                    PhotoCardItem(
                        model = detail.mediaUrl,
                        showIcon = false,
                        modifier =
                            Modifier
                                .width(Size150)
                                .height(Size100)
                                .clickable {
                                    videoUrl = detail.mediaUrl
                                    openVideo = true
                                },
                    )
                }
                detail.mediaUrl.endsWith(".pdf", true) -> {
                    // Mostrar link de PDF
                    Row(
                        modifier =
                            Modifier
                                .clickable {
                                    uriHandler.openUri(detail.mediaUrl)
                                }
                                .padding(PaddingSmall),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "📄",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(end = 4.dp),
                        )
                        Text(
                            text = "Ver PDF",
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                ),
                        )
                    }
                }
                else -> {
                    // Otro tipo de archivo - abrir en navegador
                    Row(
                        modifier =
                            Modifier
                                .clickable {
                                    uriHandler.openUri(detail.mediaUrl)
                                }
                                .padding(PaddingSmall),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "📎",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(end = 4.dp),
                        )
                        Text(
                            text = "Ver archivo adjunto",
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                ),
                        )
                    }
                }
            }
        }

        // Previews
        PreviewImage(openImage = openImage, model = imageUrl) {
            openImage = false
            imageUrl = EMPTY
        }

        PreviewVideo(openVideo = openVideo, url = videoUrl) {
            videoUrl = EMPTY
            openVideo = false
        }
    }
}
