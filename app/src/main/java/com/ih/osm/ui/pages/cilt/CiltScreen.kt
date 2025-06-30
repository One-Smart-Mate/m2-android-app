package com.ih.osm.ui.pages.cilt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.ih.osm.R
import com.ih.osm.domain.model.CiltData
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.SpacerSize
import com.ih.osm.ui.components.buttons.ButtonType
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.components.cilt.CiltDetailSection
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.pages.cilt.action.CiltAction
import com.ih.osm.ui.theme.PaddingToolbar
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun CiltScreen(
    navController: NavController,
    viewModel: CiltRoutineViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        viewModel.handleGetCilts()
    }

    LaunchedEffect(state.isSequenceFinished) {
        viewModel.handleGetCilts()
        viewModel.resetSequenceFinishedFlag()
    }

    if (state.isLoading) {
        LoadingScreen()
    } else {
        CiltContent(
            navController = navController,
            data = state.ciltData,
        ) { action ->
            /*
            when (action) {
                CiltAction.GetCilts -> viewModel.handleGetCilts()
            }
             */
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

@Composable
fun CiltContent(
    navController: NavController,
    data: CiltData?,
    onAction: (CiltAction) -> Unit,
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding),
        ) {
            stickyHeader {
                Column {
                    CustomAppBar(
                        navController = navController,
                        title = stringResource(R.string.cilt_routine),
                    )
                    CustomSpacer(space = SpacerSize.SMALL)
                }
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        CustomButton(
                            text = stringResource(R.string.update_cilts),
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.DEFAULT,
                        ) {
                            //
                        }
                        CustomButton(
                            text = stringResource(R.string.download_third_party_cilt),
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.DEFAULT,
                        ) {
                            //
                        }
                        CustomButton(
                            text = stringResource(R.string.non_programmable_cilts),
                            modifier = Modifier.weight(1f),
                            buttonType = ButtonType.DEFAULT,
                        ) {
                            //
                        }
                    }
                }

                CustomSpacer(space = SpacerSize.SMALL)
            }

            if (data != null && data.positions.any { it.ciltMasters.isNotEmpty() }) {
                item {
                    CiltDetailSection(
                        data = data,
                        navController = navController,
                    )
                }
            } else {
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(dimensionResource(id = R.dimen.box_padding)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.vertical_spacedby)),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                            )
                            Text(
                                text = stringResource(R.string.no_cilt_data),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}
