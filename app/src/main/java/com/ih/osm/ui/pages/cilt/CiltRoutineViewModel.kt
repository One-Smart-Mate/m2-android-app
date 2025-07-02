package com.ih.osm.ui.pages.cilt

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ih.osm.R
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.core.notifications.NotificationManager
import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.data.model.GetCiltsRequest
import com.ih.osm.data.model.StartSequenceExecutionRequest
import com.ih.osm.data.model.StopSequenceExecutionRequest
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.EvidenceType
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.model.Sequence
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.firebase.FirebaseStorageRepository
import com.ih.osm.domain.usecase.cilt.CreateCiltEvidenceUseCase
import com.ih.osm.domain.usecase.cilt.GetCiltsUseCase
import com.ih.osm.domain.usecase.cilt.GetOplByIdUseCase
import com.ih.osm.domain.usecase.cilt.StartSequenceExecutionUseCase
import com.ih.osm.domain.usecase.cilt.StopSequenceExecutionUseCase
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
    private val startSequenceExecutionUseCase: StartSequenceExecutionUseCase,
    private val stopSequenceExecutionUseCase: StopSequenceExecutionUseCase,
    private val notificationManager: NotificationManager,
    private val authRepository: AuthRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository,
    @ApplicationContext private val context: Context,
) : BaseViewModel<CiltRoutineViewModel.UiState>(UiState()) {
    private val pendingEvidences = mutableListOf<Evidence>()

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
        val isSequenceFinished: Boolean = false,
        val isUploadingEvidence: Boolean = false,
    )

    fun handleGetCilts() {
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
                notificationManager.buildNotificationSequenceStarted()
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
                setState { copy(isUploadingEvidence = true) }
                uploadPendingEvidences(executionId)
                setState {
                    copy(isUploadingEvidence = false, isSequenceFinished = true)
                }
                notificationManager.buildNotificationSequenceFinished()
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

    fun addLocalEvidence(
        executionId: Int,
        uri: Uri,
    ) {
        val evidence =
            Evidence.fromCreateEvidence(
                cardId = executionId.toString(),
                url = uri.toString(),
                type = EvidenceType.IMCR.name,
            )
        pendingEvidences.add(evidence)
    }

    private suspend fun uploadPendingEvidences(executionId: Int) {
        val createdAt = getCurrentDateTimeUtc()

        for (evidence in pendingEvidences) {
            val uploadedUrl = firebaseStorageRepository.uploadEvidence(evidence)

            if (uploadedUrl.isNotEmpty()) {
                val request =
                    CiltEvidenceRequest(
                        executionId = executionId,
                        evidenceUrl = uploadedUrl,
                        createdAt = createdAt,
                    )
                kotlin.runCatching {
                    callUseCase { createCiltEvidenceUseCase(request) }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                }
            }
        }
        pendingEvidences.clear()
    }

    fun getSequenceById(sequenceId: Int): Sequence? {
        val execution =
            state.value.ciltData?.positions
                ?.flatMap { it.ciltMasters }
                ?.flatMap { it.sequences }
                ?.find { it.id == sequenceId }
        Log.e("test", "Execution $execution")
        return execution
    }

    fun resetSequenceFinishedFlag() {
        setState { copy(isSequenceFinished = false) }
    }
}
