package com.ih.osm.ui.pages.cilt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ih.osm.R
import com.ih.osm.core.ui.functions.getColorFromHex
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.model.Sequence
import com.ih.osm.domain.model.stopMachine
import com.ih.osm.domain.model.stoppageReason
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.CustomTextField
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.launchers.CameraLauncher
import com.ih.osm.ui.components.opl.OplItemCard
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.theme.Size20

@Composable
fun CiltDetailScreen(
    sequenceId: Int,
    navController: NavController,
    viewModel: CiltRoutineViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    val isLoading = state.isLoading

    val sequence = viewModel.getSequenceById(sequenceId)
    val opl = state.opl
    val remediationOpl = state.remediationOpl

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
                        SequenceDetailContent(
                            sequence = sequence,
                            onStartExecution = { executionId ->
                                viewModel.startSequenceExecution(
                                    executionId,
                                )
                            },
                            onStopExecution = {
                                    executionId,
                                    initialParameter,
                                    evidenceAtCreation,
                                    finalParameter,
                                    evidenceAtFinal,
                                    nok,
                                    amTagId,
                                ->
                                viewModel.stopSequenceExecution(
                                    executionId = executionId,
                                    initialParameter = initialParameter,
                                    evidenceAtCreation = evidenceAtCreation,
                                    finalParameter = finalParameter,
                                    evidenceAtFinal = evidenceAtFinal,
                                    nok = nok,
                                    amTagId = amTagId,
                                )
                            },
                            onCreateEvidence = { siteId, positionId, ciltId, ciltExecutionsEvidencesId, evidenceUrl ->
                                viewModel.createEvidence(
                                    siteId = siteId,
                                    positionId = positionId,
                                    ciltId = ciltId,
                                    ciltExecutionsEvidencesId = ciltExecutionsEvidencesId,
                                    evidenceUrl = evidenceUrl
                                )
                            },
                            onUpdateEvidence = { id, siteId, positionId, ciltId, ciltExecutionsEvidencesId, evidenceUrl ->
                                viewModel.updateEvidence(
                                    id = id,
                                    siteId = siteId,
                                    positionId = positionId,
                                    ciltId = ciltId,
                                    ciltExecutionsEvidencesId = ciltExecutionsEvidencesId,
                                    evidenceUrl = evidenceUrl
                                )
                            },
                            opl = opl,
                            getOplById = { viewModel.getOplById(it) },
                            remediationOpl = remediationOpl,
                            getRemediationOplById = { viewModel.getRemediationOplById(it) },
                        )
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
            text = sequence.executions.first().ciltTypeName,
            style =
            MaterialTheme.typography.titleLarge
                .copy(fontWeight = FontWeight.Bold),
        )
        Box(
            modifier =
            Modifier
                .size(Size20)
                .background(
                    color = getColorFromHex(sequence.executions.first().secuenceColor),
                    shape = CircleShape,
                ),
        )
    }
}

