package com.ih.osm.ui.pages.cilt

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.EvidenceType
import com.ih.osm.domain.model.Execution
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.model.stopMachine
import com.ih.osm.domain.model.stoppageReason
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.CustomTextField
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.buttons.ButtonType
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.components.evidence.SectionImagesEvidence
import com.ih.osm.ui.components.launchers.CameraLauncher
import com.ih.osm.ui.components.opl.OplItemCard
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.extensions.fromIsoToNormalDate
import com.ih.osm.ui.extensions.isWithinExecutionWindow
import com.ih.osm.ui.navigation.navigateToCreateCard
import com.ih.osm.ui.pages.cilt.action.CiltAction
import com.ih.osm.ui.theme.PaddingToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun CiltDetailScreen(
    executionId: Int,
    navController: NavController,
    viewModel: CiltRoutineViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val focusManager = LocalFocusManager.current

    val isLoading = state.isLoading

    val execution = viewModel.getExecutionById(executionId)

    val opl = state.opl
    val remediationOpl = state.remediationOpl
    val superiorId = state.superiorId

    LaunchedEffect(state.ciltData) {
        if (state.ciltData != null) {
            viewModel.getSuperiorIdFromExecutionLevelId(executionId)
        }
    }

    LaunchedEffect(state.isSequenceFinished) {
        if (state.isSequenceFinished) {
            navController.popBackStack()
        }
    }

    if (state.isUploadingEvidence || isLoading) {
        LoadingScreen()
        return
    }

    if (execution != null) {
        Scaffold { padding ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { focusManager.clearFocus() },
                        ),
            ) {
                LazyColumn(
                    modifier =
                        Modifier
                            .defaultScreen(padding)
                            .imePadding(),
                ) {
                    stickyHeader {
                        CustomAppBar(
                            navController = navController,
                            content = { CiltDetailHeader(execution) },
                        )
                    }

                    item {
                        ExecutionDetailContent(
                            execution = execution,
                            navController = navController,
                            viewModel = viewModel,
                            onAction = { viewModel.process(it) },
                            opl = opl,
                            remediationOpl = remediationOpl,
                            superiorId = superiorId,
                            snackbarHostState = snackBarHostState,
                            coroutineScope = coroutineScope,
                        )
                    }
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
                            viewModel.process(CiltAction.CleanMessage)
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
fun CiltDetailHeader(execution: Execution) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = execution.ciltTypeName,
            style =
                MaterialTheme.typography.titleLarge
                    .copy(fontWeight = FontWeight.Bold),
        )
    }
}

