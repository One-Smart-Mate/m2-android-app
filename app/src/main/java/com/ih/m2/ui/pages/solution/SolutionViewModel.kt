package com.ih.m2.ui.pages.solution

import android.content.Context
import android.net.Uri
import android.util.Log
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.m2.R
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.CardType
import com.ih.m2.domain.model.Employee
import com.ih.m2.domain.model.Evidence
import com.ih.m2.domain.model.EvidenceType
import com.ih.m2.domain.model.toAudios
import com.ih.m2.domain.model.toImages
import com.ih.m2.domain.model.toVideos
import com.ih.m2.domain.usecase.card.GetCardDetailUseCase
import com.ih.m2.domain.usecase.card.SaveCardSolutionUseCase
import com.ih.m2.domain.usecase.cardtype.GetCardTypeUseCase
import com.ih.m2.domain.usecase.employee.GetEmployeesUseCase
import com.ih.m2.ui.extensions.defaultIfNull
import com.ih.m2.ui.utils.DEFINITIVE_SOLUTION
import com.ih.m2.ui.utils.EMPTY
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SolutionViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val coroutineContext: CoroutineContext,
    private val getCardDetailUseCase: GetCardDetailUseCase,
    private val getCardTypeUseCase: GetCardTypeUseCase,
    private val saveCardSolutionUseCase: SaveCardSolutionUseCase,
    @ApplicationContext private val context: Context
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
        val isSolutionSuccess: Boolean = false
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
    }

    fun process(action: Action) {
        when (action) {
            is Action.SetSolutionInfo -> handleSetSolutionInfo(action.solutionType, action.cardId)
            is Action.GetEmployees -> handleGetEmployees()
            is Action.OnSearchEmployee -> handleOnSearchEmployee(action.query)
            is Action.OnSelectEmployee -> handleOnSelectEmployee(action.employee)
            is Action.OnCommentChange -> handleOnCommentChange(action.comment)
            is Action.OnSave -> handleOnSave()
            is Action.OnAddEvidence -> handleOnAddEvidence(action.uri, action.type)
            is Action.OnDeleteEvidence -> handleOnDeleteEvidence(action.evidence)
            is Action.GetCardDetail -> handleGetCardDetail(action.cardId)
            is Action.GetCardType -> handleGetCardType(action.id)
        }
    }

    private fun handleOnAddEvidence(uri: Uri, type: EvidenceType) {
        viewModelScope.launch {
            val state = stateFlow.first()
            val maxImages = imagesQuantity(state.solutionType, state.cardType)
            if (state.evidences.toImages().size == maxImages) {
                setState { copy(message = context.getString(R.string.limit_images)) }
                return@launch
            }
            val maxVideos = videoQuantity(state.solutionType, state.cardType)
            if (state.evidences.toVideos().size == maxVideos) {
                setState { copy(message = context.getString(R.string.limit_videos)) }
                return@launch
            }
            val maxAudios = audiosQuantity(state.solutionType, state.cardType)
            if (state.evidences.toAudios().size == maxAudios) {
                setState { copy(message = context.getString(R.string.limit_audios)) }
                return@launch
            }
            val list = state.evidences.toMutableList()
            list.add(
                Evidence.fromCreateEvidence(
                    cardId = state.card?.id.orEmpty(),
                    url = uri.toString(),
                    type = type.name
                )
            )
            setState { copy(evidences = list) }
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

    private fun handleOnSave() {
        setState { copy(isLoading = true, message = "Saving solution...") }
        viewModelScope.launch(coroutineContext) {
            val state = stateFlow.first()

            if (state.selectedEmployee == null) {
                setState { copy(isLoading = false, message = "Please select a user!") }
                return@launch
            }
            kotlin.runCatching {
                saveCardSolutionUseCase(
                    solutionType = state.solutionType,
                    cardId = state.card?.id?.toInt().defaultIfNull(0),
                    comments = state.comments,
                    userSolutionId = state.selectedEmployee.id,
                    evidences = state.evidences
                )
            }.onSuccess {
                Log.e("Test", "Solution Success ${it}")
                setState { copy(isLoading = false, isSolutionSuccess = true, message = EMPTY) }
            }.onFailure {
                Log.e("Test", "Solution Failure ${it.localizedMessage}")
                setState { copy(isLoading = false, message = it.localizedMessage.orEmpty()) }
            }
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
            )
        }
        process(Action.GetCardDetail(cardId))
    }

    private fun handleGetCardDetail(cardId: String) {
        setState { copy(isLoading = true, message = "Loading data...") }
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getCardDetailUseCase(cardId, false)
            }.onSuccess {
                setState { copy(card = it) }
                process(Action.GetEmployees)
                process(Action.GetCardType(it.cardTypeId.orEmpty()))
            }.onFailure {
                setState { copy(message = it.localizedMessage.orEmpty(), isLoading = false) }
            }
        }
    }

    private fun handleGetCardType(cardTypeId: String) {
        setState { copy(isLoading = true) }
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getCardTypeUseCase(cardTypeId)
            }.onSuccess {
                val audioDuration = if (stateFlow.first().solutionType == DEFINITIVE_SOLUTION) {
                    it.audiosDurationClose
                } else {
                    it.audiosDurationPs
                }.defaultIfNull(0)
                Log.e("test", "Card Type $it")
                setState {
                    copy(
                        cardType = cardType,
                        audioDuration = audioDuration,
                        isLoading = false
                    )
                }
            }.onFailure {
                setState { copy(message = it.localizedMessage.orEmpty(), isLoading = false) }
            }
        }
    }

    private fun handleGetEmployees() {
        setState { copy(isLoading = true) }
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getEmployeesUseCase()
            }.onSuccess {
                setState { copy(isLoading = false, message = EMPTY, employeeList = it) }
            }.onFailure {
                setState { copy(message = it.localizedMessage.orEmpty(), isLoading = false) }
            }
        }
    }


    @AssistedFactory
    interface Factory : AssistedViewModelFactory<SolutionViewModel, UiState> {
        override fun create(state: UiState): SolutionViewModel
    }

    companion object :
        MavericksViewModelFactory<SolutionViewModel, UiState> by hiltMavericksViewModelFactory()
}
