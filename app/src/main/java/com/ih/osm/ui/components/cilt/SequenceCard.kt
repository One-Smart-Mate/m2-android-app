package com.ih.osm.ui.components.cilt

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ih.osm.R
import com.ih.osm.core.ui.functions.getColorFromHex
import com.ih.osm.domain.model.ExecutionStatus
import com.ih.osm.domain.model.Sequence
import com.ih.osm.ui.extensions.getExecutionStatus
import com.ih.osm.ui.extensions.toHourFromIso
import com.ih.osm.ui.extensions.toMinutesAndSeconds
import com.ih.osm.ui.navigation.navigateToCiltDetail

@Composable
fun SequenceCard(
    sequence: Sequence,
    navController: NavController,
) {
    val execution = sequence.executions.first()

    val status =
        execution.secuenceSchedule.getExecutionStatus(
            sequenceStart = execution.secuenceStart,
            allowExecuteBefore = execution.allowExecuteBefore,
            allowExecuteBeforeMinutes = execution.allowExecuteBeforeMinutes,
            toleranceBeforeMinutes = execution.toleranceBeforeMinutes,
            toleranceAfterMinutes = execution.toleranceAfterMinutes,
            allowExecuteAfterDue = execution.allowExecuteAfterDue,
        )

    val (statusText, statusColor) =
        when (status) {
            ExecutionStatus.PREMATURE -> stringResource(R.string.status_premature) to Color(0xFFFFEB3B)
            ExecutionStatus.EXPIRED -> stringResource(R.string.status_expired) to Color(0xFFF44336)
            ExecutionStatus.ON_TIME -> stringResource(R.string.status_on_time) to Color(0xFF4CAF50)
            ExecutionStatus.PENDING -> stringResource(R.string.status_pending) to Color(0xFF2196F3)
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(id = R.dimen.card_padding_horizontal),
                    vertical = dimensionResource(id = R.dimen.card_padding_vertical),
                )
                .clickable { navController.navigateToCiltDetail(sequence.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(end = 32.dp),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.content_padding)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = sequence.executions.firstOrNull()?.siteExecutionId.toString(),
                        style =
                            MaterialTheme.typography.titleMedium
                                .copy(fontWeight = FontWeight.Bold),
                    )
                    Text(
                        text = sequence.ciltTypeName,
                        style =
                            MaterialTheme.typography.titleMedium
                                .copy(fontWeight = FontWeight.Bold),
                    )
                    Box(
                        modifier =
                            Modifier
                                .size(dimensionResource(id = R.dimen.circle_shape_size))
                                .background(
                                    color = getColorFromHex(sequence.secuenceColor),
                                    shape = CircleShape,
                                ),
                    )
                    Text(
                        text = sequence.executions.firstOrNull()?.secuenceSchedule.toHourFromIso(),
                        style =
                            MaterialTheme.typography.titleMedium
                                .copy(fontWeight = FontWeight.Bold),
                    )
                }

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.content_padding)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (sequence.executions.firstOrNull()?.secuenceStart != null) {
                        Text(
                            text = sequence.executions.firstOrNull()?.secuenceStart.toHourFromIso(),
                            style =
                                MaterialTheme.typography.titleMedium
                                    .copy(fontWeight = FontWeight.Bold),
                        )
                    }

                    if (sequence.executions.firstOrNull()?.realDuration != null) {
                        Text(
                            text =
                                sequence.executions.firstOrNull()?.realDuration?.toMinutesAndSeconds()
                                    ?: "0 s",
                            style =
                                MaterialTheme.typography.titleMedium
                                    .copy(fontWeight = FontWeight.Bold),
                        )
                    }
                }

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.content_padding)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .background(
                                    color = statusColor,
                                    shape = MaterialTheme.shapes.small,
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = statusText,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        )
                    }

                    if (sequence.executions.firstOrNull()?.nok == true) {
                        Box(
                            modifier =
                                Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.error,
                                        shape = MaterialTheme.shapes.small,
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.nok),
                                color = MaterialTheme.colorScheme.onError,
                                style =
                                    MaterialTheme.typography.titleMedium
                                        .copy(fontWeight = FontWeight.Bold),
                            )
                        }
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = stringResource(R.string.view_details),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp),
            )
        }
    }
}
