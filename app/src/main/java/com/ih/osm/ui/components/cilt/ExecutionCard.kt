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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ih.osm.R
import com.ih.osm.core.ui.functions.getColorFromHex
import com.ih.osm.domain.model.Execution
import com.ih.osm.domain.model.getStatus
import com.ih.osm.domain.model.getStatusColor
import com.ih.osm.domain.model.getStatusTextColor
import com.ih.osm.ui.extensions.parseUTCToLocal
import com.ih.osm.ui.extensions.toHourMinuteString
import com.ih.osm.ui.theme.PaddingSmall

@Composable
fun ExecutionCard(
    execution: Execution,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = PaddingSmall, horizontal = PaddingSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    // .padding(PaddingSmall),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = execution.siteExecutionId.toString(),
                        style =
                            MaterialTheme.typography.titleMedium
                                .copy(fontWeight = FontWeight.Bold),
                    )
                    Text(
                        text = execution.ciltTypeName,
                        style =
                            MaterialTheme.typography.titleMedium
                                .copy(fontWeight = FontWeight.Bold),
                    )
                    Box(
                        modifier =
                            Modifier
                                .size(dimensionResource(id = R.dimen.circle_shape_size))
                                .background(
                                    color = getColorFromHex(execution.secuenceColor),
                                    shape = CircleShape,
                                ),
                    )
                }
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = PaddingSmall),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text =
                            execution.secuenceSchedule
                                .parseUTCToLocal()
                                .toHourMinuteString(),
                        style =
                            MaterialTheme.typography.titleMedium
                                .copy(fontWeight = FontWeight.Bold),
                    )
                    Box(
                        modifier =
                            Modifier
                                .background(
                                    color = execution.getStatusColor(),
                                    shape = MaterialTheme.shapes.small,
                                ).padding(horizontal = 12.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = execution.getStatus(),
                            color = execution.getStatusTextColor(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = stringResource(R.string.view_details),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier =
                    Modifier
                        .padding(end = 8.dp),
            )
        }
    }
}
