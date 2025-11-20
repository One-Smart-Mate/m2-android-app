package com.ih.osm.ui.pages.positionprocedure

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ih.osm.R
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.CiltMaster
import com.ih.osm.domain.model.CiltProcedureData
import com.ih.osm.domain.model.Execution
import com.ih.osm.domain.model.Position
import com.ih.osm.domain.model.Sequence
import com.ih.osm.domain.model.UserInfo
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.cilt.CiltDetailSection
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.extensions.getTextColor
import com.ih.osm.ui.navigation.navigateToCiltDetailWithTarget
import com.ih.osm.ui.pages.positionprocedure.PositionProcedureViewModel
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingNormal

@Composable
fun PositionProcedureScreen(
    navController: NavController,
    viewModel: PositionProcedureViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Clear any stale execution state when screen is first displayed
    LaunchedEffect(Unit) {
        if (state.createdExecutionData != null || state.creatingExecutionForSequence != null) {
            viewModel.clearAllExecutionState()
        }
    }

    // Handle navigation after successful execution creation
    LaunchedEffect(state.createdExecutionData) {
        state.createdExecutionData?.let { (sequenceId, siteExecutionId) ->
            try {
                // Use 0 as dummy executionId since we want to show general routines view,
                // but with automatic transition to the specific siteExecutionId
                navController.navigateToCiltDetailWithTarget(0, siteExecutionId)
            } catch (e: Exception) {
            }
            viewModel.clearNavigationData()
        }
    }

    // Clean up state when leaving the screen
    DisposableEffect(navController) {
        onDispose {
            viewModel.clearAllExecutionState()
        }
    }

    // Debug logs for UI rendering
    android.util.Log.d("PositionProcedureScreen", "=== UI STATE DEBUG ===")
    android.util.Log.d("PositionProcedureScreen", "state.isLoading: ${state.isLoading}")
    android.util.Log.d("PositionProcedureScreen", "state.procedureData is null: ${state.procedureData == null}")
    android.util.Log.d("PositionProcedureScreen", "state.procedureData?.positions?.size: ${state.procedureData?.positions?.size}")
    android.util.Log.d("PositionProcedureScreen", "state.procedureData?.positions?.isEmpty(): ${state.procedureData?.positions?.isEmpty()}")
    android.util.Log.d("PositionProcedureScreen", "state.message: '${state.message}'")

    if (state.isLoading) {
        LoadingScreen(text = stringResource(R.string.loading_procedures))
    } else {
        PositionProcedureContent(
            navController = navController,
            procedureData = state.procedureData,
            creatingExecutionForSequence = state.creatingExecutionForSequence,
            onCreateExecution = { sequence, positionId, levelId ->
                try {
                    viewModel.createExecution(sequence, positionId, levelId)
                } catch (e: Exception) {
                }
            },
            onNavigateToExecution = { executionId ->
                // Navigate to execution detail if needed
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PositionProcedureContent(
    navController: NavController,
    procedureData: CiltProcedureData?,
    creatingExecutionForSequence: Int?,
    onCreateExecution: (CiltProcedureData.Sequence, Int, String) -> Unit,
    onNavigateToExecution: (Int) -> Unit,
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding),
        ) {
            stickyHeader {
                Column(
                    modifier =
                        Modifier
                            .background(
                                color = MaterialTheme.colorScheme.background,
                            ),
                ) {
                    CustomAppBar(
                        navController = navController,
                        title = stringResource(id = R.string.download_third_party_cilt),
                    )
                    CustomSpacer(space = SpacerSize.SMALL)
                }
            }

            item {
                CustomSpacer(space = SpacerSize.EXTRA_LARGE)
            }

            // Critical decision point logging
            android.util.Log.d("PositionProcedureContent", "=== RENDER DECISION POINT ===")
            android.util.Log.d("PositionProcedureContent", "procedureData is null: ${procedureData == null}")
            android.util.Log.d("PositionProcedureContent", "procedureData?.positions is null: ${procedureData?.positions == null}")
            android.util.Log.d("PositionProcedureContent", "procedureData?.positions?.isEmpty(): ${procedureData?.positions?.isEmpty()}")
            android.util.Log.d("PositionProcedureContent", "procedureData?.positions?.size: ${procedureData?.positions?.size}")
            val shouldShowEmptyMessage = procedureData?.positions?.isEmpty() != false
            android.util.Log.d("PositionProcedureContent", "shouldShowEmptyMessage: $shouldShowEmptyMessage")

            if (shouldShowEmptyMessage) {
                android.util.Log.d("PositionProcedureContent", "=== SHOWING EMPTY MESSAGE ===")
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(PaddingNormal),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_procedures_found),
                                style =
                                    MaterialTheme.typography.bodyLarge
                                        .copy(
                                            color = getTextColor().copy(alpha = 0.6f),
                                        ),
                            )
                            CustomSpacer()
                            Text(
                                text = stringResource(id = R.string.no_procedures_found),
                                style =
                                    MaterialTheme.typography.bodyMedium
                                        .copy(
                                            color = getTextColor().copy(alpha = 0.4f),
                                        ),
                            )
                        }
                    }
                }
            } else {
                android.util.Log.d("PositionProcedureContent", "=== SHOWING PROCEDURE DATA ===")
                android.util.Log.d("PositionProcedureContent", "About to render ${procedureData?.positions?.size} positions")
                // Convert CiltProcedureData to CiltData format to reuse CILT components
                val ciltData =
                    CiltData(
                        userInfo =
                            UserInfo(
                                id = 0,
                                name = "",
                                email = "",
                            ),
                        positions =
                            procedureData.positions.map { position ->
                                Position(
                                    id = position.id,
                                    name = position.name,
                                    siteName = position.siteName,
                                    areaName = position.areaName,
                                    ciltMasters =
                                        position.ciltMasters.map { ciltMaster ->
                                            CiltMaster(
                                                id = ciltMaster.id,
                                                siteId = ciltMaster.siteId,
                                                ciltName = ciltMaster.ciltName,
                                                ciltDescription = ciltMaster.ciltDescription,
                                                creatorId = ciltMaster.creatorId,
                                                creatorName = ciltMaster.creatorName,
                                                reviewerId = ciltMaster.reviewerId,
                                                reviewerName = ciltMaster.reviewerName,
                                                approvedById = ciltMaster.approvedById,
                                                approvedByName = ciltMaster.approvedByName,
                                                ciltDueDate = ciltMaster.ciltDueDate,
                                                standardTime = ciltMaster.standardTime,
                                                urlImgLayout = ciltMaster.urlImgLayout,
                                                order = ciltMaster.order,
                                                dateOfLastUsed = ciltMaster.dateOfLastUsed,
                                                createdAt = ciltMaster.createdAt,
                                                updatedAt = ciltMaster.updatedAt,
                                                deletedAt = ciltMaster.deletedAt,
                                                status = ciltMaster.status,
                                                sequences =
                                                    ciltMaster.sequences.map { sequence ->
                                                        Sequence(
                                                            id = sequence.id,
                                                            siteId = sequence.siteId,
                                                            siteName = sequence.siteName,
                                                            ciltMstrId = sequence.ciltMstrId,
                                                            ciltMstrName = sequence.ciltMstrName,
                                                            frecuencyId = sequence.frecuencyId ?: 0,
                                                            frecuencyCode = sequence.frecuencyCode ?: "",
                                                            ciltTypeId = sequence.ciltTypeId,
                                                            ciltTypeName = sequence.ciltTypeName,
                                                            secuenceList = sequence.secuenceList,
                                                            secuenceColor = sequence.secuenceColor,
                                                            toolsRequired = sequence.toolsRequired,
                                                            standardTime = sequence.standardTime,
                                                            standardOk = sequence.standardOk,
                                                            referenceOplSopId = sequence.referenceOplSopId,
                                                            remediationOplSopId = sequence.remediationOplSopId.toString(),
                                                            stoppageReason = sequence.stoppageReason,
                                                            machineStopped = sequence.machineStopped,
                                                            quantityPicturesCreate = sequence.quantityPicturesCreate,
                                                            quantityPicturesClose = sequence.quantityPicturesClose,
                                                            selectableWithoutProgramming = sequence.selectableWithoutProgramming,
                                                            referencePoint = sequence.referencePoint,
                                                            order = sequence.order,
                                                            status = sequence.status,
                                                            createdAt = sequence.createdAt,
                                                            updatedAt = sequence.updatedAt,
                                                            deletedAt = sequence.deletedAt,
                                                            executions =
                                                                sequence.executions.map { execution ->
                                                                    Execution(
                                                                        id = execution.id,
                                                                        siteId = 0,
                                                                        siteExecutionId = execution.siteExecutionId,
                                                                        positionId = execution.positionId,
                                                                        ciltId = execution.ciltId,
                                                                        ciltSequenceId = execution.ciltSecuenceId,
                                                                        levelId = execution.levelId,
                                                                        route = execution.route,
                                                                        userId = execution.userId,
                                                                        userWhoExecutedId = execution.userWhoExecutedId,
                                                                        specialWarning = null,
                                                                        secuenceSchedule = execution.secuenceSchedule,
                                                                        allowExecuteBefore = execution.allowExecuteBefore,
                                                                        allowExecuteBeforeMinutes = execution.allowExecuteBeforeMinutes,
                                                                        toleranceBeforeMinutes = execution.toleranceBeforeMinutes,
                                                                        toleranceAfterMinutes = execution.toleranceAfterMinutes,
                                                                        allowExecuteAfterDue = execution.allowExecuteAfterDue,
                                                                        secuenceStart = execution.secuenceStart,
                                                                        secuenceStop = execution.secuenceStop,
                                                                        duration = execution.duration,
                                                                        realDuration = execution.realDuration?.toIntOrNull(),
                                                                        standardOk = execution.standardOk,
                                                                        initialParameter = execution.initialParameter,
                                                                        evidenceAtCreation = execution.evidenceAtCreation,
                                                                        finalParameter = execution.finalParameter,
                                                                        evidenceAtFinal = execution.evidenceAtFinal,
                                                                        nok = execution.nok,
                                                                        stoppageReason = execution.stoppageReason,
                                                                        machineStopped = execution.machineStopped,
                                                                        amTagId = 0,
                                                                        referencePoint = null,
                                                                        secuenceList = "",
                                                                        secuenceColor = "",
                                                                        ciltTypeId = 0,
                                                                        ciltTypeName = "",
                                                                        referenceOplSopId = 0,
                                                                        remediationOplSopId = "",
                                                                        toolsRequiered = "",
                                                                        selectableWithoutProgramming = false,
                                                                        status = execution.status,
                                                                        createdAt = execution.createdAt,
                                                                        updatedAt = execution.updatedAt,
                                                                        deletedAt = execution.deletedAt,
                                                                        evidences = emptyList(),
                                                                        referenceOplSop = null,
                                                                        remediationOplSop = null,
                                                                    )
                                                                },
                                                        )
                                                    },
                                            )
                                        },
                                )
                            },
                    )

                item {
                    // Reuse CILT visual component but with procedure data
                    CiltDetailSection(
                        data = ciltData,
                        navController = navController,
                        isProcedureMode = true,
                        onCreateExecution = { sequence, positionId, levelId ->
                            // Convert CiltData.Sequence back to CiltProcedureData.Sequence for creation
                            val procedureSequence =
                                procedureData.positions
                                    .flatMap { it.ciltMasters }
                                    .flatMap { it.sequences }
                                    .find { it.id == sequence.id }

                            procedureSequence?.let {
                                onCreateExecution(it, positionId, levelId)
                            }
                        },
                    )
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
private fun PositionProcedureScreenPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            PositionProcedureContent(
                navController = rememberNavController(),
                procedureData = CiltProcedureData.mockData(),
                creatingExecutionForSequence = null,
                onCreateExecution = { _, _, _ -> },
                onNavigateToExecution = {},
            )
        }
    }
}
