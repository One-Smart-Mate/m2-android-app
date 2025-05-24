package com.ih.osm.ui.components.opl

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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ih.osm.domain.model.Opl
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.CustomTag
import com.ih.osm.ui.components.SpacerDirection
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.TagSize
import com.ih.osm.ui.components.TagType
import com.ih.osm.ui.extensions.getTextColor
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.PaddingSmall
import com.ih.osm.ui.theme.PaddingTiny
import com.ih.osm.ui.theme.Radius8

@Composable
fun OplItemCard(
    opl: Opl,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingNormal, vertical = PaddingSmall),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(PaddingNormal),
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
                    CustomSpacer(space = SpacerSize.TINY)
                    Text(
                        text = "ID: ${opl.id}",
                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                color = getTextColor().copy(alpha = 0.6f),
                            ),
                    )
                }
                CustomTag(
                    title = opl.oplType.uppercase(),
                    tagSize = TagSize.SMALL,
                    tagType = TagType.OUTLINE,
                )
            }

            CustomSpacer()

            // Objetivo
            Row(
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
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
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
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

                // Ya es String, no necesita formateo
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
    }
}
