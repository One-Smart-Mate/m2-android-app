package com.ih.osm.ui.components.procedure

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.ih.osm.R
import com.ih.osm.domain.model.Procedure
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.extensions.getTextColor
import com.ih.osm.ui.theme.PaddingNormal

@Composable
fun ProcedureItemCard(
    procedure: Procedure,
    onClick: () -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingNormal, vertical = 4.dp)
                .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(PaddingNormal),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = procedure.title,
                        style =
                            MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = getTextColor(),
                            ),
                    )
                    CustomSpacer(space = SpacerSize.TINY)
                    Text(
                        text = procedure.objective,
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                color = getTextColor().copy(alpha = 0.7f),
                            ),
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription =
                        if (isExpanded) {
                            stringResource(R.string.collapse)
                        } else {
                            stringResource(
                                R.string.expand,
                            )
                        },
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    CustomSpacer()
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                    CustomSpacer()

                    // Creator and reviewer info
                    Text(
                        text = stringResource(R.string.opl_created_by, procedure.creatorName),
                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                color = getTextColor().copy(alpha = 0.6f),
                            ),
                    )
                    Text(
                        text = stringResource(R.string.opl_reviewed_by, procedure.reviewerName),
                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                color = getTextColor().copy(alpha = 0.6f),
                            ),
                    )

                    // Procedure steps
                    if (procedure.details.isNotEmpty()) {
                        CustomSpacer()
                        Text(
                            text = stringResource(R.string.procedure_steps),
                            style =
                                MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = getTextColor(),
                                ),
                        )
                        CustomSpacer(space = SpacerSize.TINY)
                        procedure.details.forEachIndexed { index, detail ->
                            Text(
                                text = "${index + 1}. ${detail.text}",
                                style =
                                    MaterialTheme.typography.bodySmall.copy(
                                        color = getTextColor().copy(alpha = 0.8f),
                                    ),
                                modifier = Modifier.padding(vertical = 2.dp),
                            )
                        }
                    } else {
                        CustomSpacer()
                        Text(
                            text = stringResource(R.string.no_steps_defined_for_procedure),
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    color = getTextColor().copy(alpha = 0.5f),
                                ),
                        )
                    }

                    CustomSpacer()
                    Text(
                        text = "${procedure.details.size} ${stringResource(R.string.steps)}",
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
}
