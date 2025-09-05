package com.ih.osm.ui.pages.cilt

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import com.ih.osm.ui.navigation.Screen
import com.ih.osm.ui.navigation.navigateToCreateCard
import com.ih.osm.ui.navigation.navigateToSequence
import com.ih.osm.ui.pages.cilt.action.CiltAction
import com.ih.osm.ui.theme.PaddingToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun CiltDetailScreen(
    executionId: Int,
    targetSiteExecutionId: Int = -1,
    navController: NavController,
    viewModel: CiltRoutineViewModel = hiltViewModel(),
) {
    Log.e("CiltDetailScreen", "🚀🚀🚀 CILT DETAIL SCREEN STARTED 🚀🚀🚀")
    Log.e("CiltDetailScreen", "executionId: $executionId")
    Log.e("CiltDetailScreen", "targetSiteExecutionId: $targetSiteExecutionId")
    Log.e("CiltDetailScreen", "Current route: ${navController.currentDestination?.route}")
    Log.e("CiltDetailScreen", "============================================")
    Log.d("CiltDetailScreen", "=== SCREEN CREATED ===")
    Log.d("CiltDetailScreen", "executionId: $executionId")
    Log.d("CiltDetailScreen", "targetSiteExecutionId: $targetSiteExecutionId")
    Log.d("CiltDetailScreen", "Current route: ${navController.currentDestination?.route}")
    Log.d("CiltDetailScreen", "========================")
    val state by viewModel.state.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val focusManager = LocalFocusManager.current

    val isLoading = state.isLoading

    val execution =
        if (executionId == 0 && targetSiteExecutionId != -1) {
            // When coming from Procedimientos, find execution by siteExecutionId
            Log.d("CiltDetailScreen", "🔍 SEARCHING for execution with siteExecutionId: $targetSiteExecutionId")
            val foundExecution = viewModel.getExecutionBySiteExecutionId(targetSiteExecutionId)
            if (foundExecution != null) {
                Log.d("CiltDetailScreen", "✅ FOUND execution: siteExecutionId=${foundExecution.siteExecutionId}, id=${foundExecution.id}")
            } else {
                Log.e("CiltDetailScreen", "❌ EXECUTION NOT FOUND for siteExecutionId: $targetSiteExecutionId")
                Log.d("CiltDetailScreen", "Available executions:")
                state.ciltData?.positions?.forEach { position ->
                    Log.d("CiltDetailScreen", "Position: ${position.name}")
                    position.ciltMasters.forEach { master ->
                        Log.d("CiltDetailScreen", "  CiltMaster: ${master.ciltName}")
                        master.sequences.forEach { sequence ->
                            Log.d("CiltDetailScreen", "    Sequence ${sequence.id}: ${sequence.executions.size} executions")
                            sequence.executions.forEach { exec ->
                                Log.d("CiltDetailScreen", "      - Execution id=${exec.id}, siteExecutionId=${exec.siteExecutionId}")
                            }
                        }
                    }
                }
            }
            foundExecution
        } else {
            Log.d("CiltDetailScreen", "🔍 SEARCHING for execution with id: $executionId")
            val foundExecution = viewModel.getExecutionById(executionId)
            Log.d("CiltDetailScreen", "Found execution: ${foundExecution?.id}")
            foundExecution
        }

    val opl = state.opl
    val remediationOpl = state.remediationOpl
    val superiorId = state.superiorId

    LaunchedEffect(Unit) {
        Log.d("CiltDetailScreen", "=== INITIAL LOAD ===")
        Log.d("CiltDetailScreen", "targetSiteExecutionId: $targetSiteExecutionId")
        if (targetSiteExecutionId != -1) {
            Log.d("CiltDetailScreen", "🔄 Coming from Procedimientos - forcing data refresh")
        } else {
            Log.d("CiltDetailScreen", "📋 Normal navigation - regular data load")
        }
        viewModel.process(CiltAction.GetCilts)
        Log.d("CiltDetailScreen", "=== INITIAL LOAD END ===")
    }

    LaunchedEffect(state.ciltData) {
        Log.d("CiltDetailScreen", "CiltData changed: ${state.ciltData != null}")
        if (state.ciltData != null) {
            Log.d("CiltDetailScreen", "Loading superior ID for executionId: $executionId")
            viewModel.getSuperiorIdFromExecutionLevelId(executionId)
        }
    }

    // Auto-navigate to sequence when coming from Procedimientos
    var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSequenceFinished) {
        Log.d("CiltDetailScreen", "=== SEQUENCE FINISHED HANDLER ===")
        Log.d("CiltDetailScreen", "🔔 isSequenceFinished: ${state.isSequenceFinished}")
        Log.d("CiltDetailScreen", "🎯 targetSiteExecutionId: $targetSiteExecutionId")
        Log.d("CiltDetailScreen", "🆔 executionId: $executionId")
        Log.d("CiltDetailScreen", "🧭 Current route: ${navController.currentDestination?.route}")
        if (state.isSequenceFinished) {
            Log.d("CiltDetailScreen", "✅ SEQUENCE EXECUTION COMPLETED - DETERMINING NAVIGATION")
            Log.d("CiltDetailScreen", "🤔 Coming from Procedimientos? ${targetSiteExecutionId != -1}")
            Log.d("CiltDetailScreen", "📍 Current destination: ${navController.currentDestination?.route}")
            Log.d("CiltDetailScreen", "🔄 REDIRECTION LOGIC: Will redirect to main rutinas or pop back stack")

            // Reset hasNavigated flag when sequence is finished
            hasNavigated = false
            Log.d("CiltDetailScreen", "🧹 RESET hasNavigated to false")

            // When coming from Procedimientos (targetSiteExecutionId != -1), go back to rutinas
            if (targetSiteExecutionId != -1) {
                Log.d("CiltDetailScreen", "🏠 REDIRECTION TO MAIN RUTINAS (came from Procedimientos)")
                Log.d("CiltDetailScreen", "📍 Before navigation - current route: ${navController.currentDestination?.route}")
                Log.d("CiltDetailScreen", "🎯 Target was siteExecutionId: $targetSiteExecutionId - now completed")
                // Force complete destruction by popping back to CiltScreen
                val success = navController.popBackStack(Screen.Cilt.route, false)
                Log.d("CiltDetailScreen", "PopBackStack result: $success")
                if (!success) {
                    // Fallback: navigate fresh
                    Log.d("CiltDetailScreen", "Fallback navigation")
                    navController.navigate(Screen.Cilt.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
                Log.d("CiltDetailScreen", "📍 After navigation - current route: ${navController.currentDestination?.route}")
            } else {
                Log.d("CiltDetailScreen", "⬅️ NORMAL NAVIGATION - POP BACK STACK (regular navigation)")
                Log.d("CiltDetailScreen", "📤 Popping back to previous screen")
                navController.popBackStack()
            }

            // Reset the sequence finished flag immediately after navigation
            viewModel.resetSequenceFinishedFlag()
            Log.d("CiltDetailScreen", "🧹 RESET isSequenceFinished flag to prevent re-triggering")
            Log.d("CiltDetailScreen", "✅ REDIRECTION COMPLETED - User should see main rutinas or previous screen")
            Log.d("CiltDetailScreen", "=== NAVIGATION COMPLETED ===")
        }
    }

    LaunchedEffect(targetSiteExecutionId, hasNavigated) {
        Log.d("CiltDetailScreen", "=== AUTO-NAVIGATE HANDLER STARTED ===")
        Log.d("CiltDetailScreen", "🔑 Keys - targetSiteExecutionId: $targetSiteExecutionId, hasNavigated: $hasNavigated")
        Log.d("CiltDetailScreen", "📊 Current state - execution: ${execution?.siteExecutionId}, ciltData: ${state.ciltData != null}")
        Log.d("CiltDetailScreen", "⚠️ This LaunchedEffect should NOT restart if execution changes")

        if (targetSiteExecutionId != -1 && execution != null && state.ciltData != null && !hasNavigated) {
            Log.d("CiltDetailScreen", "✅ AUTO-NAVIGATE CONDITIONS MET")
            Log.d("CiltDetailScreen", "Found execution: ${execution.siteExecutionId} (ID: ${execution.id})")
            Log.d("CiltDetailScreen", "Starting sequence search...")
            // Find the sequence that contains this execution
            val sequence =
                state.ciltData?.positions
                    ?.flatMap { it.ciltMasters }
                    ?.flatMap { it.sequences }
                    ?.find { sequence ->
                        val hasExecution = sequence.executions.any { it.siteExecutionId == targetSiteExecutionId }
                        Log.d("CiltDetailScreen", "Checking sequence ${sequence.id}: has execution = $hasExecution")
                        hasExecution
                    }

            if (sequence != null) {
                Log.d("CiltDetailScreen", "🎯 FOUND SEQUENCE: ${sequence.id} for execution: ${execution.id}")
                Log.d("CiltDetailScreen", "⏱️ Waiting 500ms before navigation...")
                // Navigate to the sequence after a small delay to ensure smooth transition
                Log.d("CiltDetailScreen", "⏳ Starting 500ms delay - LaunchedEffect should NOT restart during this time")
                delay(500)
                Log.d("CiltDetailScreen", "✅ Delay completed - proceeding with navigation")
                Log.d("CiltDetailScreen", "🚀 EXECUTING NAVIGATION TO SEQUENCE")
                Log.d("CiltDetailScreen", "🎯 Sequence ID: ${sequence.id}, Execution ID: ${execution.id}")
                Log.d("CiltDetailScreen", "🆔 siteExecutionId: ${execution.siteExecutionId}")
                hasNavigated = true
                navController.navigateToSequence(sequence.id, execution.id)
                Log.d("CiltDetailScreen", "✅ Navigation command executed - User should now see sequence screen")
                Log.d("CiltDetailScreen", "🔄 When sequence completes, it will trigger redirection back here")
            } else {
                Log.e("CiltDetailScreen", "❌ SEQUENCE NOT FOUND for siteExecutionId: $targetSiteExecutionId")
                Log.d("CiltDetailScreen", "Available sequences:")
                state.ciltData?.positions?.forEach { position ->
                    position.ciltMasters.forEach { master ->
                        master.sequences.forEach { seq ->
                            Log.d("CiltDetailScreen", "  - Sequence ${seq.id}: ${seq.executions.map { it.siteExecutionId }}")
                        }
                    }
                }
            }
        } else {
            Log.d("CiltDetailScreen", "❌ AUTO-NAVIGATE CONDITIONS NOT MET:")
            Log.d("CiltDetailScreen", "  - targetSiteExecutionId != -1: ${targetSiteExecutionId != -1}")
            Log.d("CiltDetailScreen", "  - execution != null: ${execution != null}")
            Log.d("CiltDetailScreen", "  - ciltData != null: ${state.ciltData != null}")
            Log.d("CiltDetailScreen", "  - !hasNavigated: ${!hasNavigated}")
        }
        Log.d("CiltDetailScreen", "=== AUTO-NAVIGATE HANDLER END ===")
    }

    if (state.isUploadingEvidence || isLoading) {
        Log.d("CiltDetailScreen", "Showing loading screen - uploadingEvidence: ${state.isUploadingEvidence}, isLoading: $isLoading")
        LoadingScreen()
        return
    }

    if (execution != null) {
        Log.e("CiltDetailScreen", "🖥️🖥️🖥️ RENDERING EXECUTION SCREEN for siteExecutionId: ${execution.siteExecutionId}")
        Log.e("CiltDetailScreen", "📊📊📊 EXECUTION STATUS: '${execution.status}' - ID: ${execution.id}")
        Log.d("CiltDetailScreen", "Rendering execution screen for siteExecutionId: ${execution.siteExecutionId}")
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
    } else {
        Log.e("CiltDetailScreen", "❌ EXECUTION IS NULL - CANNOT RENDER")
        Log.e("CiltDetailScreen", "🆔 executionId: $executionId, targetSiteExecutionId: $targetSiteExecutionId")
        Log.e("CiltDetailScreen", "📊 CiltData is null: ${state.ciltData == null}")
        Log.e("CiltDetailScreen", "🛑 This indicates a problem with data loading or navigation parameters")
        // Show empty screen or error message
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text("No execution found")
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
            text = execution.siteExecutionId.toString(),
            style =
                MaterialTheme.typography.titleLarge
                    .copy(fontWeight = FontWeight.Bold),
        )
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
    Log.d("CiltDetailScreen", "🎯 ENTERING ExecutionDetailContent for execution ${execution.siteExecutionId}")
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

    // Check if execution is completed (status = "R" means completed)
    val isCompleted = execution.status == "R"
    Log.d("CiltDetailScreen", "=== EXECUTION STATUS CHECK ===")
    Log.d("CiltDetailScreen", "Execution ${execution.siteExecutionId} - status: '${execution.status}'")
    Log.d("CiltDetailScreen", "secuenceStart: ${execution.secuenceStart}")
    Log.d("CiltDetailScreen", "secuenceStop: ${execution.secuenceStop}")
    Log.d("CiltDetailScreen", "isCompleted: $isCompleted (based on status == 'R')")
    Log.d("CiltDetailScreen", "===============================")

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

    // Only show start/finish buttons if execution is not completed (status != "R")
    if (!isCompleted) {
        Log.d("CiltDetailScreen", "🔘 SHOWING ACTION BUTTONS - execution not completed")
        if (!isStarted) {
            Log.d("CiltDetailScreen", "▶️ Showing START SEQUENCE button")
            CustomButton(
                text = stringResource(R.string.start_sequence),
                buttonType = ButtonType.DEFAULT,
                onClick = {
                    Log.d("CiltDetailScreen", "🚀 START SEQUENCE clicked - checking execution window")
                    if (canExecute) {
                        Log.d("CiltDetailScreen", "✅ Execution window valid - starting sequence")
                        onAction(CiltAction.StartExecution(execution.id))
                    } else {
                        Log.d("CiltDetailScreen", "❌ Execution window invalid - showing snackbar")
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
            Log.d("CiltDetailScreen", "⏹️ Showing FINISH SEQUENCE button")
            CustomButton(
                text = stringResource(R.string.finish_sequence),
                buttonType = ButtonType.DEFAULT,
                onClick = {
                    Log.d("CiltDetailScreen", "🛑 FINISH SEQUENCE clicked")
                    onAction(CiltAction.StopExecution(execution.id))
                },
            )
        }
    } else {
        Log.d("CiltDetailScreen", "🚫 HIDING ACTION BUTTONS - execution completed (status = 'R')")
        // Show completion status
        Log.d("CiltDetailScreen", "📊 SHOWING COMPLETION STATUS - execution already finished")
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp),
                    )
                    .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "Completado",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Secuencia Completada (Status: R)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
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

    if (!isCompleted) {
        Log.d("CiltDetailScreen", "✏️ SHOWING EDITABLE parameter_found field")
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.parameter_found),
            placeholder = stringResource(R.string.enter_parameter_found),
            icon = Icons.Outlined.Menu,
            onChange = { onAction(CiltAction.SetParameterFound(it)) },
        )
    } else {
        Log.d("CiltDetailScreen", "🔒 SHOWING READ-ONLY parameter_found field - value: ${execution.initialParameter}")
        // Show read-only field for completed executions
        InfoItem(
            label = stringResource(R.string.parameter_found),
            value = execution.initialParameter ?: "N/A",
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Only show camera for evidence if execution is not completed
    if (!isCompleted) {
        Log.d("CiltDetailScreen", "📷 SHOWING INITIAL EVIDENCE camera")
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
    } else {
        Log.d("CiltDetailScreen", "🚫 HIDING INITIAL EVIDENCE camera - execution completed")
    }

    SectionImagesEvidence(
        imageEvidences =
            if (isCompleted) {
                // For completed executions, we would need to get the actual evidence from the execution
                // For now, showing empty list, but this should be populated with actual evidence data
                emptyList()
            } else {
                evidenceUrisBefore.map { uri ->
                    Evidence.fromCreateEvidence("", uri.toString(), EvidenceType.INITIAL.name)
                }
            },
        onDeleteEvidence = { evidence ->
            if (!isCompleted) {
                onAction(CiltAction.RemoveEvidenceBefore(evidence.url))
            }
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

    if (!isCompleted) {
        Log.d("CiltDetailScreen", "✏️ SHOWING EDITABLE final_parameter field")
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.final_parameter),
            placeholder = stringResource(R.string.enter_final_parameter),
            icon = Icons.Outlined.Menu,
            onChange = { onAction(CiltAction.SetFinalParameter(it)) },
        )
    } else {
        Log.d("CiltDetailScreen", "🔒 SHOWING READ-ONLY final_parameter field - value: ${execution.finalParameter}")
        // Show read-only field for completed executions
        InfoItem(
            label = stringResource(R.string.final_parameter),
            value = execution.finalParameter ?: "N/A",
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Only show camera for final evidence if execution is not completed
    if (!isCompleted) {
        Log.d("CiltDetailScreen", "📷 SHOWING FINAL EVIDENCE camera")
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
    } else {
        Log.d("CiltDetailScreen", "🚫 HIDING FINAL EVIDENCE camera - execution completed")
    }

    SectionImagesEvidence(
        imageEvidences =
            if (isCompleted) {
                // For completed executions, we would need to get the actual evidence from the execution
                // For now, showing empty list, but this should be populated with actual evidence data
                emptyList()
            } else {
                evidenceUrisAfter.map { uri ->
                    Evidence.fromCreateEvidence("", uri.toString(), EvidenceType.FINAL.name)
                }
            },
        onDeleteEvidence = { evidence ->
            if (!isCompleted) {
                onAction(CiltAction.RemoveEvidenceAfter(evidence.url))
            }
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
