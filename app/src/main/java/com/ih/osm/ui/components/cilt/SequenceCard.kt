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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ih.osm.core.ui.functions.getColorFromHex
import com.ih.osm.domain.model.Sequence
import com.ih.osm.ui.navigation.navigateToCiltDetail
import com.ih.osm.ui.theme.Size20

@Composable
fun SequenceCard(
    sequence: Sequence,
    index: Int,
    navController: NavController,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { navController.navigateToCiltDetail(sequence.id) },
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Secuencia $index",
                style =
                    MaterialTheme.typography.titleMedium
                        .copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = sequence.levelName,
                style =
                    MaterialTheme.typography.titleMedium
                        .copy(fontWeight = FontWeight.Bold),
            )
            Box(
                modifier =
                    Modifier
                        .size(Size20)
                        .background(
                            color = getColorFromHex(sequence.secuenceColor),
                            shape = CircleShape,
                        ),
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Ver Detalles",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
