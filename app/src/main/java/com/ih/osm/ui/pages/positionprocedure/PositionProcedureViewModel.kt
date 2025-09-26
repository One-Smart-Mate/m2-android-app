package com.ih.osm.ui.pages.positionprocedure

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ih.osm.R
import com.ih.osm.data.model.GenerateCiltExecutionRequest
import com.ih.osm.domain.model.CiltProcedureData
import com.ih.osm.domain.model.NodeCardItem
import com.ih.osm.domain.repository.network.NetworkRepository
import com.ih.osm.domain.usecase.procedure.CreateCiltExecutionUseCase
import com.ih.osm.domain.usecase.procedure.GetPositionProceduresUseCase
import com.ih.osm.domain.usecase.session.GetSessionUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.pages.positionprocedure.action.PositionProcedureAction
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PositionProcedureViewModel
    @Inject
    constructor(
        private val getPositionProceduresUseCase: GetPositionProceduresUseCase,
        private val createCiltExecutionUseCase: CreateCiltExecutionUseCase,
        private val getSessionUseCase: GetSessionUseCase,
        private val networkRepository: NetworkRepository,
        @ApplicationContext private val context: Context,
    ) : BaseViewModel<PositionProcedureViewModel.UiState>(UiState()) {
        data class UiState(
            val procedureData: CiltProcedureData? = null,
            val isLoading: Boolean = false,
            val message: String = EMPTY,
            val nodeLevelList: Map<Int, List<NodeCardItem>> = emptyMap(),
            val selectedLevelList: Map<Int, String> = emptyMap(),
            val creatingExecutionForSequence: Int? = null,
            // (sequenceId, executionId)
            val createdExecutionData: Pair<Int, Int>? = null,
        )

        init {
            handleGetPositionProcedures()
        }

        fun handleAction(action: PositionProcedureAction) {
            when (action) {
                is PositionProcedureAction.UpdateList -> handleUpdateProcedureList()
            }
        }

        private fun handleGetPositionProcedures() {
            viewModelScope.launch {
                setState { copy(isLoading = true) }
                kotlin
                    .runCatching {
                        android.util.Log.d("PositionProcedureVM", "=== STARTING API CALL ===")
                        val result = callUseCase { getPositionProceduresUseCase() }
                        android.util.Log.d("PositionProcedureVM", "=== API CALL SUCCESS ===")
                        android.util.Log.d("PositionProcedureVM", "Result is null: ${result == null}")
                        android.util.Log.d("PositionProcedureVM", "Result positions size: ${result?.positions?.size}")
                        android.util.Log.d("PositionProcedureVM", "Result positions isEmpty: ${result?.positions?.isEmpty()}")

                        result?.positions?.forEachIndexed { index, position ->
                            android.util.Log.d("PositionProcedureVM", "Position $index: ${position.name}")
                            android.util.Log.d("PositionProcedureVM", "  - CiltMasters count: ${position.ciltMasters.size}")
                            position.ciltMasters.forEach { master ->
                                android.util.Log.d("PositionProcedureVM", "    * Master: ${master.ciltName}")
                                android.util.Log.d("PositionProcedureVM", "    * Sequences: ${master.sequences.size}")
                            }
                        }
                        result
                    }.onSuccess { procedureData ->
                        android.util.Log.d("PositionProcedureVM", "=== SETTING STATE SUCCESS ===")
                        android.util.Log.d(
                            "PositionProcedureVM",
                            "About to set procedureData with positions: ${procedureData?.positions?.size}",
                        )
                        setState {
                            copy(
                                procedureData = procedureData,
                                message = EMPTY,
                                isLoading = false,
                            )
                        }
                        android.util.Log.d("PositionProcedureVM", "=== STATE SET COMPLETE ===")
                        val currentState = getState()
                        android.util.Log.d(
                            "PositionProcedureVM",
                            "Current state procedureData is null: ${currentState.procedureData == null}",
                        )
                        android.util.Log.d(
                            "PositionProcedureVM",
                            "Current state positions size: ${currentState.procedureData?.positions?.size}",
                        )
                    }.onFailure { exception ->
                        android.util.Log.e("PositionProcedureVM", "=== API CALL FAILED ===")
                        android.util.Log.e("PositionProcedureVM", "Exception: ${exception.message}")
                        android.util.Log.e("PositionProcedureVM", "Exception type: ${exception.javaClass.simpleName}")
                        exception.printStackTrace()
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
                handleGetPositionProcedures()
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
