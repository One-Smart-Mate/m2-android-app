package com.ih.osm.ui.components.procedure

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ih.osm.R
import com.ih.osm.domain.model.CiltProcedureData
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.extensions.getTextColor
import com.ih.osm.ui.theme.PaddingNormal

@Composable
fun CiltProcedureCard(
    ciltMaster: CiltProcedureData.CiltMaster,
    positionId: Int,
    levelId: String,
    creatingExecutionForSequence: Int?,
    onCreateExecution: (CiltProcedureData.Sequence, Int, String) -> Unit,
    onNavigateToExecution: (Int) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingNormal, vertical = 4.dp),
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
                        text = ciltMaster.ciltName,
                        style =
                            MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = getTextColor(),
                            ),
                    )
                    CustomSpacer(space = SpacerSize.TINY)
                    Text(
                        text = ciltMaster.ciltDescription,
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                color = getTextColor().copy(alpha = 0.7f),
                            ),
                    )
                }
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                ) {
                    Icon(
                        imageVector =
                            if (isExpanded) {
                                Icons.Filled.KeyboardArrowUp
                            } else {
                                Icons.Filled.KeyboardArrowDown
                            },
                        contentDescription =
                            if (isExpanded) {
                                stringResource(R.string.collapse)
                            } else {
                                stringResource(R.string.expand)
                            },
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    CustomSpacer()
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                    CustomSpacer()

                    // Creator and reviewer info
                    Text(
                        text = stringResource(R.string.opl_created_by, ciltMaster.creatorName),
                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                color = getTextColor().copy(alpha = 0.6f),
                            ),
                    )
                    Text(
                        text = stringResource(R.string.opl_reviewed_by, ciltMaster.reviewerName),
                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                color = getTextColor().copy(alpha = 0.6f),
                            ),
                    )

                    // Sequences
                    if (ciltMaster.sequences.isNotEmpty()) {
                        CustomSpacer()
                        Text(
                            text = stringResource(R.string.available_sequences),
                            style =
                                MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = getTextColor(),
                                ),
                        )
                        CustomSpacer(space = SpacerSize.TINY)

                        Column {
                            ciltMaster.sequences.sortedBy { it.order }.forEach { sequence ->
                                SequenceCard(
                                    sequence = sequence,
                                    positionId = positionId,
                                    levelId = levelId,
                                    creatingExecutionForSequence = creatingExecutionForSequence,
                                    onCreateExecution = onCreateExecution,
                                    onNavigateToExecution = onNavigateToExecution,
                                )

                                // Show executions for this sequence if available
                                if (sequence.executions.isNotEmpty()) {
                                    sequence.executions.forEach { execution ->
                                        ExecutionCard(execution = execution)
                                    }
                                }

                                CustomSpacer(space = SpacerSize.SMALL)
                            }
                        }
                    } else {
                        CustomSpacer()
                        Text(
                            text = stringResource(R.string.no_sequences_defined),
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    color = getTextColor().copy(alpha = 0.5f),
                                ),
                        )
                    }

                    CustomSpacer()
                    Text(
                        text =
                            stringResource(
                                R.string.number_of_sequences,
                                ciltMaster.sequences.size,
                            ),
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

@Composable
private fun SequenceCard(
    sequence: CiltProcedureData.Sequence,
    positionId: Int,
    levelId: String,
    creatingExecutionForSequence: Int?,
    onCreateExecution: (CiltProcedureData.Sequence, Int, String) -> Unit,
    onNavigateToExecution: (Int) -> Unit,
) {
    val sequenceColor =
        try {
            Color(android.graphics.Color.parseColor("#${sequence.secuenceColor}"))
        } catch (e: Exception) {
            MaterialTheme.colorScheme.primary
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(PaddingNormal),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(sequenceColor),
                )
                CustomSpacer(space = SpacerSize.SMALL)
                Column {
                    Text(
                        text = sequence.ciltTypeName,
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = getTextColor(),
                            ),
                    )
                    Text(
                        text =
                            stringResource(
                                R.string.order_and_time,
                                sequence.order,
                                sequence.standardTime,
                            ),
                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                color = getTextColor().copy(alpha = 0.6f),
                            ),
                    )
                    if (sequence.specialWarning != null) {
                        Text(
                            text = "⚠️ ${sequence.specialWarning}",
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.error,
                                ),
                        )
                    }
                }
            }

            Button(
                onClick = {
                    onCreateExecution(sequence, positionId, levelId)
                },
                enabled = creatingExecutionForSequence != sequence.id,
                modifier = Modifier.padding(start = 8.dp),
            ) {
                if (creatingExecutionForSequence == sequence.id) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                    )
                    CustomSpacer(space = SpacerSize.TINY)
                    Text(stringResource(R.string.creating))
                } else {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = stringResource(R.string.create_execution),
                        modifier = Modifier.size(16.dp),
                    )
                    CustomSpacer(space = SpacerSize.TINY)
                    Text(stringResource(R.string.execute))
                }
            }
        }
    }
}

@Composable
private fun ExecutionCard(execution: CiltProcedureData.Execution) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CustomSpacer(space = SpacerSize.TINY)
                    Text(
                        text = "${execution.siteExecutionId}",
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = getTextColor(),
                            ),
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(R.string.time),
                        tint = getTextColor().copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp),
                    )
                    CustomSpacer(space = SpacerSize.TINY)
                    Text(
                        text = stringResource(R.string.execution_minutes, execution.duration),
                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                color = getTextColor().copy(alpha = 0.6f),
                            ),
                    )
                }
            }

            CustomSpacer(space = SpacerSize.TINY)

            Text(
                text = execution.route ?: stringResource(R.string.no_path_specified),
                style =
                    MaterialTheme.typography.bodySmall.copy(
                        color = getTextColor().copy(alpha = 0.7f),
                    ),
            )

            if (execution.standardOk.isNotEmpty()) {
                CustomSpacer(space = SpacerSize.TINY)
                Text(
                    text = "✓ ${execution.standardOk}",
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                        ),
                )
            }

            if (execution.referenceOpl.title.isNotEmpty()) {
                CustomSpacer(space = SpacerSize.TINY)
                Text(
                    text = "📖 OPL: ${execution.referenceOpl.title}",
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            color = getTextColor().copy(alpha = 0.8f),
                        ),
                )
            }
        }
    }
}
