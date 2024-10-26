package com.ih.osm.ui.pages.solution

import android.content.Context
import android.net.Uri
import android.util.Log
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.osm.R
import com.ih.osm.core.file.FileHelper
import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.core.notifications.NotificationManager
import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.CardType
import com.ih.osm.domain.model.Employee
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.EvidenceType
import com.ih.osm.domain.model.NetworkStatus
import com.ih.osm.domain.model.toAudios
import com.ih.osm.domain.model.toImages
import com.ih.osm.domain.model.toVideos
import com.ih.osm.domain.usecase.card.GetCardDetailUseCase
import com.ih.osm.domain.usecase.card.SaveCardSolutionUseCase
import com.ih.osm.domain.usecase.card.UpdateCardMechanicUseCase
import com.ih.osm.domain.usecase.cardtype.GetCardTypeUseCase
import com.ih.osm.domain.usecase.employee.GetEmployeesUseCase
import com.ih.osm.ui.extensions.defaultIfNull
import com.ih.osm.ui.utils.ASSIGN_CARD_ACTION
import com.ih.osm.ui.utils.DEFINITIVE_SOLUTION
import com.ih.osm.ui.utils.EMPTY
import com.ih.osm.ui.utils.PROVISIONAL_SOLUTION
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SolutionViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val coroutineContext: CoroutineContext,
    private val getCardDetailUseCase: GetCardDetailUseCase,
    private val getCardTypeUseCase: GetCardTypeUseCase,
    private val saveCardSolutionUseCase: SaveCardSolutionUseCase,
    @ApplicationContext private val context: Context,
    private val fileHelper: FileHelper,
    private val notificationManager: NotificationManager,
    private val updateCardMechanicUseCase: UpdateCardMechanicUseCase,
    private val sharedPreferences: SharedPreferences
) : MavericksViewModel<SolutionViewModel.UiState>(initialState) {
    data class UiState(
        val solutionType: String = EMPTY,
        val message: String = EMPTY,
        val isLoading: Boolean = false,
        val employeeList: List<Employee> = emptyList(),
        val isSearching: Boolean = false,
        val query: String = EMPTY,
        val selectedEmployee: Employee? = null,
        val resultList: List<Employee> = emptyList(),
        val comments: String = EMPTY,
        val audioDuration: Int = 0,
        val evidences: List<Evidence> = emptyList(),
        val card: Card? = null,
        val cardType: CardType? = null,
        val isSolutionSuccess: Boolean = false,
        val isFetching: Boolean = false,
        val isEvidenceEnabled: Boolean = true,
        val isCommentsEnabled: Boolean = true
    ) : MavericksState

    sealed class Action {
        data class SetSolutionInfo(val solutionType: String, val cardId: String) : Action()

        data object GetEmployees : Action()

        data class OnSearchEmployee(val query: String) : Action()

        data class OnSelectEmployee(val employee: Employee) : Action()

        data class OnCommentChange(val comment: String) : Action()

        data object OnSave : Action()

        data class OnAddEvidence(val uri: Uri, val type: EvidenceType) : Action()

        data class OnDeleteEvidence(val evidence: Evidence) : Action()

        data class GetCardDetail(val cardId: String) : Action()

        data class GetCardType(val id: String) : Action()

        data object ClearMessage : Action()
    }

    fun process(action: Action) {
        when (action) {
            is Action.SetSolutionInfo -> handleSetSolutionInfo(action.solutionType, action.cardId)
            is Action.GetEmployees -> handleGetEmployees()
            is Action.OnSearchEmployee -> handleOnSearchEmployee(action.query)
            is Action.OnSelectEmployee -> handleOnSelectEmployee(action.employee)
            is Action.OnCommentChange -> handleOnCommentChange(action.comment)
            is Action.OnSave -> handleAction()
            is Action.OnAddEvidence -> handleOnAddEvidence(action.uri, action.type)
            is Action.OnDeleteEvidence -> handleOnDeleteEvidence(action.evidence)
            is Action.GetCardDetail -> handleGetCardDetail(action.cardId)
            is Action.GetCardType -> handleGetCardType(action.id)
            is Action.ClearMessage -> setState { copy(message = EMPTY) }
        }
    }

    private fun handleOnAddEvidence(uri: Uri, type: EvidenceType) {
        viewModelScope.launch {
            val state = stateFlow.first()
            val cardType =
                state.cardType.defaultIfNull(getCardTypeUseCase(state.card?.cardTypeId.orEmpty()))
            val errorMessage =
                when (type) {
                    EvidenceType.IMCL, EvidenceType.IMPS -> {
                        val maxImages = imagesQuantity(state.solutionType, cardType)
                        if ((state.evidences.toImages().size) == maxImages) {
                            context.getString(R.string.limit_images)
                        } else {
                            EMPTY
                        }
                    }

                    EvidenceType.VICL, EvidenceType.VIPS -> {
                        val maxVideos = videoQuantity(state.solutionType, cardType)
                        if (state.evidences.toVideos().size == maxVideos) {
                            context.getString(R.string.limit_videos)
                        } else {
                            EMPTY
                        }
                    }

                    EvidenceType.AUCL, EvidenceType.AUPS -> {
                        val maxAudios = audiosQuantity(state.solutionType, cardType)
                        if (state.evidences.toAudios().size == maxAudios) {
                            context.getString(R.string.limit_audios)
                        } else {
                            EMPTY
                        }
                    }

                    else -> EMPTY
                }
            if (errorMessage.isNotEmpty()) {
                setState { copy(message = errorMessage, isLoading = false) }
                return@launch
            }

            val list = state.evidences.toMutableList()
            list.add(
                Evidence.fromCreateEvidence(
                    cardId = state.card?.uuid.orEmpty(),
                    url = uri.toString(),
                    type = type.name
                )
            )
            setState { copy(evidences = list, cardType = cardType) }
        }
    }

    private fun imagesQuantity(solutionType: String, cardType: CardType?): Int {
        return if (solutionType == DEFINITIVE_SOLUTION) {
            cardType?.quantityImagesClose
        } else {
            cardType?.quantityImagesPs
        }.defaultIfNull(0)
    }

    private fun videoQuantity(solutionType: String, cardType: CardType?): Int {
        return if (solutionType == DEFINITIVE_SOLUTION) {
            cardType?.quantityVideosClose
        } else {
            cardType?.quantityVideosPs
        }.defaultIfNull(0)
    }

    private fun audiosQuantity(solutionType: String, cardType: CardType?): Int {
        return if (solutionType == DEFINITIVE_SOLUTION) {
            cardType?.quantityAudiosClose
        } else {
            cardType?.quantityAudiosPs
        }.defaultIfNull(0)
    }

    private fun handleOnDeleteEvidence(evidence: Evidence) {
        viewModelScope.launch {
            val state = stateFlow.first()
            val list = state.evidences.filter { it.id != evidence.id }
            setState { copy(evidences = list) }
        }
    }

    private fun handleAction() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val state = stateFlow.first()
            val networkStatus = NetworkConnection.networkStatus(context)
            if (networkStatus == NetworkStatus.DATA_CONNECTED &&
                sharedPreferences.getNetworkPreference().isEmpty()
            ) {
                setState {
                    copy(
                        isLoading = false,
                        message = context.getString(R.string.network_preferences_allowed)
                    )
                }
                return@launch
            }
            Log.e("test", "Aqui continue")
            if (state.solutionType == ASSIGN_CARD_ACTION) {
                handleOnSaveMechanic()
            } else {
                handleOnSave()
            }
        }
    }

    private fun handleOnSaveMechanic() {
        setState { copy(isLoading = true, message = context.getString(R.string.assigned_mechanic)) }
        viewModelScope.launch(coroutineContext) {
            val state = stateFlow.first()
            if (NetworkConnection.isConnected().not()) {
                setState {
                    copy(
                        isLoading = false,
                        message = context.getString(R.string.please_connect_to_internet)
                    )
                }
                return@launch
            }
            kotlin.runCatching {
                updateCardMechanicUseCase(
                    mechanicId = state.selectedEmployee?.id.orEmpty(),
                    cardId = state.card?.id.orEmpty()
                )
            }.onSuccess {
                setState { copy(isSolutionSuccess = true) }
                buildNotification()
                cleanScreenStates()
            }.onFailure {
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }

    private fun handleOnSave() {
        setState { copy(isLoading = true, message = context.getString(R.string.saving_solution)) }
        viewModelScope.launch(coroutineContext) {
            val state = stateFlow.first()
            if (state.selectedEmployee == null) {
                setState {
                    copy(
                        isLoading = false,
                        message = context.getString(R.string.please_select_a_user)
                    )
                }
                return@launch
            }
            kotlin.runCatching {
                saveCardSolutionUseCase(
                    solutionType = state.solutionType,
                    cardId = state.card?.uuid.toString(),
                    comments = state.comments,
                    userSolutionId = state.selectedEmployee.id,
                    evidences = state.evidences,
                    saveLocal = true
                )
            }.onSuccess {
                Log.e("Test", "Solution Success $it")
                setState { copy(isSolutionSuccess = true) }
                buildNotification()
                cleanScreenStates()
            }.onFailure {
                fileHelper.logException(it)
                Log.e("Test", "Solution Failure ${it.localizedMessage}")
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }

    private fun buildNotification() {
        viewModelScope.launch {
            val state = stateFlow.first()
            val title =
                when (state.solutionType) {
                    PROVISIONAL_SOLUTION -> {
                        context.getString(R.string.provisional_solution)
                    }
                    DEFINITIVE_SOLUTION -> {
                        context.getString(R.string.definitive_solution)
                    }
                    else -> {
                        context.getString(R.string.assign_mechanic)
                    }
                }
            val description = context.getString(R.string.success_update)
            notificationManager.buildNotification(
                title,
                description
            )
        }
    }

    private fun handleOnCommentChange(comment: String) {
        setState { copy(comments = comment) }
    }

    private fun handleOnSelectEmployee(employee: Employee) {
        setState {
            copy(
                selectedEmployee = employee,
                query = employee.name,
                resultList = emptyList()
            )
        }
    }

    private fun handleOnSearchEmployee(query: String) {
        viewModelScope.launch {
            val state = stateFlow.first()
            val resultList =
                state.employeeList.filter { it.name.lowercase().contains(query.lowercase()) }
            setState { copy(query = query, resultList = resultList) }
        }
    }

    private fun handleSetSolutionInfo(solutionType: String, cardId: String) {
        setState {
            copy(
                solutionType = solutionType,
                isFetching = true,
                isEvidenceEnabled = solutionType != ASSIGN_CARD_ACTION,
                isCommentsEnabled = solutionType != ASSIGN_CARD_ACTION
            )
        }
        handleGetCardDetail(cardId)
    }

    private fun handleGetCardDetail(cardId: String) {
        Log.e("test", "CardID -> $cardId")
        setState { copy(isLoading = true, message = context.getString(R.string.loading_data)) }
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getCardDetailUseCase(cardId, false)
            }.onSuccess {
                setState { copy(card = it) }
                handleGetCardType(it.cardTypeId.orEmpty())
            }.onFailure {
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }

    private fun handleGetCardType(cardTypeId: String) {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getCardTypeUseCase(cardTypeId)
            }.onSuccess {
                val audioDuration =
                    if (stateFlow.first().solutionType == DEFINITIVE_SOLUTION) {
                        it?.audiosDurationClose
                    } else {
                        it?.audiosDurationPs
                    }.defaultIfNull(0)
                Log.e("test", "Card Type $it")
                setState {
                    copy(
                        cardType = cardType,
                        audioDuration = audioDuration
                    )
                }
                handleGetEmployees()
            }.onFailure {
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }

    private fun handleGetEmployees() {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getEmployeesUseCase()
            }.onSuccess {
                setState { copy(employeeList = it) }
                cleanScreenStates()
            }.onFailure {
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }

    private fun cleanScreenStates(message: String = EMPTY) {
        setState { copy(isLoading = false, message = message) }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<SolutionViewModel, UiState> {
        override fun create(state: UiState): SolutionViewModel
    }

    companion object :
        MavericksViewModelFactory<SolutionViewModel, UiState> by hiltMavericksViewModelFactory()
}
