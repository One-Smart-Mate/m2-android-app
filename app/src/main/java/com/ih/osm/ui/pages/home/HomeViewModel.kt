package com.ih.osm.ui.pages.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ih.osm.BuildConfig
import com.ih.osm.MainActivity
import com.ih.osm.R
import com.ih.osm.core.file.FileHelper
import com.ih.osm.core.firebase.FirebaseNotificationType
import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.core.network.NetworkConnectionStatus
import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.core.workmanager.WorkManagerUUID
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.NetworkStatus
import com.ih.osm.domain.model.User
import com.ih.osm.domain.model.toLocalCards
import com.ih.osm.domain.usecase.card.GetCardsUseCase
import com.ih.osm.domain.usecase.catalogs.SyncCatalogsUseCase
import com.ih.osm.domain.usecase.notifications.GetFirebaseNotificationUseCase
import com.ih.osm.domain.usecase.user.GetUserUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.extensions.getActivity
import com.ih.osm.ui.extensions.lastSyncDate
import com.ih.osm.ui.navigation.ARG_SYNC_CATALOG
import com.ih.osm.ui.pages.home.action.HomeAction
import com.ih.osm.ui.utils.EMPTY
import com.ih.osm.ui.utils.LOAD_CATALOGS
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val getUserUseCase: GetUserUseCase,
        private val getCardsUseCase: GetCardsUseCase,
        private val syncCatalogsUseCase: SyncCatalogsUseCase,
        private val sharedPreferences: SharedPreferences,
        private val fileHelper: FileHelper,
        private val getFirebaseNotificationUseCase: GetFirebaseNotificationUseCase,
        @ApplicationContext private val context: Context,
        savedStateHandle: SavedStateHandle,
    ) : BaseViewModel<HomeViewModel.UiState>(UiState()) {
        data class UiState(
            val user: User? = null,
            val message: String = EMPTY,
            val cards: List<Card> = emptyList(),
            val isSyncing: Boolean = true,
            val isLoading: Boolean = false,
            val networkStatus: NetworkStatus = NetworkStatus.NO_INTERNET_ACCESS,
            val lastSyncUpdate: String = EMPTY,
            val showSyncLocalCards: Boolean = false,
            val showSyncCatalogs: Boolean = false,
            val showSyncRemoteCards: Boolean = false,
            val updateApp: Boolean = false,
        )

        fun process(action: HomeAction) {
            when (action) {
                is HomeAction.GetCards -> handleGetCards()
                is HomeAction.SyncCatalogs -> handleSyncCatalogs(action.syncCatalogs)
                is HomeAction.SyncRemoteCards -> handleSyncRemoteCards()
                is HomeAction.SyncLocalCards -> {
                    Log.e("HomeViewModel", "Recibida acción para sincronizar tarjetas locales")
                    handleSyncLocalCards(action.context)
                }
            }
        }

        init {
            checkNetworkStatus()
            val syncCatalogs = savedStateHandle.get<String>(ARG_SYNC_CATALOG).orEmpty()
            handleSyncCatalogs(syncCatalogs)
        }

        private fun handleSyncLocalCards(appContext: Context) {
            Log.e("HomeViewModel", "Entrando a handleSyncLocalCards()")
            viewModelScope.launch {
                val state = getState()
                if (NetworkConnection.isConnected().not() ||
                    state.networkStatus == NetworkStatus.NO_INTERNET_ACCESS ||
                    state.networkStatus == NetworkStatus.WIFI_DISCONNECTED ||
                    state.networkStatus == NetworkStatus.DATA_DISCONNECTED
                ) {
                    Log.e("HomeViewModel", "No hay conexión, mensaje: ${context.getString(R.string.please_connect_to_internet)}")
                    setState {
                        copy(
                            isLoading = false,
                            message = context.getString(R.string.please_connect_to_internet),
                            showSyncLocalCards = true,
                        )
                    }
                    return@launch
                }
                if (state.networkStatus == NetworkStatus.DATA_CONNECTED &&
                    sharedPreferences.getNetworkPreference().isEmpty()
                ) {
                    Log.e(
                        "HomeViewModel",
                        "Datos móviles activos sin preferencia guardada, mensaje: : ${context.getString(
                            R.string.network_preferences_allowed,
                        )}",
                    )
                    setState {
                        copy(
                            isLoading = false,
                            message = context.getString(R.string.network_preferences_allowed),
                            showSyncLocalCards = true,
                        )
                    }
                    return@launch
                }
                Log.e("HomeViewModel", "Enviando trajetas locales a sincronizar")
                appContext.getActivity<MainActivity>()?.enqueueSyncCardsWork()
                setState { copy(showSyncLocalCards = false) }
            }
        }

        private fun handleSyncCatalogs(syncCatalogs: String) {
            if (syncCatalogs == LOAD_CATALOGS) {
                viewModelScope.launch {
                    setState {
                        copy(
                            isLoading = true,
                            message = context.getString(R.string.loading_data),
                        )
                    }

                    val state = getState()
                    if (NetworkConnection.isConnected().not() ||
                        state.networkStatus == NetworkStatus.NO_INTERNET_ACCESS ||
                        state.networkStatus == NetworkStatus.WIFI_DISCONNECTED ||
                        state.networkStatus == NetworkStatus.DATA_DISCONNECTED
                    ) {
                        setState {
                            copy(
                                isLoading = false,
                                message = context.getString(R.string.please_connect_to_internet),
                                isSyncing = false,
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
                                isSyncing = false,
                            )
                        }
                        return@launch
                    }
                    handleGetCatalogs()
                }
            } else {
                handleGetUser()
            }
        }

        //
        private fun handleGetCatalogs() {
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { syncCatalogsUseCase(syncCards = true) }
                }.onSuccess {
                    Log.e("test", "Fetch catalogs complete")
                    setState {
                        copy(
                            showSyncCatalogs = false,
                        )
                    }
                    handleGetUser()
                }.onFailure {
                    fileHelper.logException(it)
                }
            }
        }

        private fun handleGetCards(syncRemote: Boolean = false) {
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { getCardsUseCase(syncRemote = syncRemote) }
                }.onSuccess { cards ->
                    val hasLocalCards = cards.toLocalCards().isNotEmpty()
                    Log.e(
                        "test",
                        "Fetch cards complete wok in progress? ${WorkManagerUUID.checkIfNull()}",
                    )
                    setState {
                        copy(
                            cards = cards,
                            showSyncLocalCards = hasLocalCards && WorkManagerUUID.checkIfNull(),
                            showSyncRemoteCards = false,
                        )
                    }
                    if (hasLocalCards) {
                        checkLastUpdate()
                    }
                    checkPreferences()
                    cleanScreenStates()
                }.onFailure {
                    cleanScreenStates(it.localizedMessage.orEmpty())
                }
            }
        }

        private fun handleSyncRemoteCards() {
            setState {
                copy(
                    isLoading = true,
                    message = context.getString(R.string.loading_data),
                )
            }
            viewModelScope.launch {
                val state = getState()
                if (NetworkConnection.isConnected().not() ||
                    state.networkStatus == NetworkStatus.NO_INTERNET_ACCESS ||
                    state.networkStatus == NetworkStatus.WIFI_DISCONNECTED ||
                    state.networkStatus == NetworkStatus.DATA_DISCONNECTED
                ) {
                    Log.e("HomeViewModel", "No hay conexión, mensaje a mostrar: ${context.getString(R.string.please_connect_to_internet)}")
                    setState {
                        copy(
                            isLoading = false,
                            message = context.getString(R.string.please_connect_to_internet),
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
            setState { copy(isLoading = true) }
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { getUserUseCase() }
                }.onSuccess { user ->
                    Log.e("test", "Fetch user complete")
                    handleGetCards()
                    setState { copy(user = user) }
                }.onFailure {
                    cleanScreenStates(it.localizedMessage.orEmpty())
                }
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
                    },
                )
                if (getState().networkStatus == NetworkStatus.NO_INTERNET_ACCESS) {
                    val networkStatus = NetworkConnection.networkStatus(context)
                    setState { copy(networkStatus = networkStatus) }
                    checkConnectivity(networkStatus)
                }
            }
        }

        private fun checkConnectivity(networkStatus: NetworkStatus) {
            viewModelScope.launch {
                val isDisconnected = callUseCase { NetworkConnection.isConnected().not() }
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

        //
        private fun cleanScreenStates(message: String = EMPTY) {
            Log.e("HomeViewModel", "Limpiando estados, mensaje: $message")
            setState {
                copy(
                    isLoading = false,
                    message = if (message.isNotEmpty()) message else getState().message,
                    isSyncing = false,
                )
            }
            Log.e("HomeViewModel", "Después de limpiar estados, mensaje: ${getState().message}")
        }

        private fun checkPreferences() {
            viewModelScope.launch {
                kotlin.runCatching {
                    val firebaseNotifications = callUseCase { getFirebaseNotificationUseCase() }
                    Log.e("test", "Firebase $firebaseNotifications")

                    when (firebaseNotifications) {
                        FirebaseNotificationType.SYNC_REMOTE_CATALOGS -> {
                            setState { copy(showSyncCatalogs = true) }
                        }

                        FirebaseNotificationType.SYNC_REMOTE_CARDS -> {
                            setState { copy(showSyncRemoteCards = true) }
                        }

                        FirebaseNotificationType.UPDATE_APP -> {
                            val appVersion = sharedPreferences.getAppVersion()
                            if (appVersion.isNotEmpty() && appVersion != BuildConfig.VERSION_NAME) {
                                setState { copy(updateApp = true) }
                            } else {
                                getFirebaseNotificationUseCase(remove = true, appUpdate = true)
                                setState { copy(updateApp = false) }
                            }
                        }

                        else -> {
                        }
                    }
                }
            }
        }
    }
