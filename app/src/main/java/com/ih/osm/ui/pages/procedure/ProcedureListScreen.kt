package com.ih.osm.ui.pages.procedure

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ih.osm.R
import com.ih.osm.domain.model.CiltProcedureData
import com.ih.osm.domain.model.NodeCardItem
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.procedure.CiltProcedureCard
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.extensions.getTextColor
import com.ih.osm.ui.navigation.navigateToCiltDetailWithTarget
import com.ih.osm.ui.pages.createcard.SectionItemCard
import com.ih.osm.ui.pages.procedure.action.ProcedureListAction
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingNormal

@Composable
fun ProcedureListScreen(
    navController: NavController,
    viewModel: ProcedureListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Handle navigation after execution is created
    LaunchedEffect(state.executionCreated) {
        state.executionCreated?.let { (sequenceId, siteExecutionId) ->
            navController.navigateToCiltDetailWithTarget(0, siteExecutionId)
            viewModel.handleAction(ProcedureListAction.ClearAllExecutionState)
        }
    }

    // Clean up state when leaving the screen
    DisposableEffect(navController) {
        onDispose {
            viewModel.handleAction(ProcedureListAction.ClearAllExecutionState)
        }
    }

    if (state.isLoading) {
        LoadingScreen(text = stringResource(R.string.loading_procedures))
    } else {
        ProcedureListContent(
            navController = navController,
            procedureData = state.procedureData,
            levelList = state.nodeLevelList,
            selectedLevelList = state.selectedLevelList,
            creatingExecutionForSequence = state.creatingExecutionForSequence,
            onAction = { action ->
                if (action is ProcedureListAction.SetLevel) {
                    viewModel.handleAction(action)
                }
            },
            onCreateExecution = { sequence, positionId, levelId ->
                viewModel.handleAction(
                    ProcedureListAction.CreateExecution(
                        sequence = sequence,
                        positionId = positionId,
                        levelId = levelId,
                    ),
                )
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProcedureListContent(
    navController: NavController,
    procedureData: CiltProcedureData?,
    levelList: Map<Int, List<NodeCardItem>>,
    selectedLevelList: Map<Int, String>,
    creatingExecutionForSequence: Int?,
    onAction: (ProcedureListAction) -> Unit,
    onCreateExecution: (CiltProcedureData.Sequence, Int, String) -> Unit,
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
                        title = stringResource(id = R.string.general_procedures),
                    )
                    CustomSpacer(space = SpacerSize.SMALL)
                }
            }

            item {
                LevelContent(
                    levelList = levelList,
                    onLevelClick = { item, key ->
                        onAction(ProcedureListAction.SetLevel(item.id, key))
                    },
                    selectedLevelList = selectedLevelList,
                )
            }

            item {
                CustomSpacer(space = SpacerSize.EXTRA_LARGE)
            }

            if (procedureData?.positions?.isEmpty() != false) {
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
                                text = stringResource(id = R.string.select_level_to_view_available_procedures),
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
                procedureData?.positions?.forEach { position ->
                    item {
                        Text(
                            text = position.name,
                            style =
                                MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = getTextColor(),
                                ),
                            modifier = Modifier.padding(horizontal = PaddingNormal),
                        )
                        CustomSpacer(space = SpacerSize.SMALL)
                    }

                    items(position.ciltMasters) { ciltMaster ->
                        CiltProcedureCard(
                            ciltMaster = ciltMaster,
                            positionId = position.id,
                            levelId = selectedLevelList.values.lastOrNull() ?: "528",
                            creatingExecutionForSequence = creatingExecutionForSequence,
                            onCreateExecution = onCreateExecution,
                        )
                    }
                }

                item {
                    CustomSpacer(space = SpacerSize.EXTRA_LARGE)
                }
            }
        }
    }
}

@Composable
fun LevelContent(
    levelList: Map<Int, List<NodeCardItem>>,
    onLevelClick: (NodeCardItem, key: Int) -> Unit,
    selectedLevelList: Map<Int, String>,
) {
    AnimatedVisibility(visible = levelList.isNotEmpty()) {
        Column {
            CustomSpacer()

            levelList.forEach { level ->
                if (level.value.isNotEmpty()) {
                    Text(
                        text = "${stringResource(R.string.level)} ${level.key}",
                        style =
                            MaterialTheme.typography.titleLarge
                                .copy(fontWeight = FontWeight.Bold),
                    )
                    CustomSpacer(space = SpacerSize.SMALL)
                    LazyRow {
                        items(level.value) { item ->
                            SectionItemCard(
                                title = item.name,
                                description = item.description,
                                selected = item.id == selectedLevelList[level.key],
                            ) {
                                onLevelClick(item, level.key)
                            }
                        }
                    }
                    CustomSpacer()
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
private fun ProcedureListScreenPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            ProcedureListContent(
                navController = rememberNavController(),
                procedureData = CiltProcedureData.mockData(),
                levelList = emptyMap(),
                selectedLevelList = emptyMap(),
                creatingExecutionForSequence = null,
                onAction = {},
                onCreateExecution = { _, _, _ -> },
            )
        }
    }
}
