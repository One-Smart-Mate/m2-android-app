package com.ih.m2.ui.pages.home

import android.content.Context
import android.util.Log
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.m2.R
import com.ih.m2.core.network.NetworkConnection
import com.ih.m2.core.ui.LCE
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.User
import com.ih.m2.domain.usecase.card.GetCardsUseCase
import com.ih.m2.domain.usecase.card.SyncCardsUseCase
import com.ih.m2.domain.usecase.catalogs.SyncCatalogsUseCase
import com.ih.m2.domain.usecase.user.GetUserUseCase
import com.ih.m2.ui.navigation.ARG_SYNC_CATALOG
import com.ih.m2.ui.pages.cardlist.CardListViewModel
import com.ih.m2.ui.utils.EMPTY
import com.ih.m2.ui.utils.LOAD_CATALOGS
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class HomeViewModelV2 @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
    private val getUserUseCase: GetUserUseCase,
    private val getCardsUseCase: GetCardsUseCase,
    private val syncCatalogsUseCase: SyncCatalogsUseCase,
    private val syncCardsUseCase: SyncCardsUseCase,
    @ApplicationContext private val context: Context,
) : MavericksViewModel<HomeViewModelV2.UiState>(initialState) {


    data class UiState(
        val state: LCE<User> = LCE.Uninitialized,
        val message: String = EMPTY,
        val syncCatalogs: Boolean = true,
        val cards: List<Card> = emptyList(),
        val refreshCards: Boolean = false,
        val isLoading: Boolean = false
    ) : MavericksState

    sealed class Action {
        data class SyncCatalogs(val syncCatalogs: String = EMPTY) : Action()
        data object GetCards : Action()
        data object RefreshCards : Action()
        data object SyncCards : Action()
        data object ClearMessage:Action()
    }

    fun process(action: Action) {
        when (action) {
            is Action.SyncCatalogs -> handleSyncCatalogs(action.syncCatalogs)
            is Action.GetCards -> handleGetCards()
            is Action.RefreshCards -> setState { copy(refreshCards = true) }
            is Action.SyncCards -> handleSyncCards()
            is Action.ClearMessage -> setState { copy(message = EMPTY) }

        }
    }

    private fun handleSyncCatalogs(syncCatalogs: String) {
        if (syncCatalogs == LOAD_CATALOGS) {
            setState { copy(isLoading = true, message = context.getString(R.string.loading_data)) }
            viewModelScope.launch(coroutineContext) {
                kotlin.runCatching {
                    syncCatalogsUseCase(syncCards = true)
                }.onSuccess {
                    handleCheckUser()
                }.onFailure {
                    handleCheckUser()
                }
            }
        } else {
            handleCheckUser()
        }
    }

    private fun handleCheckUser() {
        viewModelScope.launch(coroutineContext) {
            if (stateFlow.first().state is LCE.Success) {
                process(Action.GetCards)
            } else {
                handleGetUser()
            }
        }
    }

    private fun handleGetCards() {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getCardsUseCase()
            }.onSuccess { cards ->
                Log.e("test", "Cards -> $cards")
                setState { copy(cards = cards, refreshCards = false, isLoading = false) }
            }.onFailure {
                setState { copy(cards = emptyList(), refreshCards = false, isLoading = false) }
            }
        }
    }

    private fun handleGetUser() {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getUserUseCase()
            }.onSuccess { user ->
                process(Action.GetCards)
                user?.let { setState { copy(state = LCE.Success(user), isLoading = false) } }
            }.onFailure {
                setState { copy(message = it.localizedMessage.orEmpty(), isLoading = false) }
            }
        }
    }

    private fun handleSyncCards() {
        setState { copy(isLoading = true, message = context.getString(R.string.upload_cards)) }
        viewModelScope.launch(coroutineContext) {
            if (NetworkConnection.isConnected().not()) {
                setState { copy(isLoading = false, message = context.getString(R.string.please_connect_to_internet)) }
                return@launch
            }
            kotlin.runCatching {
                val result = getCardsUseCase(localCards = true)
                syncCardsUseCase(result)
            }.onSuccess {
                setState { copy(isLoading = false, message = EMPTY) }
            }.onFailure {
                setState { copy(isLoading = false, message = it.localizedMessage.orEmpty()) }
            }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<HomeViewModelV2, UiState> {
        override fun create(state: UiState): HomeViewModelV2
    }

    companion object :
        MavericksViewModelFactory<HomeViewModelV2, UiState> by hiltMavericksViewModelFactory()
}