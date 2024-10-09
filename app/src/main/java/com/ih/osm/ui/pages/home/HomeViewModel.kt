package com.ih.osm.ui.pages.home

import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.osm.R
import com.ih.osm.core.file.FileHelper
import com.ih.osm.core.firebase.FirebaseNotificationType
import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.core.network.NetworkConnectionStatus
import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.core.ui.LCE
import com.ih.osm.core.workmanager.WorkManagerUUID
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.NetworkStatus
import com.ih.osm.domain.model.User
import com.ih.osm.domain.model.toLocalCards
import com.ih.osm.domain.usecase.card.GetCardsUseCase
import com.ih.osm.domain.usecase.catalogs.SyncCatalogsUseCase
import com.ih.osm.domain.usecase.notifications.GetFirebaseNotificationUseCase
import com.ih.osm.domain.usecase.user.GetUserUseCase
import com.ih.osm.ui.extensions.lastSyncDate
import com.ih.osm.ui.extensions.runWorkRequest
import com.ih.osm.ui.utils.EMPTY
import com.ih.osm.ui.utils.LOAD_CATALOGS
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel
@AssistedInject
constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
    private val getUserUseCase: GetUserUseCase,
    private val getCardsUseCase: GetCardsUseCase,
    private val syncCatalogsUseCase: SyncCatalogsUseCase,
    private val sharedPreferences: SharedPreferences,
    @ApplicationContext private val context: Context,
    private val fileHelper: FileHelper,
    private val getFirebaseNotificationUseCase: GetFirebaseNotificationUseCase
) : MavericksViewModel<HomeViewModel.UiState>(initialState) {
    data class UiState(
        val state: LCE<User> = LCE.Uninitialized,
        val message: String = EMPTY,
        val syncCatalogs: Boolean = true,
        val cards: List<Card> = emptyList(),
        val isSyncing: Boolean = true,
        val isLoading: Boolean = false,
        val networkStatus: NetworkStatus = NetworkStatus.NO_INTERNET_ACCESS,
        val syncCompleted: Boolean = false,
        val lastSyncUpdate: String = EMPTY,
        val showSyncCards: Boolean = false,
        val showSyncCatalogsCard: Boolean = false,
        val showSyncRemoteCards: Boolean = false
    ) : MavericksState

    sealed class Action {
        data class SyncCatalogs(val syncCatalogs: String = EMPTY) : Action()

        data object GetCards : Action()

        data object SetIsSync : Action()

        data class SyncCards(val context: Context) : Action()

        data object ClearMessage : Action()

        data object SyncRemoteCards : Action()
    }

    fun process(action: Action) {
        when (action) {
            is Action.SyncCatalogs -> handleSyncCatalogs(action.syncCatalogs)
            is Action.GetCards -> handleGetCards()
            is Action.SetIsSync -> setState { copy(isSyncing = true) }
            is Action.SyncCards -> handleSyncCards(action.context)
            is Action.ClearMessage -> cleanScreenStates()
            is Action.SyncRemoteCards -> handleGetRemoteCards()
        }
    }

    init {
        checkNetworkStatus()
    }

    private fun handleSyncCatalogs(syncCatalogs: String) {
        if (syncCatalogs == LOAD_CATALOGS) {
            setState {
                copy(
                    isLoading = true,
                    message = context.getString(R.string.loading_data),
                    isSyncing = true
                )
            }
            viewModelScope.launch(coroutineContext) {
                kotlin.runCatching {
                    syncCatalogsUseCase(syncCards = true)
                }.onSuccess {
                    setState {
                        copy(
                            syncCompleted = true,
                            showSyncCatalogsCard = false,
                            isSyncing = false
                        )
                    }
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
            if (stateFlow.first().state !is LCE.Success) {
                handleGetUser()
            }
        }
    }

    private fun handleGetCards(syncRemote: Boolean = false) {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getCardsUseCase(syncRemote = syncRemote)
            }.onSuccess { cards ->
                val showCards = cards.toLocalCards().isNotEmpty()
                setState {
                    copy(
                        cards = cards,
                        showSyncCards = showCards,
                        showSyncRemoteCards = false
                    )
                }
                if (showCards) {
                    checkLastUpdate()
                }
                checkPreferences()
                cleanScreenStates()
            }.onFailure {
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }

    private fun handleGetRemoteCards() {
        setState {
            copy(
                isLoading = true,
                message = context.getString(R.string.loading_data),
                isSyncing = true
            )
        }
        viewModelScope.launch {
            val state = stateFlow.first()
            if (NetworkConnection.isConnected().not() ||
                state.networkStatus == NetworkStatus.NO_INTERNET_ACCESS ||
                state.networkStatus == NetworkStatus.WIFI_DISCONNECTED ||
                state.networkStatus == NetworkStatus.DATA_DISCONNECTED
            ) {
                setState {
                    copy(
                        isLoading = false,
                        message = context.getString(R.string.please_connect_to_internet),
                        isSyncing = false
                    )
                }
                return@launch
            }
            if (state.networkStatus == NetworkStatus.DATA_CONNECTED &&
                sharedPreferences.getNetworkPreference().isEmpty()
            ) {
                setState {
                    copy(
                        isLoading = false,
                        message = context.getString(R.string.network_preferences_allowed),
                        isSyncing = false
                    )
                }
                return@launch
            }
            handleGetCards(syncRemote = true)
        }
    }

    private fun checkLastUpdate() {
        viewModelScope.launch {
            val lastUpdate = sharedPreferences.getLastSyncDate()
            val lastUpdateText =
                if (lastUpdate.isNotEmpty()) {
                    lastUpdate.lastSyncDate(context)
                } else {
                    sharedPreferences.saveLastSyncDate()
                    context.getString(R.string.right_now)
                }
            setState { copy(lastSyncUpdate = lastUpdateText) }
        }
    }

    private fun handleGetUser() {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getUserUseCase()
            }.onSuccess { user ->
                process(Action.GetCards)
                user?.let {
                    setState { copy(state = LCE.Success(user)) }
                }
            }.onFailure {
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }

    private fun handleSyncCards(appContext: Context) {
        viewModelScope.launch {
            val state = stateFlow.first()
            if (state.isSyncing) return@launch
            setState {
                copy(
                    isLoading = true,
                    message = context.getString(R.string.upload_cards),
                    showSyncCards = false,
                    isSyncing = true
                )
            }
        }
        viewModelScope.launch(coroutineContext) {
            val state = stateFlow.first()
            if (NetworkConnection.isConnected().not() ||
                state.networkStatus == NetworkStatus.NO_INTERNET_ACCESS ||
                state.networkStatus == NetworkStatus.WIFI_DISCONNECTED ||
                state.networkStatus == NetworkStatus.DATA_DISCONNECTED
            ) {
                setState {
                    copy(
                        isLoading = false,
                        message = context.getString(R.string.please_connect_to_internet),
                        showSyncCards = true,
                        isSyncing = false
                    )
                }
                return@launch
            }
            if (state.networkStatus == NetworkStatus.DATA_CONNECTED &&
                sharedPreferences.getNetworkPreference().isEmpty()
            ) {
                setState {
                    copy(
                        isLoading = false,
                        message = context.getString(R.string.network_preferences_allowed),
                        showSyncCards = true,
                        isSyncing = false
                    )
                }
                return@launch
            }

            appContext.runWorkRequest()
            WorkManagerUUID.get()?.let { uuid ->
                WorkManager.getInstance(appContext)
                    .getWorkInfoByIdFlow(uuid)
                    .collect {
                        when (it.state) {
                            WorkInfo.State.SUCCEEDED -> {
                                handleGetCards()
                            }

                            WorkInfo.State.CANCELLED,
                            WorkInfo.State.FAILED,
                            WorkInfo.State.BLOCKED
                            -> {
                                setState {
                                    copy(
                                        isLoading = false,
                                        isSyncing = false,
                                        message = context.getString(R.string.work_failed)
                                    )
                                }
                            }

                            else -> {
                            }
                        }
                    }
            } ?: handleGetCards()
        }
    }

    private fun checkNetworkStatus() {
        viewModelScope.launch {
            NetworkConnection.initObserve(
                object : NetworkConnectionStatus {
                    override fun onNetworkChange(networkStatus: NetworkStatus) {
                        setState { copy(networkStatus = networkStatus) }
                        checkConnectivity(networkStatus)
                    }
                }
            )
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
            val networkState =
                when {
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
        setState {
            copy(
                isLoading = false,
                message = message,
                syncCompleted = true,
                isSyncing = false
            )
        }
    }

    private fun checkPreferences() {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                when (getFirebaseNotificationUseCase()) {
                    FirebaseNotificationType.SYNC_REMOTE_CATALOGS -> {
                        setState { copy(showSyncCatalogsCard = true) }
                    }

                    FirebaseNotificationType.SYNC_REMOTE_CARDS -> {
                        setState { copy(showSyncRemoteCards = true) }
                    }

                    else -> {
                    }
                }
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
