package com.ih.osm.ui.pages.sequence

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.ih.osm.R
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.core.notifications.NotificationManager
import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.data.model.StartSequenceExecutionRequest
import com.ih.osm.data.model.StopSequenceExecutionRequest
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.EvidenceParentType
import com.ih.osm.domain.model.EvidenceType
import com.ih.osm.domain.model.Execution
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.model.Sequence
import com.ih.osm.domain.model.isValidExecution
import com.ih.osm.domain.usecase.card.DeleteCardUseCase
import com.ih.osm.domain.usecase.card.SyncCardUseCase
import com.ih.osm.domain.usecase.cilt.GetOplByIdUseCase
import com.ih.osm.domain.usecase.cilt.GetSequenceUseCase
import com.ih.osm.domain.usecase.cilt.StartSequenceExecutionUseCase
import com.ih.osm.domain.usecase.cilt.StopSequenceExecutionUseCase
import com.ih.osm.domain.usecase.level.GetLevelsUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.extensions.defaultIfNull
import com.ih.osm.ui.extensions.getCurrentDateTimeUtc
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SequenceViewModel
@Inject
constructor(
    private val getOplByIdUseCase: GetOplByIdUseCase,
    private val getSequenceUseCase: GetSequenceUseCase,
    private val startSequenceUseCase: StartSequenceExecutionUseCase,
    @ApplicationContext val context: Context,
    private val notificationManager: NotificationManager,
    private val getLevelsUSeCase: GetLevelsUseCase,
    private val sharedPreferences: SharedPreferences,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val syncCardUseCase: SyncCardUseCase,
    private val stopSequenceExecutionUseCase: StopSequenceExecutionUseCase,
) : BaseViewModel<SequenceViewModel.UiState>(UiState()) {
    data class UiState(
        val isLoading: Boolean = true,
        val evidences: List<Evidence> = emptyList(),
        val showBottomSheet: Boolean = false,
        val showBottomSheetRemediation: Boolean = false,
        val opl: Opl? = null,
        val oplRemediation: Opl? = null,
        val message: String = EMPTY,
        val sequence: Sequence? = null,
        val execution: Execution? = null,
        val bannerMessage: String = EMPTY,
        val enableStartButton: Boolean = false,
        val enableCompleteButton: Boolean = false,
        val enableStartExecution: Boolean = false,
        val duration: Int = 0,
        val initialParameter: String = "",
        val finalParameter: String = "",
        val isParamOk: Boolean = true,
        val superiorId: Int = 0,
        val card: Card? = null,
        val navigateBack: Boolean = false,
    )

    sealed class SequenceAction {
        data class AddEvidence(val uri: Uri, val type: EvidenceType) : SequenceAction()

        data class RemoveEvidence(val evidence: Evidence) : SequenceAction()

        data object GetOpl : SequenceAction()

        data object GetRemediationOpl : SequenceAction()

        data object DismissBottomSheet : SequenceAction()

        data object DismissBottomSheetRemediation : SequenceAction()

        data object CleanMessage : SequenceAction()

        data object StartSequence : SequenceAction()

        data class CompleteSequence(val initialParameter: String, val finalParameter: String) :
            SequenceAction()

        data object ToggleOkParam : SequenceAction()

        data object CleanSequenceInformation : SequenceAction()

        data class UpdateInitialParameter(val value: String) : SequenceAction()

        data class UpdateFinalParameter(val value: String) : SequenceAction()
    }

    fun process(action: SequenceAction) {
        when (action) {
            is SequenceAction.AddEvidence -> handleAddEvidence(action.uri, action.type)
            is SequenceAction.RemoveEvidence -> handleRemoveEvidence(action.evidence.url)
            is SequenceAction.GetOpl -> handleGetOpl()
            is SequenceAction.GetRemediationOpl -> handleGetRemediationOpl()
            is SequenceAction.DismissBottomSheet -> setState { copy(showBottomSheet = false) }
            is SequenceAction.DismissBottomSheetRemediation ->
                setState {
                    copy(
                        showBottomSheetRemediation = false,
                    )
                }

            is SequenceAction.CleanMessage -> setState { copy(message = EMPTY) }
            is SequenceAction.StartSequence -> handleStartSequence()
            is SequenceAction.ToggleOkParam -> {
                val isParamOk = getState().isParamOk.not()
                setState { copy(isParamOk = isParamOk) }
            }

            is SequenceAction.CompleteSequence -> handleCompleteSequence(
                action.initialParameter,
                action.finalParameter
            )

            is SequenceAction.CleanSequenceInformation -> cleanSequenceInformation()
            is SequenceAction.UpdateInitialParameter -> setState { copy(initialParameter = action.value) }
            is SequenceAction.UpdateFinalParameter -> setState { copy(finalParameter = action.value) }
        }
    }

    fun load(
        sequenceId: Int,
        executionId: Int,
    ) {
        handleGetSequence(sequenceId, executionId)
    }

    fun handleGetSequence(
        sequenceId: Int,
        executionId: Int,
    ) {
        viewModelScope.launch {
            if (getState().sequence != null && getState().execution != null) {
                setState { copy(isLoading = false) }
                handleCheckCard()
                return@launch
            }
            kotlin.runCatching {
                callUseCase { getSequenceUseCase(sequenceId) }
            }.onSuccess { sequence ->
                val execution = sequence.executions.find { it.id == executionId }
                val isValidExecution = true
                setState {
//                        copy(
//                            isLoading = false,
//                            sequence = sequence,
//                            execution = execution,
//                            bannerMessage = if (isValidExecution) EMPTY else execution?.validate(context)?.second.orEmpty(),
//                            enableStartButton = isValidExecution,
//                            enableStartExecution = isValidExecution && execution?.status != "A",
//                            isParamOk = execution?.nok.defaultIfNull(true),
//                        )
                    copy(
                        isLoading = false,
                        sequence = sequence,
                        execution = execution,
                        bannerMessage = EMPTY,
                        enableStartButton = isValidExecution,
                        enableStartExecution = isValidExecution,
                        isParamOk = execution?.nok.defaultIfNull(true),
                    )
                }
                handleGetLevels()
            }.onFailure {
                setState { copy(message = it.localizedMessage.orEmpty(), isLoading = false) }
            }
        }
    }

    fun handleAddEvidence(
        uri: Uri,
        type: EvidenceType,
    ) {
        viewModelScope.launch {
            val evidenceList = getState().evidences.toMutableList()
            evidenceList.add(
                Evidence.fromCreateEvidence(
                    cardId = getState().execution?.id.toString(),
                    url = uri.toString(),
                    type = type.name,
                    parentType = EvidenceParentType.EXECUTION,
                ),
            )
            setState { copy(evidences = evidenceList) }
        }
    }

    fun handleRemoveEvidence(url: String) {
        viewModelScope.launch {
            val evidenceList = getState().evidences.filter { it.url != url }
            setState { copy(evidences = evidenceList) }
        }
    }

    fun handleGetOpl() {
        viewModelScope.launch {
            if (getState().opl == null) {
                val oplId = getState().execution?.referenceOplSopId.toString()
                kotlin.runCatching {
                    callUseCase { getOplByIdUseCase(oplId) }
                }.onSuccess {
                    setState { copy(opl = it, showBottomSheet = true) }
                }.onFailure {
                    setState { copy(message = it.localizedMessage.orEmpty()) }
                }
            } else {
                setState { copy(showBottomSheet = true) }
            }
        }
    }

    fun handleGetRemediationOpl() {
        viewModelScope.launch {
            if (getState().oplRemediation == null) {
                val oplId = getState().execution?.remediationOplSopId.toString()
                kotlin.runCatching {
                    callUseCase { getOplByIdUseCase(oplId) }
                }.onSuccess {
                    setState { copy(oplRemediation = it, showBottomSheetRemediation = true) }
                }.onFailure {
                    setState { copy(message = it.localizedMessage.orEmpty()) }
                }
            } else {
                setState { copy(showBottomSheetRemediation = true) }
            }
        }
    }

    fun handleStartSequence() {
        viewModelScope.launch {
            if (NetworkConnection.isConnected().not()) {
                setState { copy(message = context.getString(R.string.no_internet_access)) }
                return@launch
            }
            setState { copy(enableStartButton = false) }

            kotlin.runCatching {
                callUseCase {
                    startSequenceUseCase(
                        StartSequenceExecutionRequest(
                            id = getState().execution?.id.defaultIfNull(0),
                            startDate = getCurrentDateTimeUtc(),
                        ),
                    )
                }
            }.onSuccess {
                setState {
                    copy(
                        enableStartButton = false,
                        enableCompleteButton = true,
                        duration = getDuration(),
                    )
                }
                notificationManager.buildNotificationSequenceStarted()
            }.onFailure {
                val isExecutionStarted =
                    it.localizedMessage.orEmpty().lowercase().contains(
                        "The CILT sequence has already been started".lowercase(),
                    )
                setState {
                    copy(
                        message =
                            context.getString(
                                R.string.error_starting_sequence,
                                it.localizedMessage.orEmpty(),
                            ),
                        enableStartButton = !isExecutionStarted,
                        enableCompleteButton = isExecutionStarted,
                        duration = getDuration(),
                    )
                }
            }
        }
    }

    private fun getDuration(): Int {
        var duration = getState().duration.defaultIfNull(0)
        duration =
            when (duration) {
                in 0 until 1800 -> 1800
                in 1800 until 3600 -> 3600 * 2
                else -> 3600 * 3
            }
        return duration
    }

    private fun handleGetLevels() {
        viewModelScope.launch {
            kotlin.runCatching {
                callUseCase { getLevelsUSeCase() }
            }.onSuccess { levels ->
                val levelId = getState().execution?.levelId.defaultIfNull(0).toString()
                setState {
                    copy(
                        superiorId =
                            levels.find { it.id == levelId }?.superiorId?.toInt()
                                .defaultIfNull(0),
                    )
                }
            }.onFailure {
                setState { copy(superiorId = 0) }
            }
        }
    }

    fun handleCompleteSequence(
        initialParameter: String,
        finalParameter: String,
    ) {
        viewModelScope.launch {
            val state = getState()
            setState { copy(isLoading = true) }

            if (NetworkConnection.isConnected().not()) {
                setState {
                    copy(
                        message = context.getString(R.string.please_connect_to_internet),
                        isLoading = false,
                    )
                }
                return@launch
            }

            var remoteCardId = 0

            if (state.card != null) {
                kotlin.runCatching {
                    callUseCase { syncCardUseCase(card = state.card) }
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

            if (state.isParamOk.not() && remoteCardId == 0) {
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
                    id = state.execution?.id.defaultIfNull(0),
                    stopDate = stopDate,
                    initialParameter = state.initialParameter,
                    evidenceAtCreation = state.evidences.any { it.type == EvidenceType.INITIAL.name },
                    finalParameter = state.finalParameter,
                    evidenceAtFinal = state.evidences.any { it.type == EvidenceType.FINAL.name },
                    nok = state.isParamOk,
                    amTagId = remoteCardId,
                )

            kotlin.runCatching {
                callUseCase { stopSequenceExecutionUseCase(request, state.evidences) }
            }.onSuccess {
                notificationManager.buildNotificationSequenceFinished()
                setState { copy(isLoading = false, navigateBack = true) }
            }.onFailure {
                LoggerHelperManager.logException(it)
                setState {
                    copy(
                        message =
                            context.getString(
                                R.string.error_stopping_sequence,
                                it.localizedMessage.orEmpty(),
                            ),
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun handleCheckCard() {
        viewModelScope.launch {
            val card = sharedPreferences.getCiltCard()
            setState { copy(card = card) }
        }
    }

    fun cleanSequenceInformation() {
        viewModelScope.launch {
            if (getState().card != null) {
                setState { copy(isLoading = true) }
                kotlin.runCatching {
                    callUseCase { deleteCardUseCase(getState().card?.uuid.orEmpty()) }
                }.onSuccess {
                    setState { copy(navigateBack = true) }
                }.onFailure {
                    setState { copy(navigateBack = true) }
                }
            } else {
                setState { copy(navigateBack = true) }
            }
        }
    }
}
