package com.ih.osm.ui.pages.procedimiento

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ih.osm.R
import com.ih.osm.data.model.GenerateCiltExecutionRequest
import com.ih.osm.domain.model.NodeCardItem
import com.ih.osm.domain.model.ProcedimientoCiltData
import com.ih.osm.domain.model.toNodeItemList
import com.ih.osm.domain.repository.network.NetworkRepository
import com.ih.osm.domain.usecase.level.GetLevelsUseCase
import com.ih.osm.domain.usecase.procedimiento.CreateCiltExecutionUseCase
import com.ih.osm.domain.usecase.procedimiento.GetProcedimientosByLevelUseCase
import com.ih.osm.domain.usecase.session.GetSessionUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.pages.procedimiento.action.ProcedimientoListAction
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcedimientoListViewModel
    @Inject
    constructor(
        private val getLevelsUseCase: GetLevelsUseCase,
        private val getProcedimientosByLevelUseCase: GetProcedimientosByLevelUseCase,
        private val createCiltExecutionUseCase: CreateCiltExecutionUseCase,
        private val getSessionUseCase: GetSessionUseCase,
        private val networkRepository: NetworkRepository,
        @ApplicationContext private val context: Context,
    ) : BaseViewModel<ProcedimientoListViewModel.UiState>(UiState()) {
        data class UiState(
            val procedimientoData: ProcedimientoCiltData? = null,
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

        fun handleAction(action: ProcedimientoListAction) {
            when (action) {
                is ProcedimientoListAction.UpdateList -> handleUpdateProcedimientoList()
                is ProcedimientoListAction.SetLevel -> handleSetLevel(action.id, action.key)
            }
        }

        private fun handleGetLevels() {
            viewModelScope.launch {
                kotlin.runCatching {
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
                // Load Procedimientos for the selected level
                handleGetProcedimientosByLevel(id)
                setState { copy(nodeLevelList = list) }
            }
        }

        private suspend fun checkLastLevelSection(id: String) {
            val isEmpty = getState().levelList.none { it.superiorId == id }
            setState { copy(lastLevelCompleted = isEmpty) }
        }

        private fun handleGetProcedimientosByLevel(levelId: String) {
            viewModelScope.launch {
                setState { copy(isLoading = true) }
                kotlin.runCatching {
                    callUseCase { getProcedimientosByLevelUseCase(levelId) }
                }.onSuccess { procedimientoData ->
                    setState {
                        copy(
                            procedimientoData = procedimientoData,
                            message = EMPTY,
                            isLoading = false,
                        )
                    }
                }.onFailure { exception ->
                    setState {
                        copy(
                            procedimientoData = null,
                            isLoading = false,
                            message =
                                context.getString(
                                    R.string.error_loading_procedimientos,
                                    exception.localizedMessage,
                                ),
                        )
                    }
                }
            }
        }

        private fun handleUpdateProcedimientoList() {
            viewModelScope.launch {
                // Reload from the current selected level
                val currentSelectedLevel = getState().selectedLevelList.values.lastOrNull()
                if (currentSelectedLevel?.isNotEmpty() == true) {
                    handleGetProcedimientosByLevel(currentSelectedLevel)
                } else {
                    setState {
                        copy(
                            procedimientoData = null,
                            message = context.getString(R.string.select_level_procedimientos),
                        )
                    }
                }
            }
        }

        fun clearNavigationData() {
            setState { copy(createdExecutionData = null) }
        }

        fun createExecution(
            sequence: ProcedimientoCiltData.Sequence,
            positionId: Int,
            levelId: String,
        ) {
            Log.d(
                "ProcedimientoListViewModel",
                "createExecution called for sequence: ${sequence.id}, positionId: $positionId, levelId: $levelId",
            )
            Log.d("ProcedimientoListViewModel", "Starting coroutine...")

            viewModelScope.launch(Dispatchers.IO) {
                Log.d("ProcedimientoListViewModel", "Inside coroutine, starting execution creation...")
                Log.d("ProcedimientoListViewModel", "Setting creatingExecutionForSequence = ${sequence.id}")
                setState { copy(creatingExecutionForSequence = sequence.id) }
                Log.d(
                    "ProcedimientoListViewModel",
                    "State updated, current creatingExecutionForSequence = ${getState().creatingExecutionForSequence}",
                )

                try {
                    Log.d("ProcedimientoListViewModel", "Getting session...")
                    val session = getSessionUseCase()
                    Log.d("ProcedimientoListViewModel", "Session userId: ${session.userId}")

                    val request =
                        GenerateCiltExecutionRequest(
                            sequenceId = sequence.id,
                            userId = session.userId.toIntOrNull() ?: 1,
                        )

                    Log.d(
                        "ProcedimientoListViewModel",
                        "Making generate request: sequenceId=${sequence.id}, userId=${session.userId.toIntOrNull() ?: 1}",
                    )
                    val response = networkRepository.generateCiltExecution(request)
                    val executionId = response.data.siteExecutionId
                    Log.d("ProcedimientoListViewModel", "Generated execution with ID: $executionId")

                    setState {
                        copy(
                            creatingExecutionForSequence = null,
                            createdExecutionData = Pair(sequence.id, executionId),
                            message = "Ejecución creada exitosamente",
                        )
                    }

                    Log.d(
                        "ProcedimientoListViewModel",
                        "Execution created successfully, sequenceId: ${sequence.id}, executionId: $executionId",
                    )
                } catch (e: Exception) {
                    Log.e("ProcedimientoListViewModel", "Error creating execution", e)
                    setState {
                        copy(
                            creatingExecutionForSequence = null,
                            createdExecutionData = null,
                            message = "Error al crear ejecución: ${e.localizedMessage}",
                        )
                    }
                }
            }
        }
    }
