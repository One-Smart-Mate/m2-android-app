package com.ih.m2.ui.pages.home

import android.content.Context
import android.util.Log
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.m2.core.ui.LCE
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.User
import com.ih.m2.domain.model.filterByStatus
import com.ih.m2.domain.usecase.card.GetCardsUseCase
import com.ih.m2.domain.usecase.getuser.GetUserUseCase
import com.ih.m2.ui.extensions.toFilterStatus
import com.ih.m2.ui.utils.CLEAN_FILTERS
import com.ih.m2.ui.utils.EMPTY
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class HomeViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
    private val getUserUseCase: GetUserUseCase,
    private val getCardsUseCase: GetCardsUseCase,
    @ApplicationContext private val context: Context
) : MavericksViewModel<HomeViewModel.UiState>(initialState) {


    init {
        process(Action.GetUser)
    }

    data class UiState(
        val user: LCE<User> = LCE.Uninitialized,
        val cardList: List<Card> = emptyList(),
        val originalCardList: List<Card> = emptyList(),
        val showBottomSheet: Boolean = false,
        val filterSelection: String = EMPTY
    ) : MavericksState

    sealed class Action {
        data object GetUser : Action()
        data class HandleBottomSheet(val open: Boolean) : Action()
        data class OnFilterChange(val filter: String) : Action()
        data object OnApplyFilter : Action()
        data object OnCleanFilters : Action()
    }

    fun process(action: Action) {
        when (action) {
            is Action.GetUser -> handleGetUser()
            is Action.HandleBottomSheet -> handleBottomSheet(action.open)
            is Action.OnFilterChange -> handleOnFilterChange(action.filter)
            is Action.OnApplyFilter -> handleOnApplyFilter()
            is Action.OnCleanFilters -> handleOnCleanFilters()
        }
    }

    private fun handleGetUser() {
        setState { copy(user = LCE.Loading) }
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getUserUseCase()
            }.onSuccess {
                it?.let {
                    handleGetCards(it)
                }
            }.onFailure {
                setState { copy(user = LCE.Fail(it.localizedMessage.orEmpty())) }
            }
        }
    }

    private fun handleGetCards(user: User) {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getCardsUseCase()
            }.onSuccess {
                Log.e("", "success cards ${it}")
                setState { copy(user = LCE.Success(user), cardList = it, originalCardList = it) }
            }.onFailure {
                Log.e("", "Error cards ${it.localizedMessage}")
                setState { copy(user = LCE.Fail(it.localizedMessage.orEmpty())) }
            }
        }
    }

    private fun handleBottomSheet(open: Boolean) {
        setState { copy(showBottomSheet = open) }
    }

    private fun handleOnFilterChange(filter: String) {
        setState { copy(filterSelection = filter) }
    }

    private fun handleOnApplyFilter() {
        viewModelScope.launch {
            val state = stateFlow.first()
            val user = (state.user) as LCE.Success
            val filter = state.filterSelection.toFilterStatus(context)
            val filteredList = state.originalCardList.filterByStatus(filter, user.value.userId)
            setState { copy(showBottomSheet = false, cardList = filteredList) }
        }
    }

    private fun handleOnCleanFilters() {
        viewModelScope.launch {
            val state = stateFlow.first()
            setState {
                copy(
                    cardList = state.originalCardList,
                    showBottomSheet = false,
                    filterSelection = CLEAN_FILTERS
                )
            }
        }
    }


    @AssistedFactory
    interface Factory : AssistedViewModelFactory<HomeViewModel, UiState> {
        override fun create(state: UiState): HomeViewModel
    }

    companion object :
        MavericksViewModelFactory<HomeViewModel, UiState> by hiltMavericksViewModelFactory()
}
