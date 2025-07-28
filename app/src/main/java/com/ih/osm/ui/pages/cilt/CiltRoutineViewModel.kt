package com.ih.osm.ui.pages.cilt

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.ih.osm.R
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.core.network.NetworkConnectionStatus
import com.ih.osm.core.notifications.NotificationManager
import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.data.model.StartSequenceExecutionRequest
import com.ih.osm.data.model.StopSequenceExecutionRequest
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.EvidenceParentType
import com.ih.osm.domain.model.EvidenceType
import com.ih.osm.domain.model.Execution
import com.ih.osm.domain.model.NetworkStatus
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.firebase.FirebaseStorageRepository
import com.ih.osm.domain.usecase.card.SyncCardUseCase
import com.ih.osm.domain.usecase.cilt.CreateCiltEvidenceUseCase
import com.ih.osm.domain.usecase.cilt.GetCiltsUseCase
import com.ih.osm.domain.usecase.cilt.GetOplByIdUseCase
import com.ih.osm.domain.usecase.cilt.StartSequenceExecutionUseCase
import com.ih.osm.domain.usecase.cilt.StopSequenceExecutionUseCase
import com.ih.osm.domain.usecase.level.GetLevelsUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.extensions.getCurrentDate
import com.ih.osm.ui.extensions.getCurrentDateTimeUtc
import com.ih.osm.ui.pages.cilt.action.CiltAction
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
        private val getLevelsUseCase: GetLevelsUseCase,
        private val createCiltEvidenceUseCase: CreateCiltEvidenceUseCase,
        private val syncCardUseCase: SyncCardUseCase,
        private val startSequenceExecutionUseCase: StartSequenceExecutionUseCase,
        private val stopSequenceExecutionUseCase: StopSequenceExecutionUseCase,
        private val authRepository: AuthRepository,
        private val notificationManager: NotificationManager,
        private val firebaseStorageRepository: FirebaseStorageRepository,
        private val sharedPreferences: SharedPreferences,
        @ApplicationContext private val context: Context,
    ) : BaseViewModel<CiltRoutineViewModel.UiState>(UiState()) {
        private val pendingEvidences = mutableListOf<Evidence>()

        init {
            handleGetCilts()

            NetworkConnection.initObserve(
                object : NetworkConnectionStatus {
                    override fun onNetworkChange(networkStatus: NetworkStatus) {
                        setState { copy(networkStatus = networkStatus) }
                    }
                },
            )
        }

        var isStarted = mutableStateOf(false)
            private set
        var isFinished = mutableStateOf(false)
            private set
        var parameterFound = mutableStateOf("")
            private set
        var finalParameter = mutableStateOf("")
            private set
        var isParameterOk = mutableStateOf(true)
            private set
        var isEvidenceAtCreation = mutableStateOf(false)
            private set
        var isEvidenceAtFinal = mutableStateOf(false)
            private set
        val evidenceUrisBefore = mutableStateListOf<Uri>()
        val evidenceUrisAfter = mutableStateListOf<Uri>()

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
            val superiorId: String? = null,
            val networkStatus: NetworkStatus = NetworkStatus.NO_INTERNET_ACCESS,
        )

        private fun handleGetCilts() {
            viewModelScope.launch {
                setState { copy(isLoading = true) }

                val date = getCurrentDate()

                kotlin.runCatching {
                    callUseCase { getCiltsUseCase(date) }
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

        private fun getOplById(id: String) {
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

        private fun getRemediationOplById(id: String) {
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

        private fun startSequenceExecution(executionId: Int) {
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

        private fun stopSequenceExecution(
            executionId: Int,
            initialParameter: String,
            evidenceAtCreation: Boolean,
            finalParameter: String,
            evidenceAtFinal: Boolean,
            nok: Boolean,
        ) {
            viewModelScope.launch {
                val state = getState()

                if (!NetworkConnection.isConnected() ||
                    state.networkStatus == NetworkStatus.NO_INTERNET_ACCESS ||
                    state.networkStatus == NetworkStatus.WIFI_DISCONNECTED ||
                    state.networkStatus == NetworkStatus.DATA_DISCONNECTED
                ) {
                    setState {
                        copy(message = context.getString(R.string.please_connect_to_internet))
                    }
                    return@launch
                }

                if (state.networkStatus == NetworkStatus.DATA_CONNECTED &&
                    sharedPreferences.getNetworkPreference().isEmpty()
                ) {
                    setState {
                        copy(message = context.getString(R.string.network_preferences_allowed))
                    }
                    return@launch
                }

                setState { copy(isLoading = true) }

                var remoteCardId = 0
                val localCard = sharedPreferences.getCiltCard()

                if (localCard != null) {
                    kotlin.runCatching {
                        callUseCase { syncCardUseCase(localCard) }
                    }.onSuccess { syncedCard ->
                        remoteCardId = syncedCard?.id?.toIntOrNull() ?: 0
                    }.onFailure {
                        LoggerHelperManager.logException(it)
                        setState {
                            copy(
                                isLoading = false,
                                message = context.getString(R.string.error_syncing_card),
                            )
                        }
                        return@launch
                    }
                }

                if (nok && remoteCardId == 0) {
                    setState {
                        copy(
                            isLoading = false,
                            message = context.getString(R.string.error_card_required),
                        )
                    }
                    return@launch
                }

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
                        amTagId = if (nok) remoteCardId else 0,
                    )

                kotlin.runCatching {
                    callUseCase { stopSequenceExecutionUseCase(request) }
                }.onSuccess {
                    sharedPreferences.removeCiltCard()
                    setState { copy(isUploadingEvidence = true) }
                    uploadPendingEvidences(executionId)
                    setState {
                        copy(isUploadingEvidence = false, isSequenceFinished = true)
                    }
                    resetExecutionState()
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

        private suspend fun uploadPendingEvidences(executionId: Int) {
            val createdAt = getCurrentDateTimeUtc()

            for (evidence in pendingEvidences) {
                val uploadedUrl = firebaseStorageRepository.uploadEvidence(evidence)

                if (uploadedUrl.isNotEmpty()) {
                    val request =
                        CiltEvidenceRequest(
                            executionId = executionId,
                            evidenceUrl = uploadedUrl,
                            type = evidence.type,
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

        fun getExecutionById(executionId: Int): Execution? {
            return state.value.ciltData?.positions
                ?.flatMap { it.ciltMasters }
                ?.flatMap { it.sequences }
                ?.flatMap { it.executions }
                ?.find { it.id == executionId }
        }

        fun getSuperiorIdFromExecutionLevelId(
            executionId: Int,
            onResult: (String?) -> Unit = {},
        ) {
            viewModelScope.launch {
                // Retrieves the execution object by its ID
                val execution = getExecutionById(executionId)
                // Gets the levelId from the execution
                val levelId = execution?.levelId
                // If the levelId is null, sets superiorId to null and returns early
                if (levelId == null) {
                    setState { copy(superiorId = null) }
                    onResult(null)
                    return@launch
                }

                kotlin.runCatching {
                    getLevelsUseCase()
                }.onSuccess { levels ->
                    // Convert levelId to String before comparison
                    // Finds the level that matches the level ID
                    val matchingLevel = levels.find { it.id == levelId.toString() }
                    // Retrieves the superior ID from the matching level
                    val id = matchingLevel?.superiorId
                    // Updates the ViewModel state and returns the result via the callback
                    setState { copy(superiorId = id) }
                    onResult(id)
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    setState { copy(superiorId = null) }
                    onResult(null)
                }
            }
        }

        fun resetSequenceFinishedFlag() {
            setState { copy(isSequenceFinished = false) }
        }

        fun process(action: CiltAction) {
            when (action) {
                is CiltAction.GetCilts -> handleGetCilts()
                is CiltAction.StartExecution -> {
                    startSequenceExecution(action.executionId)
                    isStarted.value = true
                }

                is CiltAction.StopExecution -> {
                    stopSequenceExecution(
                        executionId = action.executionId,
                        initialParameter = parameterFound.value,
                        evidenceAtCreation = isEvidenceAtCreation.value,
                        finalParameter = finalParameter.value,
                        evidenceAtFinal = isEvidenceAtFinal.value,
                        nok = !isParameterOk.value,
                    )
                    isFinished.value = true
                }

                is CiltAction.SetParameterFound -> parameterFound.value = action.value
                is CiltAction.SetFinalParameter -> finalParameter.value = action.value
                is CiltAction.SetParameterOk -> isParameterOk.value = action.isOk
                is CiltAction.SetEvidenceAtCreation -> isEvidenceAtCreation.value = action.value
                is CiltAction.SetEvidenceAtFinal -> isEvidenceAtFinal.value = action.value
                is CiltAction.AddEvidenceBefore -> {
                    evidenceUrisBefore.add(action.uri)
                    addLocalEvidence(action.executionId, action.uri, type = EvidenceType.INITIAL.name)
                }

                is CiltAction.AddEvidenceAfter -> {
                    evidenceUrisAfter.add(action.uri)
                    addLocalEvidence(action.executionId, action.uri, type = EvidenceType.FINAL.name)
                }

                is CiltAction.RemoveEvidenceBefore -> {
                    evidenceUrisBefore.removeIf { it.toString() == action.url }
                    removeLocalEvidence(action.url)
                }

                is CiltAction.RemoveEvidenceAfter -> {
                    evidenceUrisAfter.removeIf { it.toString() == action.url }
                    removeLocalEvidence(action.url)
                }

                is CiltAction.GetOplById -> getOplById(action.id)
                is CiltAction.GetRemediationOplById -> getRemediationOplById(action.id)
                CiltAction.SetStarted -> isStarted.value = true
                CiltAction.SetFinished -> isFinished.value = true
                CiltAction.CleanMessage -> cleanMessage()
            }
        }

        private fun addLocalEvidence(
            executionId: Int,
            uri: Uri,
            type: String,
        ) {
            val evidence =
                Evidence.fromCreateEvidence(
                    cardId = executionId.toString(),
                    url = uri.toString(),
                    type = type,
                    parentType = EvidenceParentType.EXECUTION,
                )
            pendingEvidences.add(evidence)
        }

        private fun removeLocalEvidence(evidenceUrl: String) {
            pendingEvidences.removeIf { it.url == evidenceUrl }
        }

        fun cleanMessage() {
            setState { copy(message = EMPTY) }
        }

        private fun resetExecutionState() {
            isStarted.value = false
            isFinished.value = false
            parameterFound.value = ""
            finalParameter.value = ""
            isParameterOk.value = true
            isEvidenceAtCreation.value = false
            isEvidenceAtFinal.value = false
            evidenceUrisBefore.clear()
            evidenceUrisAfter.clear()
        }
    }
