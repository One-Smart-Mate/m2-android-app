package com.ih.osm.ui.pages.cilt

import android.content.Context
import android.net.Uri
import android.util.Log
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
import com.ih.osm.data.model.GetCiltsRequest
import com.ih.osm.data.model.StartSequenceExecutionRequest
import com.ih.osm.data.model.StopSequenceExecutionRequest
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.EvidenceParentType
import com.ih.osm.domain.model.EvidenceType
import com.ih.osm.domain.model.NetworkStatus
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.model.Sequence
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
        private val notificationManager: NotificationManager,
        private val authRepository: AuthRepository,
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
                Log.d("CardSync", "Local card found: $localCard")

                if (localCard != null) {
                    kotlin.runCatching {
                        callUseCase { syncCardUseCase(localCard) }
                    }.onSuccess { syncedCard ->
                        remoteCardId = syncedCard?.id?.toIntOrNull() ?: 0
                        Log.d("CardSync", "Synced card: $syncedCard")
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

        fun addLocalEvidence(
            executionId: Int,
            uri: Uri,
        ) {
            val evidence =
                Evidence.fromCreateEvidence(
                    cardId = executionId.toString(),
                    url = uri.toString(),
                    type = EvidenceType.IMCR.name,
                    parentType = EvidenceParentType.EXECUTION,
                )
            pendingEvidences.add(evidence)
        }

        fun removeLocalEvidence(evidenceUrl: String) {
            pendingEvidences.removeIf { it.url == evidenceUrl }
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

        fun getSuperiorIdFromExecutionRoute(
            sequenceId: Int,
            onResult: (String?) -> Unit = {},
        ) {
            viewModelScope.launch {
                // Retrieves the sequence object by its ID
                val sequence = getSequenceById(sequenceId)
                // Retrieves the first execution associated with the sequence (if any)
                val execution = sequence?.executions?.firstOrNull()
                // Gets the route string from the execution
                val route = execution?.route
                // If the route is null or blank, sets superiorId to null and returns early
                if (route.isNullOrBlank()) {
                    setState { copy(superiorId = null) }
                    onResult(null)
                    return@launch
                }
                // Extracts the last node ID from the route
                val lastNodeId = route.split("/").last().trim()

                kotlin.runCatching {
                    getLevelsUseCase()
                }.onSuccess { levels ->
                    // Finds the level that matches the last node ID
                    val matchingLevel =
                        levels.find { it.name.trim().equals(lastNodeId.trim(), ignoreCase = true) }
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

        fun setStarted(value: Boolean) {
            isStarted.value = value
        }

        fun setFinished(value: Boolean) {
            isFinished.value = value
        }

        fun setParameterFound(value: String) {
            parameterFound.value = value
        }

        fun setFinalParameter(value: String) {
            finalParameter.value = value
        }

        fun setParameterOk(value: Boolean) {
            isParameterOk.value = value
        }

        fun setEvidenceAtCreation(value: Boolean) {
            isEvidenceAtCreation.value = value
        }

        fun setEvidenceAtFinal(value: Boolean) {
            isEvidenceAtFinal.value = value
        }

        fun addEvidenceBefore(uri: Uri) {
            evidenceUrisBefore.add(uri)
        }

        fun addEvidenceAfter(uri: Uri) {
            evidenceUrisAfter.add(uri)
        }

        fun removeEvidenceBefore(url: String) {
            evidenceUrisBefore.removeIf { it.toString() == url }
        }

        fun removeEvidenceAfter(url: String) {
            evidenceUrisAfter.removeIf { it.toString() == url }
        }

        fun resetExecutionState() {
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