@Composable
fun SequenceDetailContent(
    sequence: Sequence,
    onStartExecution: (Int) -> Unit,
    onStopExecution: (
        executionId: Int,
        initialParameter: String,
        evidenceAtCreation: Boolean,
        finalParameter: String,
        evidenceAtFinal: Boolean,
        nok: Boolean,
        amTagId: Int,
    ) -> Unit,
    onCreateEvidence: (
        siteId: Int,
        positionId: Int,
        ciltId: Int,
        ciltExecutionsEvidencesId: Int,
        evidenceUrl: String
    ) -> Unit,
    onUpdateEvidence: (
        id: Int,
        siteId: Int,
        positionId: Int,
        ciltId: Int,
        ciltExecutionsEvidencesId: Int,
        evidenceUrl: String
    ) -> Unit,
    opl: Opl?,
    getOplById: (String) -> Unit,
    remediationOpl: Opl?,
    getRemediationOplById: (String) -> Unit,
) {
    var parameterFound by remember { mutableStateOf("") }
    var finalParameter by remember { mutableStateOf("") }
    var isParameterOk by remember { mutableStateOf(true) }

    InfoItem(label = stringResource(R.string.code_label), value = sequence.frecuencyCode)
    InfoItem(
        label = stringResource(R.string.duration_label),
        value = sequence.executions.firstOrNull()?.duration.toString(),
    )

    Box(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                if (sequence.executions
                        .first()
                        .stopMachine()
                ) {
                    Color(0xFFB71C1C)
                } else {
                    Color(0xFFEEEEEE)
                },
                shape = RoundedCornerShape(8.dp),
            )
            .padding(12.dp),
    ) {
        Text(
            text =
            if (sequence.executions.first().stopMachine()) {
                stringResource(
                    R.string.stoppage_required,
                )
            } else {
                stringResource(R.string.stoppage_not_required)
            },
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge,
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    InfoItem(
        label = stringResource(R.string.stop_reason_label),
        value =
        if (sequence.executions.first().stoppageReason()) {
            stringResource(R.string.stop_reason_yes)
        } else {
            stringResource(
                R.string.stop_reason_no,
            )
        },
    )

    Spacer(modifier = Modifier.height(12.dp))

    val executionId = sequence.executions.firstOrNull()?.id

    if (executionId != null) {
        Button(
            onClick = { onStartExecution(executionId) },
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        ) {
            Text(stringResource(R.string.start_sequence))
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = stringResource(R.string.steps_to_follow),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
    )
    sequence.executions.firstOrNull()?.secuenceList?.split("\n")?.forEach { step ->
        Text("• ${step.trim()}", style = MaterialTheme.typography.bodyLarge)
    }

    Spacer(modifier = Modifier.height(12.dp))

    InfoItem(
        label = stringResource(R.string.reference_label),
        value =
        sequence.executions.firstOrNull()?.referencePoint
            ?: stringResource(R.string.not_available),
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = stringResource(R.string.tools_list),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
    )
    sequence.executions.firstOrNull()?.toolsRequiered?.split("\n")?.forEach { tool ->
        Text("• ${tool.trim()}", style = MaterialTheme.typography.bodyLarge)
    }

    Spacer(modifier = Modifier.height(8.dp))

    InfoItem(
        label = stringResource(R.string.parameter_ok),
        value =
        sequence.executions.firstOrNull()?.standardOk
            ?: stringResource(R.string.not_available),
    )

    Spacer(modifier = Modifier.height(8.dp))

    val oplId = sequence.executions.firstOrNull()?.referenceOplSopId?.toString()

    if (opl == null && oplId != null) {
        Button(
            onClick = {
                getOplById(oplId)
            },
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        ) {
            Text(stringResource(R.string.view_opl_sop))
        }
    }

    opl?.let {
        Spacer(modifier = Modifier.height(8.dp))
        OplItemCard(opl = it, onClick = {})
    }

    Spacer(modifier = Modifier.height(8.dp))

    CustomTextField(
        modifier = Modifier.fillMaxWidth(),
        label = stringResource(R.string.parameter_found),
        placeholder = stringResource(R.string.enter_parameter_found),
        icon = Icons.Outlined.Menu,
        onChange = { parameterFound = it },
    )

    Spacer(modifier = Modifier.height(8.dp))

    Box(
        modifier =
        Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        CameraLauncher { imageUri ->
            val execution = sequence.executions.firstOrNull()
            if (execution != null) {
                onCreateEvidence(
                    execution.siteId,
                    execution.positionId,
                    execution.ciltId,
                    execution.id,
                    imageUri.toString()
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    val oplRemediationId = sequence.executions.firstOrNull()?.remediationOplSopId

    if (remediationOpl == null && oplRemediationId != null) {
        Button(
            onClick = {
                getRemediationOplById(oplRemediationId)
            },
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        ) {
            Text(stringResource(R.string.view_remediation))
        }
    }

    remediationOpl?.let {
        Spacer(modifier = Modifier.height(8.dp))
        OplItemCard(opl = it, onClick = {})
    }

    Spacer(modifier = Modifier.height(8.dp))

    CustomTextField(
        modifier = Modifier.fillMaxWidth(),
        label = stringResource(R.string.final_parameter),
        placeholder = stringResource(R.string.enter_final_parameter),
        icon = Icons.Outlined.Menu,
        onChange = { finalParameter = it },
    )

    Spacer(modifier = Modifier.height(8.dp))

    Box(
        modifier =
        Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        CameraLauncher { imageUri ->
            val execution = sequence.executions.firstOrNull()
            if (execution != null) {
                onUpdateEvidence(
                    execution.id,
                    execution.siteId,
                    execution.positionId,
                    execution.ciltId,
                    execution.id,
                    imageUri.toString()
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = stringResource(R.string.parameter_ok_question),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = isParameterOk,
            onCheckedChange = { isParameterOk = it },
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    if (!isParameterOk) {
        Button(
            onClick = {},
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        ) {
            Text(stringResource(R.string.generate_am_card))
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    if (executionId != null) {
        Button(
            onClick = {
                onStopExecution(
                    executionId,
                    parameterFound,
                    false,
                    finalParameter,
                    false,
                    !isParameterOk,
                    0,
                )
            },
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        ) {
            Text(stringResource(R.string.finish_sequence))
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
            text = "$label ",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
