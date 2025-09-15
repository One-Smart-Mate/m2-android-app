package com.ih.osm.ui.pages.procedure

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ih.osm.R
import com.ih.osm.data.model.GenerateCiltExecutionRequest
import com.ih.osm.domain.model.CiltProcedureData
import com.ih.osm.domain.model.NodeCardItem
import com.ih.osm.domain.model.toNodeItemList
import com.ih.osm.domain.repository.network.NetworkRepository
import com.ih.osm.domain.usecase.level.GetLevelsUseCase
import com.ih.osm.domain.usecase.procedure.CreateCiltExecutionUseCase
import com.ih.osm.domain.usecase.procedure.GetProcedureByLevelUseCase
import com.ih.osm.domain.usecase.session.GetSessionUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.pages.procedure.action.ProcedureListAction
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcedureListViewModel
    @Inject
    constructor(
        private val getLevelsUseCase: GetLevelsUseCase,
        private val getProcedureByLevelUseCase: GetProcedureByLevelUseCase,
        private val createCiltExecutionUseCase: CreateCiltExecutionUseCase,
        private val getSessionUseCase: GetSessionUseCase,
        private val networkRepository: NetworkRepository,
        @ApplicationContext private val context: Context,
    ) : BaseViewModel<ProcedureListViewModel.UiState>(UiState()) {
        data class UiState(
            val procedureData: CiltProcedureData? = null,
            val isLoading: Boolean = false,
            val message: String = EMPTY,
            val currentFilter: String = EMPTY,
            val levelList: List<NodeCardItem> = emptyList(),
            val nodeLevelList: Map<Int, List<NodeCardItem>> = mutableMapOf(),
            val selectedLevelList: Map<Int, String> = mutableMapOf(),
            val lastSelectedLevel: String = EMPTY,
            val lastLevelCompleted: Boolean = false,
            val creatingExecutionForSequence: Int? = null,
            // (sequenceId, executionId)
            val createdExecutionData: Pair<Int, Int>? = null,
        )

        init {
            handleGetLevels()
        }

        fun handleAction(action: ProcedureListAction) {
            when (action) {
                is ProcedureListAction.UpdateList -> handleUpdateProcedureList()
                is ProcedureListAction.SetLevel -> handleSetLevel(action.id, action.key)
            }
        }

        private fun handleGetLevels() {
            viewModelScope.launch {
                kotlin
                    .runCatching {
                        callUseCase { getLevelsUseCase() }
                    }.onSuccess { levels ->
                        val levelList = levels.toNodeItemList()

                        // Initialize the first level with root elements (superiorId = "0")
                        val rootLevels = levelList.filter { it.superiorId == "0" }

                        val initialNodeLevelList =
                            if (rootLevels.isNotEmpty()) {
                                mapOf(0 to rootLevels)
                            } else {
                                emptyMap()
                            }

                        setState {
                            copy(
                                levelList = levelList,
                                nodeLevelList = initialNodeLevelList,
                                isLoading = false,
                            )
                        }
                    }.onFailure {
                        setState {
                            copy(
                                isLoading = false,
                                message = it.localizedMessage.orEmpty(),
                            )
                        }
                    }
            }
        }

        private suspend fun getLevelById(
            id: String,
            selectedKey: Int,
        ): Map<Int, List<NodeCardItem>> {
            val state = getState()
            val firstList = state.levelList.filter { it.superiorId == id }
            val map = state.nodeLevelList.toMutableMap()
            val selectedMap = state.selectedLevelList.toMutableMap()

            for (index in selectedKey until map.keys.size) {
                map[index] = emptyList()
                selectedMap[index] = EMPTY
            }

            map[selectedKey] = firstList
            selectedMap[selectedKey.minus(1)] = id

            setState {
                copy(
                    selectedLevelList = selectedMap,
                    lastSelectedLevel = id,
                )
            }

            return map
        }

        private fun handleSetLevel(
            id: String,
            key: Int,
        ) {
            viewModelScope.launch {
                val newKey = key.plus(1)
                val list = getLevelById(id, newKey)
                checkLastLevelSection(id)
                // Load Procedures for the selected level
                handleGetProcedureByLevel(id)
                setState { copy(nodeLevelList = list) }
            }
        }

        private suspend fun checkLastLevelSection(id: String) {
            val isEmpty = getState().levelList.none { it.superiorId == id }
            setState { copy(lastLevelCompleted = isEmpty) }
        }

        private fun handleGetProcedureByLevel(levelId: String) {
            viewModelScope.launch {
                setState { copy(isLoading = true) }
                kotlin
                    .runCatching {
                        callUseCase { getProcedureByLevelUseCase(levelId) }
                    }.onSuccess { procedureData ->
                        setState {
                            copy(
                                procedureData = procedureData,
                                message = EMPTY,
                                isLoading = false,
                            )
                        }
                    }.onFailure { exception ->
                        setState {
                            copy(
                                procedureData = null,
                                isLoading = false,
                                message =
                                    context.getString(
                                        R.string.error_loading_procedures,
                                        exception.localizedMessage,
                                    ),
                            )
                        }
                    }
            }
        }

        private fun handleUpdateProcedureList() {
            viewModelScope.launch {
                // Reload from the current selected level
                val currentSelectedLevel = getState().selectedLevelList.values.lastOrNull()
                if (currentSelectedLevel?.isNotEmpty() == true) {
                    handleGetProcedureByLevel(currentSelectedLevel)
                } else {
                    setState {
                        copy(
                            procedureData = null,
                            message = context.getString(R.string.select_level_to_view_procedures),
                        )
                    }
                }
            }
        }

        fun clearNavigationData() {
            setState {
                copy(
                    createdExecutionData = null,
                    creatingExecutionForSequence = null,
                    message = EMPTY,
                )
            }
        }

        fun clearAllExecutionState() {
            setState {
                copy(
                    createdExecutionData = null,
                    creatingExecutionForSequence = null,
                    message = EMPTY,
                )
            }
        }

        fun createExecution(
            sequence: CiltProcedureData.Sequence,
            positionId: Int,
            levelId: String,
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                setState { copy(creatingExecutionForSequence = sequence.id) }

                try {
                    val session = getSessionUseCase()

                    val request =
                        GenerateCiltExecutionRequest(
                            sequenceId = sequence.id,
                            userId = session.userId.toIntOrNull() ?: 1,
                        )

                    val response = networkRepository.generateCiltExecution(request)
                    val executionId = response.data.siteExecutionId

                    setState {
                        copy(
                            creatingExecutionForSequence = null,
                            createdExecutionData = Pair(sequence.id, executionId),
                            message = context.getString(R.string.execution_created_successfully),
                        )
                    }
                } catch (e: Exception) {
                    setState {
                        copy(
                            creatingExecutionForSequence = null,
                            createdExecutionData = null,
                            message =
                                context.getString(
                                    R.string.error_creating_execution,
                                    e.localizedMessage,
                                ),
                        )
                    }
                }
            }
        }
    }
