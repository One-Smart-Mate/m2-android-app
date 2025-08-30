package com.ih.osm.ui.pages.procedimiento

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
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
import com.ih.osm.domain.model.NodeCardItem
import com.ih.osm.domain.model.ProcedimientoCiltData
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.procedimiento.ProcedimientoCiltCard
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.extensions.getTextColor
import com.ih.osm.ui.navigation.navigateToSequence
import com.ih.osm.ui.pages.createcard.SectionItemCard
import com.ih.osm.ui.pages.procedimiento.action.ProcedimientoListAction
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingNormal

@Composable
fun ProcedimientoListScreen(
    navController: NavController,
    viewModel: ProcedimientoListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Log.d("ProcedimientoListScreen", "Screen recomposed - isLoading: ${state.isLoading}")
    Log.d("ProcedimientoListScreen", "procedimientoData is null: ${state.procedimientoData == null}")
    Log.d("ProcedimientoListScreen", "creatingExecutionForSequence: ${state.creatingExecutionForSequence}")
    Log.d("ProcedimientoListScreen", "createdExecutionData: ${state.createdExecutionData}")
    state.procedimientoData?.let { data ->
        Log.d("ProcedimientoListScreen", "Positions count: ${data.positions.size}")
        data.positions.forEach { position ->
            Log.d("ProcedimientoListScreen", "Position: ${position.name}, CiltMasters: ${position.ciltMasters.size}")
        }
    }

    // Handle navigation after successful execution creation
    state.createdExecutionData?.let { (sequenceId, executionId) ->
        Log.d("ProcedimientoListScreen", "Navigating to sequence: $sequenceId, execution: $executionId")
        try {
            navController.navigateToSequence(sequenceId, executionId)
            Log.d("ProcedimientoListScreen", "Navigation call successful")
        } catch (e: Exception) {
            Log.e("ProcedimientoListScreen", "Navigation failed", e)
        }
        viewModel.clearNavigationData()
        Log.d("ProcedimientoListScreen", "Navigation data cleared")
    }

    if (state.isLoading) {
        Log.d("ProcedimientoListScreen", "Showing loading screen")
        LoadingScreen(text = "Cargando procedimientos...")
    } else {
        Log.d("ProcedimientoListScreen", "Showing content screen")
        ProcedimientoListContent(
            navController = navController,
            procedimientoData = state.procedimientoData,
            levelList = state.nodeLevelList,
            selectedLevelList = state.selectedLevelList,
            creatingExecutionForSequence = state.creatingExecutionForSequence,
            onAction = { action ->
                if (action is ProcedimientoListAction.SetLevel) {
                    viewModel.handleAction(action)
                }
            },
            onCreateExecution = { sequence, positionId, levelId ->
                Log.d("ProcedimientoListScreen", "Creating execution for sequence: ${sequence.id}")
                Log.d("ProcedimientoListScreen", "Calling viewModel.createExecution...")
                try {
                    viewModel.createExecution(sequence, positionId, levelId)
                    Log.d("ProcedimientoListScreen", "viewModel.createExecution call completed")
                } catch (e: Exception) {
                    Log.e("ProcedimientoListScreen", "Error calling viewModel.createExecution", e)
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
fun ProcedimientoListContent(
    navController: NavController,
    procedimientoData: ProcedimientoCiltData?,
    levelList: Map<Int, List<NodeCardItem>>,
    selectedLevelList: Map<Int, String>,
    creatingExecutionForSequence: Int?,
    onAction: (ProcedimientoListAction) -> Unit,
    onCreateExecution: (ProcedimientoCiltData.Sequence, Int, String) -> Unit,
    onNavigateToExecution: (Int) -> Unit,
) {
    Log.d("ProcedimientoListContent", "Content composable started")
    Log.d("ProcedimientoListContent", "procedimientoData: ${procedimientoData != null}")
    Log.d("ProcedimientoListContent", "levelList size: ${levelList.size}")
    Log.d("ProcedimientoListContent", "selectedLevelList size: ${selectedLevelList.size}")
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
                        title = stringResource(id = R.string.procedimientos_screen_title),
                    )
                    CustomSpacer(space = SpacerSize.SMALL)
                }
            }

            item {
                LevelContent(
                    levelList = levelList,
                    onLevelClick = { item, key ->
                        onAction(ProcedimientoListAction.SetLevel(item.id, key))
                    },
                    selectedLevelList = selectedLevelList,
                )
            }

            item {
                CustomSpacer(space = SpacerSize.EXTRA_LARGE)
            }

            Log.d("ProcedimientoListContent", "Checking positions - isEmpty: ${procedimientoData?.positions?.isEmpty()}")
            if (procedimientoData?.positions?.isEmpty() != false) {
                Log.d("ProcedimientoListContent", "Showing empty state")
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
                                text = stringResource(id = R.string.procedimientos_list_empty_title),
                                style =
                                    MaterialTheme.typography.bodyLarge
                                        .copy(
                                            color = getTextColor().copy(alpha = 0.6f),
                                        ),
                            )
                            CustomSpacer()
                            Text(
                                text = stringResource(id = R.string.procedimientos_list_empty_subtitle),
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
                Log.d("ProcedimientoListContent", "Showing positions data")
                procedimientoData?.positions?.forEach { position ->
                    Log.d("ProcedimientoListContent", "Processing position: ${position.name}")
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
                        Log.d("ProcedimientoListContent", "Rendering CiltMaster: ${ciltMaster.ciltName}")
                        ProcedimientoCiltCard(
                            ciltMaster = ciltMaster,
                            positionId = position.id,
                            levelId = selectedLevelList.values.lastOrNull() ?: "528",
                            creatingExecutionForSequence = creatingExecutionForSequence,
                            onCreateExecution = onCreateExecution,
                            onNavigateToExecution = onNavigateToExecution,
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
private fun ProcedimientoListScreenPreview() {
    OsmAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            ProcedimientoListContent(
                navController = rememberNavController(),
                procedimientoData = ProcedimientoCiltData.mockData(),
                levelList = emptyMap(),
                selectedLevelList = emptyMap(),
                creatingExecutionForSequence = null,
                onAction = {},
                onCreateExecution = { _, _, _ -> },
                onNavigateToExecution = {},
            )
        }
    }
}
