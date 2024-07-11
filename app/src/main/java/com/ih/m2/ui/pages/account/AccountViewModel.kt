package com.ih.m2.ui.pages.account

import android.net.Uri
import android.util.Log
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.m2.core.FileHelper
import com.ih.m2.core.notifications.NotificationManager
import com.ih.m2.core.preferences.SharedPreferences
import com.ih.m2.domain.usecase.catalogs.SyncCatalogsUseCase
import com.ih.m2.domain.usecase.logout.LogoutUseCase
import com.ih.m2.ui.utils.EMPTY
import com.ih.m2.ui.utils.NETWORK_DATA_MOBILE
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AccountViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
    private val logoutUseCase: LogoutUseCase,
    private val syncCatalogsUseCase: SyncCatalogsUseCase,
    private val notificationManager: NotificationManager,
    private val sharedPreferences: SharedPreferences,
    private val fileHelper: FileHelper
) : MavericksViewModel<AccountViewModel.UiState>(initialState) {


    data class UiState(
        val logout: Boolean = false,
        val message: String = EMPTY,
        val isLoading: Boolean = false,
        val checked: Boolean = false,
        val uri: Uri? = null
    ) : MavericksState

    init {
        getNetworkPreferences()
        getLogFile()
    }

    private fun getLogFile() {
        viewModelScope.launch {
            val uri = fileHelper.getFileUri()
            setState { copy(uri = uri) }
        }
    }

    sealed class Action {
        data object Logout: Action()
        data object SyncCatalogs: Action()
        data object ShowNotification: Action()
        data class OnSwitchChange(val checked: Boolean): Action()
    }

    fun process(action: Action) {
        when(action) {
            is Action.Logout -> handleLogout()
            is Action.SyncCatalogs -> handleSyncCatalogs()
            is Action.ShowNotification -> handleShowNotification()
            is Action.OnSwitchChange -> handleOnSwitchChange(action.checked)
        }
    }

    private fun handleOnSwitchChange(checked: Boolean) {
        val network = if (checked) {
            NETWORK_DATA_MOBILE
        } else {
            EMPTY
        }
        viewModelScope.launch(coroutineContext) {
            sharedPreferences.saveNetworkPreference(network)
            setState { copy(checked = checked) }
        }
    }

    private fun getNetworkPreferences() {
        val network = sharedPreferences.getNetworkPreference()
        val checked = network.isNotEmpty()
        setState { copy(checked = checked) }
    }

    private fun handleLogout() {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                logoutUseCase()
            }.onSuccess {
                setState { copy(logout = true) }
            }.onFailure {
                setState { copy(message = it.localizedMessage.orEmpty()) }
            }
        }
    }

    private fun handleShowNotification() {
        viewModelScope.launch {
            var progres = 0
            val id = notificationManager.buildProgressNotification()
            while (progres != 100) {
                delay(3000)
                notificationManager.updateNotificationProgress(
                    notificationId = id,
                    currentProgress = progres,

                )
                progres+=10
            }

        }
    }

    private fun handleSyncCatalogs() {
        setState { copy(isLoading = true) }
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                syncCatalogsUseCase(syncCards = false)
            }.onSuccess {
                setState { copy(isLoading = false, message = "Successfully sync!") }
            }.onFailure {
                setState { copy(message = it.localizedMessage.orEmpty(), isLoading = false) }
            }
        }
    }


    @AssistedFactory
    interface Factory : AssistedViewModelFactory<AccountViewModel, UiState> {
        override fun create(state: UiState): AccountViewModel
    }

    companion object :
        MavericksViewModelFactory<AccountViewModel, UiState> by hiltMavericksViewModelFactory()
}
