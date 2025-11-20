package com.ih.osm.ui.components.cilt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ih.osm.R
import com.ih.osm.core.ui.functions.getColorFromHex
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.Sequence
import com.ih.osm.domain.model.sortByTime
import com.ih.osm.ui.components.ExpandableCard
import com.ih.osm.ui.extensions.calculateRemainingDaysFromIso
import com.ih.osm.ui.extensions.fromIsoToFormattedDate
import com.ih.osm.ui.navigation.navigateToSequence

@Composable
fun CiltDetailSection(
    data: CiltData,
    navController: NavController,
    isProcedureMode: Boolean = false,
    onCreateExecution: ((Sequence, Int, String) -> Unit)? = null,
) {
    data.positions.forEach { position ->
        position.ciltMasters.forEach { cilt ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = CardDefaults.outlinedCardBorder(),
                colors =
                    CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    AnatomyHorizontalSection(
                        title = stringResource(R.string.routine_label),
                        description = cilt.ciltName,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AnatomyHorizontalSection(
                        title = stringResource(R.string.position_label),
                        description = position.name,
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    AnatomyHorizontalSection(
                        title = stringResource(R.string.description_label),
                        description = cilt.ciltDescription,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ExpandableCard(
                        title = stringResource(R.string.cilt_details),
                        expanded = false,
                    ) {
                        AnatomyHorizontalSection(
                            title = stringResource(R.string.cilt_created_by),
                            description = cilt.creatorName,
                        )
                        AnatomyHorizontalSection(
                            title = stringResource(R.string.cilt_reviewed_by),
                            description = cilt.reviewerName,
                        )
                        AnatomyHorizontalSection(
                            title = stringResource(R.string.cilt_approved_by),
                            description = cilt.approvedByName,
                        )
                        AnatomyHorizontalSection(
                            title = stringResource(R.string.cilt_due_date),
                            description = cilt.ciltDueDate.fromIsoToFormattedDate(),
                        )

                        val daysRemaining = calculateRemainingDaysFromIso(cilt.ciltDueDate)
                        if (daysRemaining > 0 && daysRemaining < 5) {
                            val message =
                                if (daysRemaining == 1) {
                                    stringResource(R.string.days_remaining_singular, daysRemaining)
                                } else {
                                    stringResource(R.string.days_remaining_plural, daysRemaining)
                                }
                            Text(
                                text = message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(4.dp),
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        CiltDiagramSection(imageUrl = cilt.urlImgLayout)

                        Spacer(modifier = Modifier.height(8.dp))

                        cilt.sequences.forEach { sequence ->
                            if (isProcedureMode) {
                                SequenceCard(
                                    sequence = sequence,
                                    onCreateExecution = {
                                        onCreateExecution?.invoke(sequence, position.id, "")
                                    },
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            } else {
                                sequence.executions
                                    .sortByTime()
                                    .forEach { execution ->
                                        ExecutionCard(
                                            execution = execution,
                                        ) {
                                            navController.navigateToSequence(sequence.id, execution.id)
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AnatomyHorizontalSection(
    title: String,
    description: String,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
fun SequenceCard(
    sequence: Sequence,
    onCreateExecution: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text =
                        if (sequence.secuenceList.length > 30) {
                            sequence.secuenceList.take(30) + "..."
                        } else {
                            sequence.secuenceList
                        },
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = sequence.ciltTypeName,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier =
                            Modifier
                                .size(12.dp)
                                .background(
                                    color = getColorFromHex(sequence.secuenceColor),
                                    shape = CircleShape,
                                ),
                    )
                }
            }
            Button(
                onClick = onCreateExecution,
            ) {
                Text(stringResource(R.string.create))
            }
        }
    }
}
