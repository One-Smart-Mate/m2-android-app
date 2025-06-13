package com.ih.osm.ui.pages.cilt

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ih.osm.R
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.data.model.GetCiltsRequest
import com.ih.osm.data.model.StartSequenceExecutionRequest
import com.ih.osm.data.model.StopSequenceExecutionRequest
import com.ih.osm.data.model.UpdateCiltEvidenceRequest
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.model.Sequence
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.usecase.cilt.CreateCiltEvidenceUseCase
import com.ih.osm.domain.usecase.cilt.GetCiltsUseCase
import com.ih.osm.domain.usecase.cilt.GetOplByIdUseCase
import com.ih.osm.domain.usecase.cilt.StartSequenceExecutionUseCase
import com.ih.osm.domain.usecase.cilt.StopSequenceExecutionUseCase
import com.ih.osm.domain.usecase.cilt.UpdateCiltEvidenceUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.extensions.getCurrentDate
import com.ih.osm.ui.extensions.getCurrentDateTimeUtc
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CiltRoutineViewModel
    @Inject
    constructor(
        private val getCiltsUseCase: GetCiltsUseCase,
        private val getOplByIdUseCase: GetOplByIdUseCase,
        private val createCiltEvidenceUseCase: CreateCiltEvidenceUseCase,
        private val updateCiltEvidenceUseCase: UpdateCiltEvidenceUseCase,
        private val startSequenceExecutionUseCase: StartSequenceExecutionUseCase,
        private val stopSequenceExecutionUseCase: StopSequenceExecutionUseCase,
        private val authRepository: AuthRepository,
        @ApplicationContext private val context: Context,
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
                            message = context.getString(R.string.error_user_id_not_found),
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
                    setState {
                        copy(
                            message =
                                context.getString(
                                    R.string.error_loading_opls,
                                    it.localizedMessage.orEmpty(),
                                ),
                        )
                    }
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
                    setState {
                        copy(
                            message =
                                context.getString(
                                    R.string.error_loading_remediation_opl,
                                    it.localizedMessage.orEmpty(),
                                ),
                        )
                    }
                }
            }
        }

        fun startSequenceExecution(executionId: Int) {
            viewModelScope.launch {
                val startDate = getCurrentDateTimeUtc()
                val request =
                    StartSequenceExecutionRequest(
                        id = executionId,
                        startDate = startDate,
                    )
                kotlin.runCatching {
                    callUseCase { startSequenceExecutionUseCase(request) }
                }.onSuccess {
                    setState { copy(message = context.getString(R.string.sequence_started_successfully)) }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    setState {
                        copy(
                            message =
                                context.getString(
                                    R.string.error_starting_sequence,
                                    it.localizedMessage.orEmpty(),
                                ),
                        )
                    }
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
                val stopDate = getCurrentDateTimeUtc()
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
                    setState { copy(message = context.getString(R.string.sequence_stopped_successfully)) }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    setState {
                        copy(
                            message =
                                context.getString(
                                    R.string.error_stopping_sequence,
                                    it.localizedMessage.orEmpty(),
                                ),
                        )
                    }
                }
            }
        }

        fun createEvidence(
            siteId: Int,
            positionId: Int,
            ciltId: Int,
            ciltExecutionsEvidencesId: Int,
            evidenceUrl: String,
        ) {
            viewModelScope.launch {
                val currentTime = getCurrentDateTimeUtc()
                val request =
                    CiltEvidenceRequest(
                        siteId = siteId,
                        positionId = positionId,
                        ciltId = ciltId,
                        ciltExecutionsEvidencesId = ciltExecutionsEvidencesId,
                        evidenceUrl = evidenceUrl,
                        createdAt = currentTime,
                    )
                kotlin.runCatching {
                    callUseCase { createCiltEvidenceUseCase(request) }
                }.onSuccess {
                    setState { copy(message = context.getString(R.string.evidence_created_successfully)) }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    setState {
                        copy(
                            message =
                                context.getString(
                                    R.string.error_creating_evidence,
                                    it.localizedMessage.orEmpty(),
                                ),
                        )
                    }
                }
            }
        }

        fun updateEvidence(
            id: Int,
            siteId: Int,
            positionId: Int,
            ciltId: Int,
            ciltExecutionsEvidencesId: Int,
            evidenceUrl: String,
        ) {
            viewModelScope.launch {
                val request =
                    UpdateCiltEvidenceRequest(
                        id = id,
                        siteId = siteId,
                        positionId = positionId,
                        ciltId = ciltId,
                        ciltExecutionsEvidencesId = ciltExecutionsEvidencesId,
                        evidenceUrl = evidenceUrl,
                    )
                kotlin.runCatching {
                    callUseCase { updateCiltEvidenceUseCase(request) }
                }.onSuccess {
                    setState { copy(message = context.getString(R.string.evidence_created_successfully)) }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    setState {
                        copy(
                            message =
                                context.getString(
                                    R.string.error_creating_evidence,
                                    it.localizedMessage.orEmpty(),
                                ),
                        )
                    }
                }
            }
        }

        fun getSequenceById(sequenceId: Int): Sequence? {
            return state.value.ciltData?.positions
                ?.flatMap { it.ciltMasters }
                ?.flatMap { it.sequences }
                ?.find { it.id == sequenceId }
        }
    }
