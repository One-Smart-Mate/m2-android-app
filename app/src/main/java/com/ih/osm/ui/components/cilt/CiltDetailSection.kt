package com.ih.osm.ui.components.cilt

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ih.osm.domain.model.CiltData
import com.ih.osm.ui.components.ExpandableCard
import com.ih.osm.ui.components.SectionTag

@Composable
fun CiltDetailSection(
    data: CiltData,
    navController: NavController,
) {
    data.positions.forEach { position ->
        position.ciltMasters.forEach { cilt ->
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Posición: ${position.name}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = "Rutina: ${cilt.ciltName}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = "Descripción: ${cilt.ciltDescription}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            ExpandableCard(title = "Detalles", expanded = true) {
                SectionTag(title = "Creó", value = cilt.creatorName)
                SectionTag(title = "Revisó", value = cilt.reviewerName)
                SectionTag(title = "Autorizó", value = cilt.approvedByName)
                SectionTag(
                    title = "Fecha última actualización",
                    value = cilt.updatedAt ?: "N/A",
                )
                SectionTag(title = "Status", value = cilt.status)
            }

            CiltDiagramSection(imageUrl = cilt.urlImgLayout)

            Spacer(modifier = Modifier.height(8.dp))

            cilt.sequences.forEachIndexed { index, sequence ->
                SequenceCard(
                    sequence = sequence,
                    index = index + 1,
                    navController = navController,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
