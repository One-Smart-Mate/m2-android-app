package com.ih.osm.ui.components.cilt

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.ih.osm.R
import com.ih.osm.core.ui.functions.getColorFromHex
import com.ih.osm.domain.model.Sequence
import com.ih.osm.ui.navigation.navigateToCiltDetail

@Composable
fun SequenceCard(
    sequence: Sequence,
    navController: NavController,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(id = R.dimen.card_padding_horizontal),
                    vertical = dimensionResource(id = R.dimen.card_padding_vertical),
                )
                .clickable { navController.navigateToCiltDetail(sequence.id) },
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
                text = sequence.order.toString(),
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(dimensionResource(id = R.dimen.circle_shape_size))
                            .background(
                                color = getColorFromHex(sequence.secuenceColor),
                                shape = CircleShape,
                            ),
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.view_details),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
