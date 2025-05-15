package com.ih.osm.ui.components.cilt

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ih.osm.data.model.UserCiltData

@Composable
fun CiltDetailSection(data: UserCiltData) {
    Column(modifier = Modifier.padding(16.dp)) {
        data.positions.forEach { position ->
            Text("Posición: ${position.name}", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))

            position.ciltMasters.forEach { cilt ->
                Text("Rutina: ${cilt.ciltName}", style = MaterialTheme.typography.titleMedium)
                Text("Descripción: ${cilt.ciltDescription}")
                Text("Creó: ${cilt.creatorName}")
                Text("Revisó: ${cilt.reviewerName}")
                Text("Autorizó: ${cilt.approvedByName}")
                Text("Fecha última actualización: ${cilt.updatedAt ?: "N/A"}")
                Text("Status: ${cilt.status}")

                Spacer(modifier = Modifier.height(8.dp))

                cilt.sequences.forEachIndexed { index, sequence ->
                    SequenceCard(sequence = sequence, index = index + 1)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
