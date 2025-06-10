package com.ih.osm.ui.pages.cilt

import androidx.lifecycle.viewModelScope
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.data.model.GetCiltsRequest
import com.ih.osm.data.model.StartSequenceExecutionRequest
import com.ih.osm.data.model.StopSequenceExecutionRequest
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.model.Sequence
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.usecase.cilt.CreateCiltEvidenceUseCase
import com.ih.osm.domain.usecase.cilt.GetCiltsUseCase
import com.ih.osm.domain.usecase.cilt.GetOplByIdUseCase
import com.ih.osm.domain.usecase.cilt.StartSequenceExecutionUseCase
import com.ih.osm.domain.usecase.cilt.StopSequenceExecutionUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class CiltRoutineViewModel
    @Inject
    constructor(
        private val getCiltsUseCase: GetCiltsUseCase,
        private val getOplByIdUseCase: GetOplByIdUseCase,
        private val createCiltEvidenceUseCase: CreateCiltEvidenceUseCase,
        private val startSequenceExecutionUseCase: StartSequenceExecutionUseCase,
        private val stopSequenceExecutionUseCase: StopSequenceExecutionUseCase,
        private val authRepository: AuthRepository,
    ) : BaseViewModel<CiltRoutineViewModel.UiState>(UiState()) {
        init {
            handleGetCilts()
        }

        data class UiState(
            val ciltData: CiltData? = null,
            val isLoading: Boolean = false,
            val message: String = EMPTY,
            val isCreatingEvidence: Boolean = false,
            val createEvidenceMessage: String = EMPTY,
            val opl: Opl? = null,
            val remediationOpl: Opl? = null,
        )

        private fun handleGetCilts() {
            viewModelScope.launch {
                setState { copy(isLoading = true) }

                val userId = authRepository.get()?.userId?.toIntOrNull()
                val date = getCurrentDate()

                if (userId == null) {
                    setState {
                        copy(
                            isLoading = false,
                            message = "Error: No se pudo obtener el ID del usuario",
                        )
                    }
                    return@launch
                }

                val body = GetCiltsRequest(userId, date)

                kotlin.runCatching {
                    callUseCase { getCiltsUseCase(body) }
                }.onSuccess { data ->
                    setState {
                        copy(
                            ciltData = data,
                            isLoading = false,
                            message = EMPTY,
                        )
                    }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    setState {
                        copy(
                            ciltData = null,
                            isLoading = false,
                            message = it.localizedMessage.orEmpty(),
                        )
                    }
                }
            }
        }

        fun getOplById(id: String) {
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { getOplByIdUseCase(id) }
                }.onSuccess { opl ->
                    setState { copy(opl = opl) }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    setState { copy(message = "No se pudo cargar el OPL: ${it.localizedMessage}") }
                }
            }
        }

        fun getRemediationOplById(id: String) {
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { getOplByIdUseCase(id) }
                }.onSuccess { opl ->
                    setState { copy(remediationOpl = opl) }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    setState { copy(message = "No se pudo cargar el OPL de remediación: ${it.localizedMessage}") }
                }
            }
        }

        fun startSequenceExecution(executionId: Int) {
            viewModelScope.launch {
                val startDate = getCurrentDateTime()
                val request =
                    StartSequenceExecutionRequest(
                        id = executionId,
                        startDate = startDate,
                    )
                kotlin.runCatching {
                    callUseCase { startSequenceExecutionUseCase(request) }
                }.onSuccess {
                    setState { copy(message = "Secuencia iniciada correctamente") }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    setState { copy(message = "Error al iniciar la secuencia: ${it.localizedMessage}") }
                }
            }
        }

        fun stopSequenceExecution(
            executionId: Int,
            initialParameter: String,
            evidenceAtCreation: Boolean,
            finalParameter: String,
            evidenceAtFinal: Boolean,
            nok: Boolean,
            amTagId: Int,
        ) {
            viewModelScope.launch {
                val stopDate = getCurrentDateTime()
                val request =
                    StopSequenceExecutionRequest(
                        id = executionId,
                        stopDate = stopDate,
                        initialParameter = initialParameter,
                        evidenceAtCreation = evidenceAtCreation,
                        finalParameter = finalParameter,
                        evidenceAtFinal = evidenceAtFinal,
                        nok = nok,
                        amTagId = amTagId,
                    )

                kotlin.runCatching {
                    callUseCase { stopSequenceExecutionUseCase(request) }
                }.onSuccess {
                    setState { copy(message = "Secuencia finalizada correctamente") }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    setState { copy(message = "Error al finalizar la secuencia: ${it.localizedMessage}") }
                }
            }
        }

        fun getSequenceById(sequenceId: Int): Sequence? {
            return state.value.ciltData?.positions
                ?.flatMap { it.ciltMasters }
                ?.flatMap { it.sequences }
                ?.find { it.id == sequenceId }
        }

        fun createEvidence(request: CiltEvidenceRequest) {
            viewModelScope.launch {
                setState { copy(isCreatingEvidence = true, createEvidenceMessage = EMPTY) }
                kotlin.runCatching {
                    createCiltEvidenceUseCase(request)
                }.onSuccess {
                    setState {
                        copy(
                            isCreatingEvidence = false,
                            createEvidenceMessage = "Evidencia creada correctamente",
                        )
                    }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    setState {
                        copy(
                            isCreatingEvidence = false,
                            createEvidenceMessage = it.localizedMessage.orEmpty(),
                        )
                    }
                }
            }
        }

        private fun getCurrentDate(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }

        private fun getCurrentDateTime(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(Date())
        }
    }
