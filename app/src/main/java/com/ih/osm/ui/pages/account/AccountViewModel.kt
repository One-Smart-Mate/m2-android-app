package com.ih.osm.ui.pages.account

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.ih.osm.BuildConfig
import com.ih.osm.R
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.data.model.Site
import com.ih.osm.domain.repository.firebase.FirebaseStorageRepository
import com.ih.osm.domain.usecase.catalogs.SyncCatalogsUseCase
import com.ih.osm.domain.usecase.logout.LogoutUseCase
import com.ih.osm.domain.usecase.session.GetSessionUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.extensions.getFileFromUri
import com.ih.osm.ui.extensions.toZip
import com.ih.osm.ui.pages.account.action.AccountAction
import com.ih.osm.ui.utils.EMPTY
import com.ih.osm.ui.utils.NETWORK_DATA_MOBILE
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AccountViewModel
    @Inject
    constructor(
        private val logoutUseCase: LogoutUseCase,
        private val syncCatalogsUseCase: SyncCatalogsUseCase,
        private val sharedPreferences: SharedPreferences,
        private val firebaseStorageRepository: FirebaseStorageRepository,
        private val getSessionUseCase: GetSessionUseCase,
        @ApplicationContext val context: Context,
    ) : BaseViewModel<AccountViewModel.UiState>(UiState()) {
        data class UiState(
            val logout: Boolean = false,
            val message: String = EMPTY,
            val isLoading: Boolean = false,
            val checked: Boolean = false,
            val uri: Uri? = null,
            val sites: List<Site> = emptyList(),
            val currentSiteId: String = EMPTY,
        )

        init {
            getNetworkPreferences()
            getLogFile()
            getSites()
        }

        private fun getLogFile() {
            viewModelScope.launch {
                val uri = LoggerHelperManager.getLogFile()
                setState { copy(uri = uri) }
            }
        }

        fun process(action: AccountAction) {
            when (action) {
                is AccountAction.Logout -> handleLogout()
                is AccountAction.SyncCatalogs -> handleSyncCatalogs()
                is AccountAction.SetSwitch -> handleOnSwitchChange(action.checked)
                is AccountAction.UploadLogs -> handleUploadLogs(action.uri)
                is AccountAction.SelectSite -> handleSelectSite(action.site)
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
            setState { copy(isLoading = true) }
            viewModelScope.launch {
                kotlin
                    .runCatching {
                        callUseCase { logoutUseCase() }
                    }.onSuccess {
                        setState { copy(logout = true) }
                    }.onFailure {
                        LoggerHelperManager.logException(it)
                        setState { copy(message = it.localizedMessage.orEmpty(), isLoading = false) }
                    }
            }
        }

        private fun handleSyncCatalogs() {
            setState { copy(isLoading = true) }
            viewModelScope.launch {
                kotlin
                    .runCatching {
                        callUseCase { syncCatalogsUseCase(syncCards = false) }
                    }.onSuccess {
                        setState { copy(isLoading = false, message = "Successfully sync!") }
                    }.onFailure {
                        LoggerHelperManager.logException(it)
                        setState { copy(message = it.localizedMessage.orEmpty(), isLoading = false) }
                    }
            }
        }

        private fun handleUploadLogs(uri: Uri) {
            setState { copy(isLoading = true) }
            viewModelScope.launch {
                kotlin
                    .runCatching {
                        val session = getSessionUseCase()
                        val userId = session.userId
                        val appVersion = BuildConfig.VERSION_NAME

                        val inputFile =
                            getFileFromUri(context, uri)
                                ?: throw IllegalArgumentException("Invalid log file")
                        val zipFile = File(context.cacheDir, "logs.zip")

                        inputFile.toZip(zipFile)

                        firebaseStorageRepository.uploadLogFile(
                            userId,
                            appVersion,
                            Uri.fromFile(zipFile),
                        )
                    }.onSuccess { url ->
                        setState {
                            copy(
                                isLoading = false,
                                message =
                                    if (url.isNotEmpty()) {
                                        context.getString(R.string.logs_sent_success)
                                    } else {
                                        context.getString(R.string.logs_sent_error)
                                    },
                            )
                        }
                    }.onFailure {
                        LoggerHelperManager.logException(it)
                        setState {
                            copy(
                                isLoading = false,
                                message = context.getString(R.string.logs_sent_error),
                            )
                        }
                    }.also {
                        val zipFile = File(context.cacheDir, "logs.zip")
                        if (zipFile.exists()) {
                            zipFile.delete()
                        }
                    }
            }
        }

        private fun getSites() {
            viewModelScope.launch {
                val sites = sharedPreferences.getSites()
                val currentSiteId = sharedPreferences.getCurrentSiteId()

                setState {
                    copy(
                        sites = sites,
                        currentSiteId = currentSiteId,
                    )
                }
            }
        }

        private fun handleSelectSite(site: Site) {
            viewModelScope.launch {
                sharedPreferences.saveCurrentSiteId(site.id)
                setState { copy(currentSiteId = site.id) }
            }
        }
    }
