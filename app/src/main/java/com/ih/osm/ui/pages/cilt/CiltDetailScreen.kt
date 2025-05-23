package com.ih.osm.ui.pages.cilt

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ih.osm.core.ui.functions.getColorFromHex
import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.domain.model.Sequence
import com.ih.osm.domain.model.mockCiltData
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.ExpandableCard
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.launchers.CameraLauncher
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.Size20
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun CiltDetailScreen(
    sequenceId: Int,
    navController: NavController,
    viewModel: CiltRoutineViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    val isLoading = state.isLoading

    val sequence = viewModel.getSequenceById(sequenceId)

    when {
        isLoading -> {
            LoadingScreen()
        }

        sequence != null -> {
            Scaffold { padding ->
                LazyColumn(
                    modifier = Modifier.defaultScreen(padding),
                ) {
                    stickyHeader {
                        CustomAppBar(
                            navController = navController,
                            content = { CiltDetailHeader(sequence) },
                        )
                    }

                    item {
                        SequenceDetailContent(sequence = sequence) { evidenceRequest ->
                            viewModel.createEvidence(evidenceRequest)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CiltDetailHeader(sequence: Sequence) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = sequence.levelName,
            style =
                MaterialTheme.typography.titleLarge
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
    }
}

@Composable
fun SequenceDetailContent(
    sequence: Sequence,
    onAddEvidence: (CiltEvidenceRequest) -> Unit,
) {
    fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }

    var parameterFound by remember { mutableStateOf("") }
    var finalParameter by remember { mutableStateOf("") }
    var evidenceUriString by remember { mutableStateOf<String?>(null) }

    ExpandableCard(
        title = "Detalles de la secuencia",
        expanded = true,
    ) {
        InfoItem(label = "Tipo", value = sequence.ciltTypeName)
        InfoItem(label = "Duración", value = sequence.standardTime.toString())
        if (sequence.machineStopped == 1) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color(0xFFFFCDD2), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp),
            ) {
                Text(
                    text = "⚠ Ejecutar con máquina detenida",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB71C1C),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color(0xFFEEEEEE), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp),
            ) {
                Text(
                    text = "No es necesario detener la máquina",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424242),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {},
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
        ) {
            Text("Iniciar secuencia")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Pasos a seguir:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        sequence.secuenceList.split("\n").forEach { step ->
            Text("• ${step.trim()}", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(12.dp))

        InfoItem(label = "Referencia", value = sequence.referencePoint ?: "N/A")

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Listado de herramientas:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        sequence.toolsRequired.split("\n").forEach { tool ->
            Text("• ${tool.trim()}", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(8.dp))

        InfoItem(label = "Parámetro Ok", value = sequence.standardOk)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {},
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
        ) {
            Text("Ver OPL o SOP")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = parameterFound,
            onValueChange = { parameterFound = it },
            label = { Text("Parámetro encontrado") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier =
                Modifier
                    .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            CameraLauncher { uri ->
                evidenceUriString = uri.toString()

                val evidenceRequest =
                    CiltEvidenceRequest(
                        siteId = sequence.siteId,
                        positionId = sequence.positionId,
                        ciltId = sequence.ciltMstrId,
                        ciltExecutionsEvidencesId = sequence.executions.firstOrNull()?.id ?: 0,
                        evidenceUrl = evidenceUriString!!,
                        createdAt = getCurrentTimestamp(),
                    )
                onAddEvidence(evidenceRequest)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {},
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
        ) {
            Text("Ver OPL o SOP de remediación")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = finalParameter,
            onValueChange = { finalParameter = it },
            label = { Text("Parámetro final si se aplicó remediación") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier =
                Modifier
                    .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            CameraLauncher {
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        InfoItem(
            label = "Motivo de paro",
            value = if (sequence.stoppageReason == 1) "Sí" else "No",
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {},
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
        ) {
            Text("NOK Generar tarjeta AM")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {},
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
        ) {
            Text("Terminar secuencia")
        }
    }
}

@Composable
fun InfoItem(
    label: String,
    value: String,
) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

private val mockSequence =
    mockCiltData()
        .positions.first()
        .ciltMasters.first()
        .sequences.first()

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
private fun CiltDetailScreenPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            SequenceDetailContent(
                sequence = mockSequence,
                onAddEvidence = {},
            )
        }
    }
}
