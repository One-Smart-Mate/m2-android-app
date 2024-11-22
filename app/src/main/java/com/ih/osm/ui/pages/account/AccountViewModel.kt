package com.ih.osm.ui.pages.account

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.ih.osm.core.file.FileHelper
import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.domain.usecase.catalogs.SyncCatalogsUseCase
import com.ih.osm.domain.usecase.logout.LogoutUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.pages.account.action.AccountAction
import com.ih.osm.ui.utils.EMPTY
import com.ih.osm.ui.utils.NETWORK_DATA_MOBILE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val syncCatalogsUseCase: SyncCatalogsUseCase,
    private val sharedPreferences: SharedPreferences,
    private val fileHelper: FileHelper
) : BaseViewModel<AccountViewModel.UiState>(UiState()) {

    data class UiState(
        val logout: Boolean = false,
        val message: String = EMPTY,
        val isLoading: Boolean = false,
        val checked: Boolean = false,
        val uri: Uri? = null
    )

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

    fun process(action: AccountAction) {
        when (action) {
            is AccountAction.Logout -> handleLogout()
            is AccountAction.SyncCatalogs -> handleSyncCatalogs()
            is AccountAction.SetSwitch -> handleOnSwitchChange(action.checked)
        }
    }

    private fun handleOnSwitchChange(checked: Boolean) {
        val network =
            if (checked) {
                NETWORK_DATA_MOBILE
            } else {
                EMPTY
            }
        viewModelScope.launch {
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
        viewModelScope.launch {
            kotlin.runCatching {
                callUseCase { logoutUseCase() }
            }.onSuccess {
                setState { copy(logout = true) }
            }.onFailure {
                setState { copy(message = it.localizedMessage.orEmpty()) }
            }
        }
    }


    private fun handleSyncCatalogs() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            kotlin.runCatching {
                callUseCase { syncCatalogsUseCase(syncCards = false) }
            }.onSuccess {
                setState { copy(isLoading = false, message = "Successfully sync!") }
            }.onFailure {
                setState { copy(message = it.localizedMessage.orEmpty(), isLoading = false) }
            }
        }
    }
}
