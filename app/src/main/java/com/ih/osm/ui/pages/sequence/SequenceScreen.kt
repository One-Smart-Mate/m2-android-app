package com.ih.osm.ui.pages.sequence

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.ih.osm.R
import com.ih.osm.core.ui.functions.getColorFromHex
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.EvidenceType
import com.ih.osm.domain.model.Execution
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.model.stopMachine
import com.ih.osm.domain.model.stoppageReason
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.CustomTextField
import com.ih.osm.ui.components.ExpandableCard
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.components.card.CardItemListV2
import com.ih.osm.ui.components.cilt.AnatomyHorizontalSection
import com.ih.osm.ui.components.evidence.SectionImagesEvidence
import com.ih.osm.ui.components.launchers.CameraLauncher
import com.ih.osm.ui.components.opl.OplItemCard
import com.ih.osm.ui.extensions.defaultIfNull
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.extensions.orDefault
import com.ih.osm.ui.extensions.parseUTCToLocal
import com.ih.osm.ui.extensions.toTimeString
import com.ih.osm.ui.navigation.navigateToCreateCard
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.PaddingTinySmall
import com.ih.osm.ui.theme.PaddingToolbar
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun SequenceScreen(
    navController: NavController,
    executionId: Int,
    sequenceId: Int,
    viewModel: SequenceViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var showDialog by remember { mutableStateOf(false) }

    if (state.isLoading) {
        LoadingScreen()
    } else {
        SequenceContent(
            navController = navController,
            execution = state.execution,
            evidences = state.evidences,
            showBottomSheet = state.showBottomSheet,
            opl = state.opl,
            showBottomSheetRemediation = state.showBottomSheetRemediation,
            oplRemediation = state.oplRemediation,
            bannerMessage = state.bannerMessage,
            enableStartButton = state.enableStartButton,
            enableCompleteButton = state.enableCompleteButton,
            enableStartExecution = state.enableStartExecution,
            maxTimeDuration = state.duration,
            elapsedTime = state.elapsedTime,
            isParamOk = state.isParamOk,
            superiorId = state.superiorId,
            card = state.card,
            onShowDialog = {
                if (state.enableStartExecution) {
                    showDialog = true
                } else {
                    navController.popBackStack()
                }
            },
            onAction = { action ->
                viewModel.process(action)
            },
        )
    }
    LaunchedEffect(Unit) {
        viewModel.load(sequenceId = sequenceId, executionId = executionId)
    }

    LaunchedEffect(viewModel) {
        snapshotFlow { state }
            .flowWithLifecycle(lifecycle)
            .distinctUntilChanged()
            .collect {
                if (state.message.isNotEmpty() && state.isLoading.not()) {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(message = state.message)
                        viewModel.process(SequenceViewModel.SequenceAction.CleanMessage)
                    }
                }
                if (state.navigateBack) {
                    navController.popBackStack()
                }
            }
    }
    SnackbarHost(hostState = snackBarHostState) {
        Snackbar(
            snackbarData = it,
            containerColor = Color.Red,
            contentColor = Color.White,
            modifier = Modifier.padding(top = PaddingToolbar),
        )
    }

    BackHandler {
        if (state.enableStartExecution) {
            showDialog = true
        } else {
            navController.popBackStack()
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.leave_screen)) },
            text = { Text(stringResource(R.string.leave_screen_description)) },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    viewModel.process(SequenceViewModel.SequenceAction.CleanSequenceInformation)
                }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SequenceContent(
    navController: NavController,
    execution: Execution?,
    evidences: List<Evidence>,
    showBottomSheet: Boolean,
    opl: Opl?,
    showBottomSheetRemediation: Boolean,
    oplRemediation: Opl?,
    bannerMessage: String,
    enableStartButton: Boolean,
    enableCompleteButton: Boolean,
    enableStartExecution: Boolean,
    maxTimeDuration: Int = 0,
    elapsedTime: Int = 0,
    isParamOk: Boolean,
    superiorId: Int,
    card: Card? = null,
    onAction: (SequenceViewModel.SequenceAction) -> Unit = {},
    onShowDialog: () -> Unit = {},
) {
    // Check if execution is completed (status = "R" means completed)
    val isCompleted = execution?.status == "R"
    val scrollState = rememberScrollState()

    Scaffold { paddingValues ->
        Column(
            modifier =
                Modifier
                    .defaultScreen(paddingValues)
                    .verticalScroll(state = scrollState),
        ) {
            CustomAppBar(
                navController = navController,
                title = "${execution?.siteExecutionId} ${execution?.ciltTypeName.orEmpty()}",
            ) {
                onShowDialog()
            }

            if (bannerMessage.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = Color.Red,
                        ),
                ) {
                    Text(
                        text = bannerMessage,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(PaddingNormal),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            CustomSpacer()
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                AnatomySection(
                    title = stringResource(R.string.execution_date_time_label),
                    description = execution?.secuenceSchedule?.parseUTCToLocal().orDefault(),
                )

                AnatomySection(
                    title = stringResource(R.string.cilt_type_name_label),
                    description = execution?.ciltTypeName.orDefault(),
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(PaddingTinySmall),
                ) {
                    Text(
                        text = stringResource(R.string.execution_color_label),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Box(
                        modifier =
                            Modifier
                                .size(dimensionResource(id = R.dimen.circle_shape_size))
                                .background(
                                    color = getColorFromHex(execution?.secuenceColor.orEmpty()),
                                    shape = CircleShape,
                                ),
                    )
                }
            }
            CustomSpacer()
            AnatomySection(
                title = stringResource(R.string.route_label),
                description = execution?.route.orDefault(),
            )
            CustomSpacer()

            if (execution?.specialWarning.isNullOrEmpty().not()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = Color.Yellow,
                        ),
                ) {
                    Text(
                        text = stringResource(R.string.special_warning, execution.specialWarning),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(PaddingNormal),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
                CustomSpacer()
            }

            AnatomyHorizontalSection(
                title = stringResource(R.string.stoppage_required),
                description =
                    if (execution?.stopMachine().defaultIfNull(false)) {
                        stringResource(R.string.stop_reason_yes)
                    } else {
                        stringResource(R.string.stop_reason_no)
                    },
            )

            // Counter

            if (enableCompleteButton) {
                SequenceTimer(totalDuration = maxTimeDuration, elapsedTime = elapsedTime)
            }

            // Only show start button if execution is not completed (status != "R")
            if (enableStartButton && !isCompleted) {
                CustomButton(text = stringResource(R.string.start_sequence)) {
                    onAction(SequenceViewModel.SequenceAction.StartSequence)
                }
            } else if (isCompleted) {
                // Show completion status
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        ),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.sequence_finished),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            CustomSpacer()

            // Only show finish button if execution is not completed
            if (enableCompleteButton && !isCompleted) {
                CustomButton(text = stringResource(R.string.finish_sequence)) {
                    onAction(SequenceViewModel.SequenceAction.CompleteSequence)
                }
            }

            AnatomyHorizontalSection(
                title = stringResource(R.string.duration_label),
                description = execution?.duration?.toTimeString().orDefault(),
            )

            AnatomyHorizontalSection(
                title = stringResource(R.string.reference_label),
                description = execution?.referencePoint.orDefault(),
            )

            ExpandableCard(
                title = stringResource(R.string.steps_to_follow),
            ) {
                Text(
                    text = execution?.secuenceList.orDefault(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            ExpandableCard(
                title = stringResource(R.string.tools_list),
            ) {
                Text(
                    text = execution?.toolsRequiered.orDefault(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            AnatomyHorizontalSection(
                title = stringResource(R.string.parameter_ok),
                description = execution?.standardOk.orDefault(),
            )
            CustomSpacer()

            // Only show editable fields if execution is not completed
            if (enableStartExecution && !isCompleted) {
                CustomTextField(
                    label = stringResource(R.string.parameter_found),
                    icon = Icons.Outlined.Create,
                    modifier = Modifier.fillMaxWidth(),
                ) { value ->
                    onAction(SequenceViewModel.SequenceAction.UpdateInitialParameter(value))
                }
                CustomSpacer()
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CameraLauncher { imageUri ->
                        onAction(
                            SequenceViewModel.SequenceAction.AddEvidence(
                                imageUri,
                                EvidenceType.INITIAL,
                            ),
                        )
                    }
                }

                SectionImagesEvidence(imageEvidences = evidences.filter { it.type == EvidenceType.INITIAL.name }) {
                    onAction(SequenceViewModel.SequenceAction.RemoveEvidence(it))
                }
            } else if (isCompleted && enableStartExecution) {
                AnatomyHorizontalSection(
                    title = stringResource(R.string.parameter_found),
                    description = execution.initialParameter ?: "N/A",
                )
                CustomSpacer()
            }

            CustomSpacer()
            CustomButton(text = stringResource(R.string.view_opl_sop)) {
                onAction(SequenceViewModel.SequenceAction.GetOpl)
            }
            CustomSpacer(space = SpacerSize.LARGE)

            // Only show final parameter field if execution is not completed
            if (enableStartExecution && !isCompleted) {
                CustomTextField(
                    label = stringResource(R.string.final_parameter),
                    icon = Icons.Outlined.Create,
                    modifier = Modifier.fillMaxWidth(),
                ) { value ->
                    onAction(SequenceViewModel.SequenceAction.UpdateFinalParameter(value))
                }
                CustomSpacer()

                // camera
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CameraLauncher { imageUri ->
                        onAction(
                            SequenceViewModel.SequenceAction.AddEvidence(
                                imageUri,
                                EvidenceType.FINAL,
                            ),
                        )
                    }
                }

                SectionImagesEvidence(imageEvidences = evidences.filter { it.type == EvidenceType.FINAL.name }) {
                    onAction(SequenceViewModel.SequenceAction.RemoveEvidence(it))
                }
                CustomSpacer()
            } else if (isCompleted && enableStartExecution) {
                AnatomyHorizontalSection(
                    title = stringResource(R.string.final_parameter),
                    description = execution.finalParameter ?: "N/A",
                )
                CustomSpacer()
            }
            CustomButton(text = stringResource(R.string.view_remediation)) {
                onAction(SequenceViewModel.SequenceAction.GetRemediationOpl)
            }
            CustomSpacer()

            AnatomyHorizontalSection(
                title = stringResource(R.string.stop_reason_label),
                description =
                    if (execution?.stoppageReason().defaultIfNull(
                            false,
                        )
                    ) {
                        stringResource(R.string.stop_reason_yes)
                    } else {
                        stringResource(R.string.stop_reason_no)
                    },
            )

            // Only show parameter OK radio buttons if execution is not completed
            if (enableStartExecution && card == null && !isCompleted) {
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
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = isParamOk,
                            onClick = {
                                onAction(SequenceViewModel.SequenceAction.ToggleOkParam)
                            },
                        )
                        Text(
                            text = stringResource(R.string.yes_option_parameter_ok),
                            modifier = Modifier.padding(end = 16.dp),
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = isParamOk.not(),
                            onClick = {
                                onAction(SequenceViewModel.SequenceAction.ToggleOkParam)
                            },
                        )
                        Text(text = stringResource(R.string.no_option_parameter_ok))
                    }
                }
            }
            CustomSpacer()

            // Only show generate card button if execution is not completed
            if (isParamOk.not() && card == null && enableStartExecution && !isCompleted) {
                CustomButton(text = stringResource(R.string.generate_am_card)) {
                    navController.navigateToCreateCard("cilt:$superiorId")
                }
            }

            if (card != null) {
                ExpandableCard(
                    title = stringResource(R.string.card),
                ) {
                    CardItemListV2(
                        card = card,
                        onClick = {},
                        onAction = {},
                        isActionsEnabled = false,
                    )
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { onAction(SequenceViewModel.SequenceAction.DismissBottomSheet) },
                ) {
                    opl?.let { OplItemCard(opl = it, onClick = {}) }
                }
            }

            if (showBottomSheetRemediation) {
                ModalBottomSheet(
                    onDismissRequest = { onAction(SequenceViewModel.SequenceAction.DismissBottomSheetRemediation) },
                ) {
                    oplRemediation?.let { OplItemCard(opl = it, onClick = {}) }
                }
            }
        }
    }
}

@Composable
fun SequenceTimer(
    totalDuration: Int,
    elapsedTime: Int,
) {
    val progress = if (totalDuration > 0) elapsedTime / totalDuration.toFloat() else 0f

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
}

@Composable
fun AnatomySection(
    title: String,
    description: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(PaddingTinySmall),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun SequenceScreen_Preview() {
    // SequenceContent(navController = rememberNavController(),)
}
