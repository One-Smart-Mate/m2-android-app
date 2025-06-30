package com.ih.osm.ui.pages.cilt

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
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
import com.ih.osm.ui.components.buttons.ButtonType
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.components.launchers.CameraLauncher
import com.ih.osm.ui.components.opl.OplItemCard
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.extensions.fromIsoToNormalDate
import com.ih.osm.ui.navigation.navigateToCreateCard
import com.ih.osm.ui.theme.PaddingToolbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun CiltDetailScreen(
    sequenceId: Int,
    navController: NavController,
    viewModel: CiltRoutineViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val isLoading = state.isLoading

    val sequence = viewModel.getSequenceById(sequenceId)

    val opl = state.opl
    val remediationOpl = state.remediationOpl

    LaunchedEffect(state.isSequenceFinished) {
        if (state.isSequenceFinished) {
            navController.popBackStack()
        }
    }

    if (isLoading) {
        LoadingScreen()
        return
    }

    if (sequence != null) {
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
                        navController = navController,
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
                        onCreateEvidence = { executionId, imageUri ->
                            viewModel.createEvidence(
                                executionId = executionId,
                                imageUri = imageUri,
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
        LaunchedEffect(viewModel) {
            snapshotFlow { state }
                .flowWithLifecycle(lifecycle)
                .distinctUntilChanged()
                .collect {
                    if (state.message.isNotEmpty() && !state.isLoading) {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(message = state.message)
                        }
                    }
                }
        }
    }

    SnackbarHost(hostState = snackBarHostState) {
        Snackbar(
            snackbarData = it,
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = Color.White,
            modifier = Modifier.padding(top = PaddingToolbar),
        )
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
    }
}

@Composable
fun SequenceDetailContent(
    sequence: Sequence,
    navController: NavController,
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
        executionId: Int,
        imageUri: Uri,
    ) -> Unit,
    opl: Opl?,
    getOplById: (String) -> Unit,
    remediationOpl: Opl?,
    getRemediationOplById: (String) -> Unit,
) {
    var parameterFound by remember { mutableStateOf("") }
    var finalParameter by remember { mutableStateOf("") }
    var isParameterOk by remember { mutableStateOf(true) }
    var isEvidenceAtCreation by remember { mutableStateOf(false) }
    var isEvidenceAtFinal by remember { mutableStateOf(false) }
    var isStarted by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0) }
    val totalDuration = sequence.executions.firstOrNull()?.duration ?: 0
    val progress = if (totalDuration > 0) elapsedTime / totalDuration.toFloat() else 0f

    LaunchedEffect(isStarted) {
        while (isStarted) {
            delay(1000L)
            elapsedTime += 1
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = stringResource(R.string.execution_date_time_label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text =
                        sequence.executions.firstOrNull()?.secuenceSchedule.fromIsoToNormalDate(),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = stringResource(R.string.cilt_type_name_label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text =
                        sequence.executions.firstOrNull()?.ciltTypeName ?: stringResource(
                            R.string.not_available,
                        ),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = stringResource(R.string.execution_color_label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Box(
                    modifier =
                        Modifier
                            .size(dimensionResource(id = R.dimen.circle_shape_size))
                            .background(
                                color =
                                    getColorFromHex(
                                        sequence.executions.firstOrNull()?.secuenceColor
                                            ?: stringResource(R.string.not_available),
                                    ),
                                shape = CircleShape,
                            ),
                )
            }
        }
    }

    Text(
        text = stringResource(R.string.route_label),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
    )
    Text(
        text =
            sequence.executions.firstOrNull()?.route ?: stringResource(
                R.string.not_available,
            ),
        style = MaterialTheme.typography.bodyLarge,
    )

    Spacer(modifier = Modifier.height(8.dp))

    val specialWarning = sequence.executions.firstOrNull()?.specialWarning

    if (specialWarning != null) {
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.Yellow, shape = RoundedCornerShape(8.dp))
                    .padding(12.dp),
        ) {
            Text(
                text = stringResource(R.string.special_warning, specialWarning),
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

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

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
    ) {
        Text(
            text = String.format("%02d:%02d", elapsedTime / 60, elapsedTime % 60),
            style =
                MaterialTheme.typography.titleLarge.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                ),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }

    Spacer(modifier = Modifier.height(4.dp))

    LinearProgressIndicator(
        progress = progress.coerceIn(0f, 1f),
        modifier =
            Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp)),
        color = MaterialTheme.colorScheme.primary,
    )

    Spacer(modifier = Modifier.height(20.dp))

    val executionId = sequence.executions.firstOrNull()?.id

    if (executionId != null && !isStarted) {
        CustomButton(
            text = stringResource(R.string.start_sequence),
            buttonType = ButtonType.DEFAULT,
            onClick = {
                onStartExecution(executionId)
                isStarted = true
            },
        )
    }

    if (executionId != null && isStarted) {
        CustomButton(
            text = stringResource(R.string.finish_sequence),
            buttonType = ButtonType.DEFAULT,
            onClick = {
                // if (isStarted && !isFinished) {
                if (isStarted) {
                    onStopExecution(
                        executionId,
                        parameterFound,
                        isEvidenceAtCreation,
                        finalParameter,
                        isEvidenceAtFinal,
                        !isParameterOk,
                        0,
                    )
                    isFinished = true
                    // isStarted = false
                }
            },
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    InfoItem(
        label = stringResource(R.string.duration_label),
        value = sequence.executions.firstOrNull()?.duration.toString(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    InfoItem(
        label = stringResource(R.string.reference_label),
        value =
            sequence.executions.firstOrNull()?.referencePoint
                ?: stringResource(R.string.not_available),
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = stringResource(R.string.steps_to_follow),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
    )
    sequence.executions.firstOrNull()?.secuenceList?.split("\n")?.forEach { step ->
        Text("• ${step.trim()}", style = MaterialTheme.typography.bodyLarge)
    }

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

    CustomTextField(
        modifier =
            Modifier.fillMaxWidth(),
        // .alpha(if (isStarted) 1f else 0.5f),
        label = stringResource(R.string.parameter_found),
        placeholder = stringResource(R.string.enter_parameter_found),
        icon = Icons.Outlined.Menu,
        // onChange = { if (isStarted) parameterFound = it },
        onChange = { parameterFound = it },
    )

    Spacer(modifier = Modifier.height(8.dp))

    Box(
        modifier =
            Modifier
                .fillMaxWidth(),
        // .alpha(if (isStarted) 1f else 0.5f),
        contentAlignment = Alignment.Center,
    ) {
        CameraLauncher { imageUri ->
            val execution = sequence.executions.firstOrNull()
            if (execution != null) {
                onCreateEvidence(execution.id, imageUri)
                isEvidenceAtCreation = true
            }
        }
    }

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
        modifier =
            Modifier.fillMaxWidth(),
        // .alpha(if (isStarted) 1f else 0.5f),
        label = stringResource(R.string.final_parameter),
        placeholder = stringResource(R.string.enter_final_parameter),
        icon = Icons.Outlined.Menu,
        // onChange = { if (isStarted) finalParameter = it },
        onChange = { finalParameter = it },
    )

    Spacer(modifier = Modifier.height(8.dp))

    Box(
        modifier =
            Modifier
                .fillMaxWidth(),
        // .alpha(if (isStarted) 1f else 0.5f),
        contentAlignment = Alignment.Center,
    ) {
        CameraLauncher { imageUri ->
            val execution = sequence.executions.firstOrNull()
            if (execution != null) {
                onCreateEvidence(execution.id, imageUri)
                isEvidenceAtFinal = true
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
            // enabled = isStarted,
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    if (!isParameterOk) {
        Button(
            onClick = { navController.navigateToCreateCard() },
            // enabled = isStarted,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
        ) {
            Text(stringResource(R.string.generate_am_card))
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
