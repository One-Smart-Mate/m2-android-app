package com.ih.m2.ui.pages.solution

import android.net.Uri
import android.util.Log
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.m2.R
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.Employee
import com.ih.m2.domain.model.Evidence
import com.ih.m2.domain.model.EvidenceType
import com.ih.m2.domain.model.toAudios
import com.ih.m2.domain.model.toImages
import com.ih.m2.domain.model.toVideos
import com.ih.m2.domain.usecase.card.GetCardDetailUseCase
import com.ih.m2.domain.usecase.cardtype.GetCardTypeUseCase
import com.ih.m2.domain.usecase.employee.GetEmployeesUseCase
import com.ih.m2.ui.extensions.defaultIfNull
import com.ih.m2.ui.pages.createcard.CreateCardViewModel
import com.ih.m2.ui.utils.EMPTY
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SolutionViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val coroutineContext: CoroutineContext,
    private val getCardDetailUseCase: GetCardDetailUseCase,
    private val getCardTypeUseCase: GetCardTypeUseCase
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
        val card: Card? = null
    ) : MavericksState

    sealed class Action {
        data class SetSolutionInfo(val solutionType: String, val cardId: String) : Action()
        data object GetEmployees : Action()
        data class OnSearchEmployee(val query: String) : Action()
        data class OnSelectEmployee(val employee: Employee): Action()
        data class OnCommentChange(val comment: String): Action()
        data object OnSave: Action()
        data class OnAddEvidence(val uri: Uri, val type: EvidenceType) : Action()
        data class OnDeleteEvidence(val evidence: Evidence) : Action()
        data class GetCardDetail(val cardId: String): Action()
        data class GetCardType(val id: String): Action()
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
//            val cardType = state.cardType
//            val maxImages = cardType?.quantityImagesCreate.defaultIfNull(0)
//            if (state.evidences.toImages().size == maxImages) {
//                setState { copy(message = context.getString(R.string.limit_images)) }
//                return@launch
//            }
//            val maxVideos = cardType?.quantityVideosCreate.defaultIfNull(0)
//            if (state.evidences.toVideos().size == maxVideos) {
//                setState { copy(message = context.getString(R.string.limit_videos)) }
//                return@launch
//            }
//            val maxAudios = cardType?.quantityAudiosCreate.defaultIfNull(0)
//            if (state.evidences.toAudios().size == maxAudios) {
//                setState { copy(message = context.getString(R.string.limit_audios)) }
//                return@launch
//            }
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

    private fun handleOnDeleteEvidence(evidence: Evidence) {
        viewModelScope.launch {
            val state = stateFlow.first()
            val list = state.evidences.filter { it.id != evidence.id }
            setState { copy(evidences = list) }
        }
    }

    private fun handleOnSave() {
        viewModelScope.launch(coroutineContext) {
            val state = stateFlow.first()
            Log.e("test","Card ${state.card}")
        }
    }

    private fun handleOnCommentChange(comment: String) {
        setState { copy(comments = comment) }
    }

    private fun handleOnSelectEmployee(employee: Employee) {
        setState { copy(selectedEmployee = employee, query = employee.name, resultList = emptyList()) }
    }

    private fun handleOnSearchEmployee(query: String) {
        viewModelScope.launch {
            val state = stateFlow.first()
            val resultList = state.employeeList.filter { it.name.lowercase().contains(query.lowercase()) }
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
        setState { copy(isLoading = true) }
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getCardDetailUseCase(cardId, false)
            }.onSuccess {
                Log.e("test","Card ${it}")
                setState { copy(card = it) }
                process(Action.GetEmployees)
                process(Action.GetCardType(it.cardTypeId.orEmpty()))
            }.onFailure {
                setState { copy(message = it.localizedMessage.orEmpty(), isLoading = false) }
            }
        }
    }

    private fun handleGetCardType(cardTypeId: String) {
        Log.e("test","GetCardtype aqui $cardTypeId")
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getCardTypeUseCase(cardTypeId)
            }.onSuccess {
                Log.e("test","GetCardtype $it")
            }.onFailure {
                Log.e("test","GetCardtype error ${it.localizedMessage}")
                setState { copy(message = it.localizedMessage.orEmpty()) }
            }
        }
    }

    private fun handleGetEmployees() {
        setState { copy(isLoading = true, message = "Loading data...") }
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
