package com.osm.ui.pages.home

import android.content.Context
import android.util.Log
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.R
import com.osm.core.ui.LCE
import com.osm.domain.model.Card
import com.osm.domain.model.User
import com.osm.domain.model.filterByStatus
import com.osm.domain.usecase.card.GetCardsUseCase
import com.osm.domain.usecase.card.SyncCardsUseCase
import com.osm.domain.usecase.catalogs.SyncCatalogsUseCase
import com.osm.domain.usecase.user.GetUserUseCase
import com.osm.ui.extensions.toFilterStatus
import com.osm.ui.utils.CLEAN_FILTERS
import com.osm.ui.utils.EMPTY
import com.osm.ui.utils.LOAD_CATALOGS
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
    private val syncCardsUseCase: SyncCardsUseCase,
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
        val isRefreshing: Boolean = false,
        val shouldRefreshList: Boolean = false
    ) : MavericksState

    sealed class Action {
        data object GetUser : Action()
        data class HandleBottomSheet(val open: Boolean) : Action()
        data class HandleBottomSheetActions(val open: Boolean, val card: Card? = null) : Action()
        data class OnFilterChange(val filter: String) : Action()
        data object OnApplyFilter : Action()
        data object OnCleanFilters : Action()
        data class SyncCatalogs(val syncCatalogs: String = EMPTY): Action()
        data class OnRefresh(val remotes: Boolean = true): Action()
        data class ShouldRefreshList(val refresh: Boolean): Action()
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
            is Action.OnRefresh -> handleOnRefresh(action.remotes)
            is Action.ShouldRefreshList -> handleShouldRefreshList(action.refresh)
        }
    }

    private fun handleShouldRefreshList(refresh: Boolean) {
        setState { copy(shouldRefreshList = refresh) }
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
            setState { copy(user = LCE.Loading, loadingMessage = context.getString(R.string.loading_data)) }
            viewModelScope.launch(coroutineContext) {
                kotlin.runCatching {
                    syncCatalogsUseCase(syncCards = true)
                }.onSuccess {
                    process(Action.GetUser)
                }.onFailure {
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

    private fun handleOnRefresh(remotes: Boolean) {
        setState { copy(isRefreshing = remotes) }
        viewModelScope.launch(coroutineContext) {
            if (remotes) {
                handleSyncCards()
            }
            kotlin.runCatching {
                Log.e("test","Get Remote cards")
                getCardsUseCase(syncRemote = remotes)
            }.onSuccess {
                setState { copy( cardList = it, isRefreshing = false, shouldRefreshList = false) }
            }.onFailure {
                setState { copy(isRefreshing = false) }
            }
        }
    }

    private suspend fun handleSyncCards() {
        kotlin.runCatching {
            val result = getCardsUseCase(localCards = true)
            syncCardsUseCase(result)
        }.onSuccess {
            Log.e("test","Success Sync")
        }.onFailure {
            Log.e("test","error")
            FirebaseCrashlytics.getInstance().recordException(it)
        }
    }


    @AssistedFactory
    interface Factory : AssistedViewModelFactory<HomeViewModel, UiState> {
        override fun create(state: UiState): HomeViewModel
    }

    companion object :
        MavericksViewModelFactory<HomeViewModel, UiState> by hiltMavericksViewModelFactory()
}