@Composable
fun ExecutionDetailContent(
    execution: Execution,
    navController: NavController,
    viewModel: CiltRoutineViewModel,
    onAction: (CiltAction) -> Unit,
    opl: Opl?,
    remediationOpl: Opl?,
    superiorId: String?,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
) {
    val context = LocalContext.current

    val isStarted = viewModel.isStarted.value
    val isFinished = viewModel.isFinished.value
    val parameterFound = viewModel.parameterFound.value
    val finalParameter = viewModel.finalParameter.value
    val isParameterOk = viewModel.isParameterOk.value
    val isEvidenceAtCreation = viewModel.isEvidenceAtCreation.value
    val isEvidenceAtFinal = viewModel.isEvidenceAtFinal.value
    val evidenceUrisBefore = viewModel.evidenceUrisBefore
    val evidenceUrisAfter = viewModel.evidenceUrisAfter

    var elapsedTime by remember { mutableStateOf(0) }
    val totalDuration = execution.duration ?: 0
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
                        execution.secuenceSchedule.fromIsoToNormalDate(),
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
                    text = execution.ciltTypeName,
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
                                    getColorFromHex(execution.secuenceColor),
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
            execution.route ?: stringResource(
                R.string.not_available,
            ),
        style = MaterialTheme.typography.bodyLarge,
    )

    Spacer(modifier = Modifier.height(8.dp))

    val specialWarning = execution.specialWarning

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
                    if (execution.stopMachine()) {
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
                if (execution.stopMachine()) {
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

    val (canExecute, message) =
        execution.secuenceSchedule.isWithinExecutionWindow(
            context = context,
            allowExecuteBefore = execution.allowExecuteBefore,
            allowExecuteBeforeMinutes = execution.allowExecuteBeforeMinutes,
            toleranceBeforeMinutes = execution.toleranceBeforeMinutes,
            toleranceAfterMinutes = execution.toleranceAfterMinutes,
            allowExecuteAfterDue = execution.allowExecuteAfterDue,
        )

    if (!isStarted) {
        CustomButton(
            text = stringResource(R.string.start_sequence),
            buttonType = ButtonType.DEFAULT,
            onClick = {
                if (canExecute) {
                    onAction(CiltAction.StartExecution(execution.id))
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message =
                                message
                                    ?: context.getString(R.string.execution_out_of_window),
                        )
                    }
                }
            },
        )
    } else if (isStarted) {
        CustomButton(
            text = stringResource(R.string.finish_sequence),
            buttonType = ButtonType.DEFAULT,
            onClick = {
                onAction(CiltAction.StopExecution(execution.id))
            },
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    InfoItem(
        label = stringResource(R.string.duration_label),
        value = execution.duration.toString(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    InfoItem(
        label = stringResource(R.string.reference_label),
        value =
            execution.referencePoint
                ?: stringResource(R.string.not_available),
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = stringResource(R.string.steps_to_follow),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
    )
    execution.secuenceList.split("\n").forEach { step ->
        Text("• ${step.trim()}", style = MaterialTheme.typography.bodyLarge)
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = stringResource(R.string.tools_list),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
    )
    execution.toolsRequiered.split("\n").forEach { tool ->
        Text("• ${tool.trim()}", style = MaterialTheme.typography.bodyLarge)
    }

    Spacer(modifier = Modifier.height(8.dp))

    InfoItem(
        label = stringResource(R.string.parameter_ok),
        value = execution.standardOk,
    )

    Spacer(modifier = Modifier.height(8.dp))

    CustomTextField(
        modifier =
            Modifier.fillMaxWidth(),
        label = stringResource(R.string.parameter_found),
        placeholder = stringResource(R.string.enter_parameter_found),
        icon = Icons.Outlined.Menu,
        onChange = { onAction(CiltAction.SetParameterFound(it)) },
    )

    Spacer(modifier = Modifier.height(8.dp))

    Box(
        modifier =
            Modifier
                .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        CameraLauncher { imageUri ->
            onAction(CiltAction.AddEvidenceBefore(execution.id, imageUri))
            onAction(CiltAction.SetEvidenceAtCreation(true))
        }
    }

    SectionImagesEvidence(
        imageEvidences =
            evidenceUrisBefore.map { uri ->
                Evidence.fromCreateEvidence("", uri.toString(), EvidenceType.INITIAL.name)
            },
        onDeleteEvidence = { evidence ->
            onAction(CiltAction.RemoveEvidenceBefore(evidence.url))
        },
    )

    Spacer(modifier = Modifier.height(8.dp))

    val oplId = execution.referenceOplSopId.toString()

    if (opl == null) {
        Button(
            onClick = {
                onAction(CiltAction.GetOplById(oplId))
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
        label = stringResource(R.string.final_parameter),
        placeholder = stringResource(R.string.enter_final_parameter),
        icon = Icons.Outlined.Menu,
        onChange = { onAction(CiltAction.SetFinalParameter(it)) },
    )

    Spacer(modifier = Modifier.height(8.dp))

    Box(
        modifier =
            Modifier
                .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        CameraLauncher { imageUri ->
            onAction(CiltAction.AddEvidenceAfter(execution.id, imageUri))
            onAction(CiltAction.SetEvidenceAtFinal(true))
        }
    }

    SectionImagesEvidence(
        imageEvidences =
            evidenceUrisAfter.map { uri ->
                Evidence.fromCreateEvidence("", uri.toString(), EvidenceType.FINAL.name)
            },
        onDeleteEvidence = { evidence ->
            onAction(CiltAction.RemoveEvidenceAfter(evidence.url))
        },
    )

    Spacer(modifier = Modifier.height(8.dp))

    val oplRemediationId = execution.remediationOplSopId

    if (remediationOpl == null && oplRemediationId != null) {
        Button(
            onClick = {
                onAction(CiltAction.GetRemediationOplById(oplRemediationId))
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

    InfoItem(
        label = stringResource(R.string.stop_reason_label),
        value =
            if (execution.stoppageReason()) {
                stringResource(R.string.stop_reason_yes)
            } else {
                stringResource(
                    R.string.stop_reason_no,
                )
            },
    )

    Spacer(modifier = Modifier.height(8.dp))

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
    ) {
        Text(
            text = stringResource(R.string.parameter_ok_question),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
        )

        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = isParameterOk,
                onClick = { onAction(CiltAction.SetParameterOk(true)) },
            )
            Text(
                text = stringResource(R.string.yes_option_parameter_ok),
                modifier = Modifier.padding(end = 16.dp),
            )
            RadioButton(
                selected = !isParameterOk,
                onClick = { onAction(CiltAction.SetParameterOk(false)) },
            )
            Text(text = stringResource(R.string.no_option_parameter_ok))
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    if (!isParameterOk) {
        Button(
            onClick = {
                superiorId?.let {
                    navController.navigateToCreateCard("cilt:$it")
                }
            },
            enabled = isStarted,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
        ) {
            Text(stringResource(R.string.generate_am_card))
        }

        Spacer(modifier = Modifier.height(8.dp))
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
