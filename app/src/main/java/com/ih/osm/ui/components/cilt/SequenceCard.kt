package com.ih.osm.ui.components.cilt

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ih.osm.core.ui.functions.getColorFromHex
import com.ih.osm.domain.model.Sequence
import com.ih.osm.ui.components.ExpandableCard

@Composable
fun SequenceCard(
    sequence: Sequence,
    index: Int,
) {
    val focusManager = LocalFocusManager.current
    val backgroundColor = getColorFromHex(sequence.secuenceColor)

    var parameterFound by remember { mutableStateOf("") }
    var finalParameter by remember { mutableStateOf("") }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
                .clickable { focusManager.clearFocus() }
                .padding(1.dp),
    ) {
        ExpandableCard(
            title = "Secuencia $index - ${sequence.levelName}",
            expanded = true,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
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
