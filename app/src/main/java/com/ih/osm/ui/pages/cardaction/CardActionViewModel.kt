package com.ih.osm.ui.pages.cardaction

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ih.osm.R
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.core.notifications.NotificationManager
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.CardType
import com.ih.osm.domain.model.Employee
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.EvidenceType
import com.ih.osm.domain.model.toAudios
import com.ih.osm.domain.model.toImages
import com.ih.osm.domain.model.toVideos
import com.ih.osm.domain.usecase.card.GetCardDetailUseCase
import com.ih.osm.domain.usecase.card.SaveCardSolutionUseCase
import com.ih.osm.domain.usecase.card.UpdateCardMechanicUseCase
import com.ih.osm.domain.usecase.cardtype.GetCardTypeUseCase
import com.ih.osm.domain.usecase.employee.GetEmployeesUseCase
import com.ih.osm.ui.components.card.actions.CardItemSheetAction
import com.ih.osm.ui.components.card.actions.toCardItemSheetAction
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.extensions.defaultIfNull
import com.ih.osm.ui.navigation.ARG_ACTION_TYPE
import com.ih.osm.ui.navigation.ARG_CARD_ID
import com.ih.osm.ui.pages.cardaction.action.CardAction
import com.ih.osm.ui.utils.DEFINITIVE_SOLUTION
import com.ih.osm.ui.utils.EMPTY
import com.ih.osm.ui.utils.PROVISIONAL_SOLUTION
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardActionViewModel
    @Inject
    constructor(
        private val getEmployeesUseCase: GetEmployeesUseCase,
        private val getCardDetailUseCase: GetCardDetailUseCase,
        private val getCardTypeUseCase: GetCardTypeUseCase,
        private val saveCardSolutionUseCase: SaveCardSolutionUseCase,
        private val notificationManager: NotificationManager,
        private val updateCardMechanicUseCase: UpdateCardMechanicUseCase,
        @ApplicationContext private val context: Context,
        savedStateHandle: SavedStateHandle,
    ) : BaseViewModel<CardActionViewModel.UiState>(UiState()) {
        data class UiState(
            val message: String = EMPTY,
            val isLoading: Boolean = false,
            val employeeList: List<Employee> = emptyList(),
            val selectedEmployee: Employee? = null,
            val filteredEmployeeList: List<Employee> = emptyList(),
            val comments: String = EMPTY,
            val evidences: List<Evidence> = emptyList(),
            val card: Card? = null,
            val cardType: CardType? = null,
            val isActionSuccess: Boolean = false,
            val actionType: CardItemSheetAction? = null,
            val screenTitle: String = EMPTY,
            val isContentEnabled: Boolean = false,
        )

        init {
            val uuid = savedStateHandle.get<String>(ARG_CARD_ID).orEmpty()
            savedStateHandle.get<String>(ARG_ACTION_TYPE)?.toCardItemSheetAction()?.let {
                setState {
                    copy(
                        actionType = it,
                        screenTitle = getScreenTitle(it),
                        isContentEnabled = it != CardItemSheetAction.AssignMechanic,
                    )
                }
            }
            handleGetCardDetail(uuid)
        }

        fun process(action: CardAction) {
            when (action) {
                is CardAction.SearchEmployee -> handleOnSearchEmployee(action.query)
                is CardAction.SetEmployee -> setState { copy(selectedEmployee = action.employee) }
                is CardAction.SetComment -> setState { copy(comments = action.comment) }
                is CardAction.AddEvidence -> handleAddEvidence(action.uri, action.type)
                is CardAction.DeleteEvidence -> handleDeleteEvidence(action.evidence)
                is CardAction.Save -> handleSaveAction()
            }
        }

        private fun handleAddEvidence(
            uri: Uri,
            type: EvidenceType,
        ) {
            viewModelScope.launch {
                val state = getState()
                val cardType =
                    state.cardType.defaultIfNull(getCardTypeUseCase(state.card?.cardTypeId.orEmpty()))
                val actionType = getState().actionType ?: return@launch
                val errorMessage =
                    when (type) {
                        EvidenceType.IMCL, EvidenceType.IMPS -> {
                            val maxImages = imagesQuantity(actionType, cardType)
                            if ((state.evidences.toImages().size) == maxImages) {
                                context.getString(R.string.limit_images)
                            } else {
                                EMPTY
                            }
                        }

                        EvidenceType.VICL, EvidenceType.VIPS -> {
                            val maxVideos = videoQuantity(actionType, cardType)
                            val maxVideoDuration = videoDuration(actionType, cardType) * 1000
                            val duration = fileHelper.getDuration(uri)
                            when {
                                state.evidences.toVideos().size == maxVideos -> context.getString(R.string.limit_videos)
                                duration > maxVideoDuration -> context.getString(R.string.limit_video_duration)
                                else -> EMPTY
                            }
                        }

                        EvidenceType.AUCL, EvidenceType.AUPS -> {
                            val maxAudios = audiosQuantity(actionType, cardType)
                            val maxAudioDuration = audiosDuration(actionType, cardType)
                            val duration = fileHelper.getDuration(uri)
                            when {
                                state.evidences.toAudios().size == maxAudios -> context.getString(R.string.limit_audios)
                                duration > maxAudioDuration -> context.getString(R.string.limit_audio_duration)
                                else -> EMPTY
                            }
                        }

                        else -> EMPTY
                    }
                if (errorMessage.isNotEmpty()) {
                    setState { copy(message = errorMessage) }
                    return@launch
                }

                val list = state.evidences.toMutableList()
                list.add(
                    Evidence.fromCreateEvidence(
                        cardId = state.card?.uuid.orEmpty(),
                        url = uri.toString(),
                        type = type.name,
                    ),
                )
                setState { copy(evidences = list, cardType = cardType) }
            }
        }

        private fun imagesQuantity(
            actionType: CardItemSheetAction,
            cardType: CardType?,
        ): Int {
            return when (actionType) {
                is CardItemSheetAction.ProvisionalSolution -> cardType?.quantityImagesPs
                is CardItemSheetAction.DefinitiveSolution -> cardType?.quantityImagesClose
                else -> 0
            }.defaultIfNull(0)
        }

        private fun videoQuantity(
            actionType: CardItemSheetAction,
            cardType: CardType?,
        ): Int {
            return when (actionType) {
                is CardItemSheetAction.ProvisionalSolution -> cardType?.quantityVideosPs
                is CardItemSheetAction.DefinitiveSolution -> cardType?.quantityVideosClose
                else -> 0
            }.defaultIfNull(0)
        }

        private fun audiosQuantity(
            actionType: CardItemSheetAction,
            cardType: CardType?,
        ): Int {
            return when (actionType) {
                is CardItemSheetAction.ProvisionalSolution -> cardType?.quantityAudiosPs
                is CardItemSheetAction.DefinitiveSolution -> cardType?.quantityAudiosClose
                else -> 0
            }.defaultIfNull(0)
        }

        private fun audiosDuration(
            actionType: CardItemSheetAction,
            cardType: CardType?,
        ): Int {
            return when (actionType) {
                is CardItemSheetAction.ProvisionalSolution -> cardType?.audiosDurationPs
                is CardItemSheetAction.DefinitiveSolution -> cardType?.audiosDurationClose
                else -> 0
            }.defaultIfNull(0)
        }

        private fun videoDuration(
            actionType: CardItemSheetAction,
            cardType: CardType?,
        ): Int {
            return when (actionType) {
                is CardItemSheetAction.ProvisionalSolution -> cardType?.videosDurationPs
                is CardItemSheetAction.DefinitiveSolution -> cardType?.videosDurationClose
                else -> 0
            }.defaultIfNull(0)
        }

//
        private fun handleDeleteEvidence(evidence: Evidence) {
            viewModelScope.launch {
                val state = getState()
                val list = state.evidences.filter { it.id != evidence.id }
                setState { copy(evidences = list) }
            }
        }

//
        private fun handleSaveAction() {
            viewModelScope.launch {
                setState { copy(isLoading = true) }
                when (getState().actionType) {
                    CardItemSheetAction.AssignMechanic -> handleOnSaveMechanic()
                    CardItemSheetAction.DefinitiveSolution,
                    CardItemSheetAction.ProvisionalSolution,
                    -> handleSaveSolution()
                    else -> return@launch
                }
            }
        }

//
        private fun handleOnSaveMechanic() {
            setState { copy(isLoading = true) }
            viewModelScope.launch {
                val state = getState()
                if (NetworkConnection.isConnected().not()) {
                    setState {
                        copy(
                            isLoading = false,
                            message = context.getString(R.string.please_connect_to_internet),
                        )
                    }
                    return@launch
                }
                kotlin.runCatching {
                    callUseCase {
                        updateCardMechanicUseCase(
                            mechanicId = state.selectedEmployee?.id.orEmpty(),
                            uuid = state.card?.uuid.orEmpty(),
                        )
                    }
                }.onSuccess {
                    setState { copy(isActionSuccess = true) }
                    buildNotification()
                    cleanScreenStates()
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    cleanScreenStates(it.localizedMessage.orEmpty())
                }
            }
        }

//
        private fun handleSaveSolution() {
            setState { copy(isLoading = true) }
            viewModelScope.launch {
                val state = getState()
                if (state.selectedEmployee == null) {
                    setState {
                        copy(
                            isLoading = false,
                            message = context.getString(R.string.please_select_a_user),
                        )
                    }
                    return@launch
                }
                val actionType =
                    if (state.actionType == CardItemSheetAction.DefinitiveSolution) {
                        DEFINITIVE_SOLUTION
                    } else {
                        PROVISIONAL_SOLUTION
                    }
                kotlin.runCatching {
                    callUseCase {
                        saveCardSolutionUseCase(
                            solutionType = actionType,
                            cardId = state.card?.uuid.toString(),
                            comments = state.comments,
                            userSolutionId = state.selectedEmployee.id,
                            evidences = state.evidences,
                            saveLocal = true,
                        )
                    }
                }.onSuccess {
                    Log.e("Test", "Solution Success $it")
                    setState { copy(isActionSuccess = true) }
                    buildNotification()
                    cleanScreenStates()
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    cleanScreenStates(it.localizedMessage.orEmpty())
                }
            }
        }

//
        private fun buildNotification() {
            viewModelScope.launch {
                val title =
                    when (getState().actionType) {
                        is CardItemSheetAction.ProvisionalSolution -> {
                            context.getString(R.string.provisional_solution)
                        }
                        is CardItemSheetAction.DefinitiveSolution -> {
                            context.getString(R.string.definitive_solution)
                        }
                        is CardItemSheetAction.AssignMechanic -> {
                            context.getString(R.string.assign_mechanic)
                        }
                        else -> EMPTY
                    }
                val description = context.getString(R.string.success_update)
                notificationManager.buildNotification(
                    title,
                    description,
                )
            }
        }

        private fun handleOnSearchEmployee(query: String) {
            viewModelScope.launch {
                val state = getState()
                val resultList =
                    state.employeeList.filter { it.name.lowercase().contains(query.lowercase()) }
                setState { copy(filteredEmployeeList = resultList) }
            }
        }

        private fun handleGetCardDetail(uuid: String) {
            Log.e("test", "CardID -> $uuid")
            setState { copy(isLoading = true) }
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { getCardDetailUseCase(uuid, false) }
                }.onSuccess {
                    setState { copy(card = it) }
                    handleGetCardType(it.cardTypeId.orEmpty())
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    cleanScreenStates(it.localizedMessage.orEmpty())
                }
            }
        }

        private fun handleGetCardType(cardTypeId: String) {
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { getCardTypeUseCase(cardTypeId) }
                }.onSuccess {
                    setState {
                        copy(
                            cardType = cardType,
                        )
                    }
                    handleGetEmployees()
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    cleanScreenStates(it.localizedMessage.orEmpty())
                }
            }
        }

        private fun handleGetEmployees() {
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { getEmployeesUseCase() }
                }.onSuccess {
                    setState { copy(employeeList = it) }
                    cleanScreenStates()
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    cleanScreenStates(it.localizedMessage.orEmpty())
                }
            }
        }

        private fun getScreenTitle(action: CardItemSheetAction): String {
            return when (action) {
                is CardItemSheetAction.AssignMechanic -> context.getString(R.string.assign_mechanic)
                is CardItemSheetAction.DefinitiveSolution -> context.getString(R.string.definitive_solution)
                is CardItemSheetAction.ProvisionalSolution -> context.getString(R.string.provisional_solution)
            }
        }

        private fun cleanScreenStates(message: String = EMPTY) {
            setState { copy(isLoading = false, message = message) }
        }

        fun cleanMessage() {
            cleanScreenStates()
        }
    }
