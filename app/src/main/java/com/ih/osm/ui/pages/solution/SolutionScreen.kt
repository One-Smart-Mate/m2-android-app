package com.ih.osm.ui.pages.solution

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
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
import com.ih.osm.ui.components.evidence.SectionAudiosEvidence
import com.ih.osm.ui.components.evidence.SectionImagesEvidence
import com.ih.osm.ui.components.evidence.SectionVideosEvidence
import com.ih.osm.ui.extensions.defaultScreen
import com.ih.osm.ui.extensions.getTextColor
import com.ih.osm.ui.theme.OsmAppTheme
import com.ih.osm.ui.theme.PaddingLarge
import com.ih.osm.ui.theme.PaddingNormal
import com.ih.osm.ui.theme.PaddingToolbar
import com.ih.osm.ui.utils.ASSIGN_CARD_ACTION
import com.ih.osm.ui.utils.DEFINITIVE_SOLUTION
import com.ih.osm.ui.utils.PROVISIONAL_SOLUTION
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SolutionScreen(
    navController: NavController,
    solutionType: String,
    cardId: String,
    viewModel: SolutionViewModel = mavericksViewModel()
) {
    val state by viewModel.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (state.isLoading) {
        LoadingScreen(text = state.message)
    } else {
        SolutionScreenContent(
            navController = navController,
            title = getSolutionScreenTitle(state.solutionType),
            onSearch = {
                viewModel.process(SolutionViewModel.Action.OnSearchEmployee(it))
            },
            employeeList = state.resultList,
            onSelectEmployee = {
                viewModel.process(SolutionViewModel.Action.OnSelectEmployee(it))
            },
            selectedEmployee = state.selectedEmployee,
            onCommentChange = {
                viewModel.process(SolutionViewModel.Action.OnCommentChange(it))
            },
            onSave = {
                viewModel.process(SolutionViewModel.Action.OnSave)
            },
            onAddEvidence = { uri, type ->
                viewModel.process(SolutionViewModel.Action.OnAddEvidence(uri, type))
            },
            onDeleteEvidence = {
                viewModel.process(SolutionViewModel.Action.OnDeleteEvidence(it))
            },
            evidences = state.evidences,
            audioDuration = state.audioDuration,
            solutionType = state.solutionType,
            isEvidenceEnabled = state.isEvidenceEnabled,
            isCommentsEnabled = state.isCommentsEnabled
        )
    }

    SnackbarHost(hostState = snackBarHostState) {
        Snackbar(
            snackbarData = it,
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = Color.White,
            modifier = Modifier.padding(top = PaddingToolbar)
        )
    }

    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { state }
            .flowWithLifecycle(lifecycle)
            .collect {
                if (it.isSolutionSuccess) {
                    navController.popBackStack()
                }
                if (it.isFetching.not()) {
                    viewModel.process(
                        SolutionViewModel.Action.SetSolutionInfo(
                            solutionType,
                            cardId
                        )
                    )
                }
                if (state.isLoading.not() && state.message.isNotEmpty()) {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = state.message
                        )
                        viewModel.process(SolutionViewModel.Action.ClearMessage)
                    }
                }
            }
    }
}

@Composable
fun getSolutionScreenTitle(solutionType: String): String {
    return when (solutionType) {
        DEFINITIVE_SOLUTION -> {
            stringResource(R.string.definitive_solution)
        }

        PROVISIONAL_SOLUTION -> {
            stringResource(R.string.provisional_solution)
        }

        ASSIGN_CARD_ACTION -> {
            stringResource(R.string.assign_mechanic)
        }

        else -> stringResource(R.string.empty)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SolutionScreenContent(
    title: String,
    navController: NavController,
    onSearch: (String) -> Unit,
    employeeList: List<Employee>,
    onSelectEmployee: (Employee) -> Unit,
    selectedEmployee: Employee? = null,
    onCommentChange: (String) -> Unit,
    onSave: () -> Unit,
    evidences: List<Evidence>,
    onAddEvidence: (Uri, EvidenceType) -> Unit,
    onDeleteEvidence: (Evidence) -> Unit,
    audioDuration: Int,
    solutionType: String,
    isEvidenceEnabled: Boolean,
    isCommentsEnabled: Boolean
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.defaultScreen(padding)
        ) {
            stickyHeader {
                CustomAppBar(
                    navController = navController,
                    title = title
                )
            }
            item {
                CustomTextField(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(PaddingNormal),
                    label = stringResource(R.string.search_for_a_user),
                    icon = Icons.Filled.Search
                ) {
                    onSearch(it)
                }
            }
            items(employeeList) {
                UserCardItem(
                    title = it.name
                ) {
                    onSelectEmployee(it)
                }
            }

            item {
                CustomSpacer()
                AnimatedVisibility(visible = selectedEmployee != null) {
                    SectionTag(
                        title = stringResource(R.string.selected_user),
                        value = selectedEmployee?.name.orEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                CustomSpacer()
            }

            item {
                AnimatedVisibility(visible = isEvidenceEnabled) {
                    Column {
                        CustomSpacer()
                        HorizontalDivider()
                        SectionCardEvidence(
                            audioDuration = audioDuration,
                            onAddEvidence = onAddEvidence,
                            imageType = if (solutionType == DEFINITIVE_SOLUTION) {
                                EvidenceType.IMCL
                            } else {
                                EvidenceType.IMPS
                            },
                            audioType = if (solutionType == DEFINITIVE_SOLUTION) {
                                EvidenceType.AUCL
                            } else {
                                EvidenceType.AUPS
                            },
                            videoType = if (solutionType == DEFINITIVE_SOLUTION) {
                                EvidenceType.VICL
                            } else {
                                EvidenceType.VIPS
                            }
                        )
                        SectionImagesEvidence(imageEvidences = evidences.toImages()) {
                            onDeleteEvidence(it)
                        }
                        SectionVideosEvidence(videoEvidences = evidences.toVideos()) {
                            onDeleteEvidence(it)
                        }
                        SectionAudiosEvidence(audioEvidences = evidences.toAudios()) {
                            onDeleteEvidence(it)
                        }
                        CustomSpacer()
                    }
                }
            }

            item {
                AnimatedVisibility(visible = isCommentsEnabled) {
                    Column {
                        HorizontalDivider()
                        CustomSpacer()
                        CustomTextField(
                            modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(PaddingNormal),
                            label = stringResource(R.string.comment_solution),
                            icon = Icons.Filled.Create
                        ) {
                            onCommentChange(it)
                        }
                        CustomSpacer()
                    }
                }
                CustomButton(text = stringResource(R.string.save)) {
                    onSave()
                }
            }
        }
    }
}

@Composable
fun UserCardItem(title: String, onClick: () -> Unit) {
    Box(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingLarge, vertical = 1.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                onClick()
            }
    ) {
        Text(
            text = title,
            style =
            MaterialTheme.typography.bodyLarge
                .copy(color = getTextColor()),
            modifier = Modifier.padding(PaddingNormal)
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
@Composable
fun SolutionScreenPreview() {
    OsmAppTheme {
        Surface {
        }
    }
}
