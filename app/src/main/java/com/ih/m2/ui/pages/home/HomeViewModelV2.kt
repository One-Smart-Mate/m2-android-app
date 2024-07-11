package com.ih.m2.ui.pages.home

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.m2.R
import com.ih.m2.core.FileHelper
import com.ih.m2.core.network.NetworkConnection
import com.ih.m2.core.network.NetworkConnectionStatus
import com.ih.m2.core.preferences.SharedPreferences
import com.ih.m2.core.ui.LCE
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.NetworkStatus
import com.ih.m2.domain.model.User
import com.ih.m2.domain.usecase.card.GetCardsUseCase
import com.ih.m2.domain.usecase.card.SyncCardsUseCase
import com.ih.m2.domain.usecase.catalogs.SyncCatalogsUseCase
import com.ih.m2.domain.usecase.user.GetUserUseCase
import com.ih.m2.ui.utils.EMPTY
import com.ih.m2.ui.utils.LOAD_CATALOGS
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class HomeViewModelV2 @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
    private val getUserUseCase: GetUserUseCase,
    private val getCardsUseCase: GetCardsUseCase,
    private val syncCatalogsUseCase: SyncCatalogsUseCase,
    private val syncCardsUseCase: SyncCardsUseCase,
    private val sharedPreferences: SharedPreferences,
    @ApplicationContext private val context: Context,
    private val fileHelper: FileHelper
) : MavericksViewModel<HomeViewModelV2.UiState>(initialState) {


    data class UiState(
        val state: LCE<User> = LCE.Uninitialized,
        val message: String = EMPTY,
        val syncCatalogs: Boolean = true,
        val cards: List<Card> = emptyList(),
        val refreshCards: Boolean = false,
        val isLoading: Boolean = false,
        val networkStatus: NetworkStatus = NetworkStatus.NO_INTERNET_ACCESS
    ) : MavericksState

    sealed class Action {
        data class SyncCatalogs(val syncCatalogs: String = EMPTY) : Action()
        data object GetCards : Action()
        data object RefreshCards : Action()
        data object SyncCards : Action()
        data object ClearMessage : Action()
    }

    fun process(action: Action) {
        when (action) {
            is Action.SyncCatalogs -> handleSyncCatalogs(action.syncCatalogs)
            is Action.GetCards -> handleGetCards()
            is Action.RefreshCards -> setState { copy(refreshCards = true) }
            is Action.SyncCards -> handleSyncCards()
            is Action.ClearMessage -> cleanScreenStates()
        }
    }

    init {
        checkNetworkStatus()
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
                    fileHelper.logException(it)
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
                setState { copy(cards = cards) }
                cleanScreenStates()
            }.onFailure {
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }

    private fun handleGetUser() {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getUserUseCase()
            }.onSuccess { user ->
                process(Action.GetCards)
                user?.let {
                    Log.e("test", "User -> $it")
                    setState { copy(state = LCE.Success(user)) }
                }
            }.onFailure {
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }

    private fun handleSyncCards() {
        setState { copy(isLoading = true, message = context.getString(R.string.upload_cards)) }
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
            if (state.networkStatus == NetworkStatus.DATA_CONNECTED
                && sharedPreferences.getNetworkPreference().isEmpty()) {
                setState {
                    copy(
                        isLoading = false,
                        message = context.getString(R.string.network_preferences_allowed)
                    )
                }
                return@launch
            }
            kotlin.runCatching {
                val result = getCardsUseCase(localCards = true)
                syncCardsUseCase(result)
            }.onSuccess {
                handleGetCards()
            }.onFailure {
                fileHelper.logException(it)
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }

    private fun checkNetworkStatus() {
        viewModelScope.launch {
            NetworkConnection.initObserve(object : NetworkConnectionStatus {
                override fun onNetworkChange(networkStatus: NetworkStatus) {
                    setState { copy(networkStatus = networkStatus) }
                    checkConnectivity(networkStatus)
                }
            })
            if (stateFlow.first().networkStatus == NetworkStatus.NO_INTERNET_ACCESS) {
                val networkStatus = NetworkConnection.networkStatus(context)
                setState { copy(networkStatus = networkStatus) }
                checkConnectivity(networkStatus)
            }
        }
    }
    private fun checkConnectivity(networkStatus: NetworkStatus) {
        viewModelScope.launch(coroutineContext) {
            val isDisconnected = NetworkConnection.isConnected().not()
            val networkState = when {
                isDisconnected && networkStatus == NetworkStatus.WIFI_CONNECTED -> {
                    NetworkStatus.WIFI_DISCONNECTED
                }
                isDisconnected && networkStatus == NetworkStatus.DATA_CONNECTED -> {
                    NetworkStatus.DATA_DISCONNECTED
                }
                else -> networkStatus
            }
            setState { copy(networkStatus = networkState) }
        }
    }


    private fun cleanScreenStates(message: String = EMPTY) {
        setState { copy(isLoading = false, message = message, refreshCards = false) }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<HomeViewModelV2, UiState> {
        override fun create(state: UiState): HomeViewModelV2
    }

    companion object :
        MavericksViewModelFactory<HomeViewModelV2, UiState> by hiltMavericksViewModelFactory()
}