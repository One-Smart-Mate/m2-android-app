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
import com.ih.m2.domain.usecase.catalogs.SyncCatalogsUseCase
import com.ih.m2.domain.usecase.user.GetUserUseCase
import com.ih.m2.ui.extensions.DayAndDateWithYear
import com.ih.m2.ui.extensions.toDate
import com.ih.m2.ui.extensions.toFilterStatus
import com.ih.m2.ui.utils.CLEAN_FILTERS
import com.ih.m2.ui.utils.EMPTY
import com.ih.m2.ui.utils.LOAD_CATALOGS
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class HomeViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
    private val getUserUseCase: GetUserUseCase,
    private val getCardsUseCase: GetCardsUseCase,
    private val syncCatalogsUseCase: SyncCatalogsUseCase,
    @ApplicationContext private val context: Context,
) : MavericksViewModel<HomeViewModel.UiState>(initialState) {

    data class UiState(
        val user: LCE<User> = LCE.Uninitialized,
        val cardList: List<Card> = emptyList(),
        val showBottomSheet: Boolean = false,
        val filterSelection: String = EMPTY,
        val syncCatalogs: Boolean = true,
        val loadingMessage: String = EMPTY,
        val showBottomSheetActions: Boolean = false,
        val selectedCard: Card? = null,
        val isRefreshing: Boolean = false
    ) : MavericksState

    sealed class Action {
        data object GetUser : Action()
        data class HandleBottomSheet(val open: Boolean) : Action()
        data class HandleBottomSheetActions(val open: Boolean, val card: Card? = null) : Action()
        data class OnFilterChange(val filter: String) : Action()
        data object OnApplyFilter : Action()
        data object OnCleanFilters : Action()
        data class SyncCatalogs(val syncCatalogs: String = EMPTY): Action()
        data object OnRefresh: Action()
    }

    fun process(action: Action) {
        when (action) {
            is Action.GetUser -> handleGetUser()
            is Action.HandleBottomSheet -> handleBottomSheet(action.open)
            is Action.OnFilterChange -> handleOnFilterChange(action.filter)
            is Action.OnApplyFilter -> handleOnApplyFilter()
            is Action.OnCleanFilters -> handleOnCleanFilters()
            is Action.SyncCatalogs -> handleSyncCatalogs(action.syncCatalogs)
            is Action.HandleBottomSheetActions -> handleBottomSheetActions(action.open, action.card)
            is Action.OnRefresh -> handleOnRefresh()
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
                setState { copy(user = LCE.Success(user), cardList = it, syncCatalogs = false) }
            }.onFailure {
                setState { copy(user = LCE.Fail(it.localizedMessage.orEmpty())) }
            }
        }
    }

    private fun handleSyncCatalogs(syncCatalogs: String) {
        Log.e("test","Sync $syncCatalogs")
        if (syncCatalogs == LOAD_CATALOGS) {
            setState { copy(user = LCE.Loading, loadingMessage = "Loading data...") }
            viewModelScope.launch(coroutineContext) {
                kotlin.runCatching {
                    syncCatalogsUseCase(syncCards = true)
                }.onSuccess {
                    Log.e("test","Sync $it")
                    process(Action.GetUser)
                }.onFailure {
                    Log.e("test","Sync failure ${it.localizedMessage}")
                    process(Action.GetUser)
                }
            }
        } else {
            process(Action.GetUser)
        }
    }

    private fun handleBottomSheet(open: Boolean) {
        setState { copy(showBottomSheet = open) }
    }

    private fun handleBottomSheetActions(open: Boolean, card: Card?) {
        setState { copy(showBottomSheetActions = open, selectedCard = card) }
    }

    private fun handleOnFilterChange(filter: String) {
        setState { copy(filterSelection = filter) }
    }

    private fun handleOnApplyFilter() {
        viewModelScope.launch(coroutineContext) {
            val state = stateFlow.first()
            val user = (state.user) as LCE.Success
            val filter = state.filterSelection.toFilterStatus(context)
            val list = getCardsUseCase()
            val filteredList = list.filterByStatus(filter, user.value.userId)
            setState { copy(showBottomSheet = false, cardList = filteredList) }
        }
    }

    private fun handleOnCleanFilters() {
        viewModelScope.launch(coroutineContext) {
            val cards = getCardsUseCase()
            setState {
                copy(
                    cardList = cards,
                    showBottomSheet = false,
                    filterSelection = CLEAN_FILTERS
                )
            }
        }
    }

    private fun handleOnRefresh() {
        setState { copy(isRefreshing = true) }
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getCardsUseCase(syncRemote = true)
            }.onSuccess {
                setState { copy( cardList = it, isRefreshing = false) }
            }.onFailure {
                setState { copy(isRefreshing = false) }
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
