package com.ih.osm.ui.pages.cardaction

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ih.osm.R
import com.ih.osm.domain.model.Employee
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.EvidenceType
import com.ih.osm.domain.model.toAudios
import com.ih.osm.domain.model.toImages
import com.ih.osm.domain.model.toVideos
import com.ih.osm.ui.components.CustomAppBar
import com.ih.osm.ui.components.CustomSpacer
import com.ih.osm.ui.components.CustomTextField
import com.ih.osm.ui.components.LoadingScreen
import com.ih.osm.ui.components.SectionTag
import com.ih.osm.ui.components.buttons.CustomButton
import com.ih.osm.ui.components.card.SectionCardEvidence
import com.ih.osm.ui.components.card.actions.CardItemSheetAction
import com.ih.osm.ui.components.evidence.SectionAudiosEvidence
import com.ih.osm.ui.components.evidence.SectionImagesEvidence
import com.ih.osm.ui.components.evidence.SectionVideosEvidence
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.extensions.getTextColor
import com.ih.osm.ui.pages.cardaction.action.CardAction
import com.ih.osm.ui.theme.PaddingLarge
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.PaddingToolbar
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CardActionScreen(
    navController: NavController,
    viewModel: CardActionViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current
//
    if (state.isLoading) {
        LoadingScreen()
    } else {
        SolutionScreenContent(
            navController = navController,
            title = state.screenTitle,
            onAction = {
                viewModel.process(it)
            },
            employeeList = state.filteredEmployeeList,
            isContentEnabled = state.isContentEnabled,
            selectedEmployee = state.selectedEmployee,
            actionType = state.actionType,
            evidences = state.evidences,
            comments = state.comments,
        )
    }

    SnackbarHost(hostState = snackBarHostState) {
        Snackbar(
            snackbarData = it,
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = Color.White,
            modifier = Modifier.padding(top = PaddingToolbar),
        )
    }

    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { state }
            .distinctUntilChanged()
            .collect {
                if (state.isActionSuccess) {
                    navController.popBackStack()
                }
                if (state.message.isNotEmpty()) {
                    scope.launch {
                        snackBarHostState.showSnackbar(state.message)
                    }
                    viewModel.cleanMessage()
                }
            }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SolutionScreenContent(
    title: String,
    navController: NavController,
    onAction: (CardAction) -> Unit,
    employeeList: List<Employee>,
    isContentEnabled: Boolean,
    selectedEmployee: Employee? = null,
    actionType: CardItemSheetAction?,
    evidences: List<Evidence>,
    comments: String,
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding),
        ) {
            stickyHeader {
                CustomAppBar(
                    navController = navController,
                    title = title,
                )
            }
            item {
                CustomTextField(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(PaddingNormal),
                    label = stringResource(R.string.search_for_a_user),
                    icon = Icons.Filled.Search,
                ) { query ->
                    onAction(CardAction.SearchEmployee(query))
                }
            }
            items(employeeList) { item ->
                EmployeeItemCard(
                    title = item.name,
                ) {
                    onAction(CardAction.SetEmployee(item))
                }
            }

            item {
                CustomSpacer()
                AnimatedVisibility(visible = selectedEmployee != null) {
                    SectionTag(
                        title = stringResource(R.string.selected_user),
                        value = selectedEmployee?.name.orEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                CustomSpacer()
            }
            item {
                AnimatedVisibility(visible = isContentEnabled) {
                    Column {
                        CustomSpacer()
                        HorizontalDivider()
                        SectionCardEvidence(
                            audioDuration = 120,
                            onAddEvidence = { uri, type ->
                                onAction(CardAction.AddEvidence(uri, type))
                            },
                            imageType =
                                if (actionType == CardItemSheetAction.DefinitiveSolution) {
                                    EvidenceType.IMCL
                                } else {
                                    EvidenceType.IMPS
                                },
                            audioType =
                                if (actionType == CardItemSheetAction.DefinitiveSolution) {
                                    EvidenceType.AUCL
                                } else {
                                    EvidenceType.AUPS
                                },
                            videoType =
                                if (actionType == CardItemSheetAction.DefinitiveSolution) {
                                    EvidenceType.VICL
                                } else {
                                    EvidenceType.VIPS
                                },
                        )
                        SectionImagesEvidence(imageEvidences = evidences.toImages()) {
                            onAction(CardAction.DeleteEvidence(it))
                        }
                        SectionVideosEvidence(videoEvidences = evidences.toVideos()) {
                            onAction(CardAction.DeleteEvidence(it))
                        }
                        SectionAudiosEvidence(audioEvidences = evidences.toAudios()) {
                            onAction(CardAction.DeleteEvidence(it))
                        }
                        CustomSpacer()
                        HorizontalDivider()
                        CustomSpacer()
                        CustomTextField(
                            modifier =
                                Modifier
                                    .fillMaxWidth(),
                            label = stringResource(R.string.comment_solution),
                            icon = Icons.Filled.Create,
                            value = comments,
                        ) {
                            onAction(CardAction.SetComment(it))
                        }
                    }
                }
                CustomSpacer()
                CustomButton(text = stringResource(R.string.save)) {
                    onAction(CardAction.Save)
                }
            }
        }
    }
}

@Composable
fun EmployeeItemCard(
    title: String,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingLarge, vertical = 1.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp),
                ).clickable {
                    onClick()
                },
    ) {
        Text(
            text = title,
            style =
                MaterialTheme.typography.bodyLarge
                    .copy(color = getTextColor()),
            modifier = Modifier.padding(PaddingNormal),
        )
    }
}
