package com.ih.osm.ui.components.cilt

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ih.osm.domain.model.Sequence

@Composable
fun SequenceCard(
    sequence: Sequence,
    index: Int,
) {
    val backgroundColor =
        when (sequence.secuenceColor.lowercase()) {
            "verde" -> Color(0xFFB9F6CA)
            "rojo" -> Color(0xFFFFCDD2)
            "amarillo" -> Color(0xFFFFF9C4)
            else -> Color.LightGray
        }

    var status by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Secuencia $index - ${sequence.levelName}", style = MaterialTheme.typography.titleLarge)
            Text("Tipo: ${sequence.ciltTypeName}", style = MaterialTheme.typography.bodyMedium)
            Text("Referencia: ${sequence.referencePoint ?: "N/A"}")
            Text("Status: ${sequence.status}")
            Text("Parada máquina: ${if (sequence.machineStopped == 1) "Sí" else "No"}")
            Text("Motivo de paro: ${sequence.stoppageReason}")
            Text("Listado de herramientas: ${sequence.toolsRequired}")
            Text("Parámetro Ok: ${sequence.standardOk}")

            Spacer(modifier = Modifier.height(8.dp))

            Text("Pasos a seguir:")
            sequence.secuenceList.split(";").forEach { step ->
                Text("• ${step.trim()}")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
