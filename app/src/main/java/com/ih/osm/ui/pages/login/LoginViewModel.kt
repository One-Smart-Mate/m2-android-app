package com.ih.osm.ui.pages.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.R
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.data.model.LoginRequest
import com.ih.osm.data.model.toDomain
import com.ih.osm.data.model.toSession
import com.ih.osm.domain.model.Session
import com.ih.osm.domain.model.User
import com.ih.osm.domain.usecase.firebase.SyncFirebaseTokenUseCase
import com.ih.osm.domain.usecase.login.LoginUseCase
import com.ih.osm.domain.usecase.session.SaveSessionUseCase
import com.ih.osm.domain.usecase.user.SaveUserUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.pages.login.action.LoginAction
import com.ih.osm.ui.utils.ANDROID_SO
import com.ih.osm.ui.utils.EMPTY
import com.ih.osm.ui.utils.NETWORK_DATA_MOBILE
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val loginUseCase: LoginUseCase,
        private val saveUserUseCase: SaveUserUseCase,
        private val saveSessionUseCase: SaveSessionUseCase,
        private val syncFirebaseTokenUseCase: SyncFirebaseTokenUseCase,
        private val sharedPreferences: SharedPreferences,
        @ApplicationContext private val context: Context,
    ) : BaseViewModel<LoginViewModel.UiState>(UiState()) {
        data class UiState(
            val isLoading: Boolean = false,
            val message: String = EMPTY,
            val isAuthenticated: Boolean = false,
            val email: String = EMPTY,
            val password: String = EMPTY,
        )

        fun process(action: LoginAction) {
            when (action) {
                is LoginAction.Login -> checkConnection()
                is LoginAction.SetEmail -> setState { copy(email = action.email) }
                is LoginAction.SetPassword -> setState { copy(password = action.password) }
            }
        }

        fun cleanMessage() {
            setState { copy(message = EMPTY) }
        }

        private fun checkConnection() {
            viewModelScope.launch {
                setState { copy(isLoading = true) }
                if (NetworkConnection.isConnected().not()) {
                    setState {
                        copy(
                            isLoading = false,
                            message = context.getString(R.string.please_connect_to_internet),
                        )
                    }
                } else {
                    handleLogin()
                }
            }
        }

        private fun handleLogin() {
            viewModelScope.launch {
                val email = getState().email
                val password = getState().password
                val timezone = TimeZone.getDefault().id
                Log.e("test", " Email: $email, Password: $password, Timezone: $timezone")
                kotlin.runCatching {
                    callUseCase {
                        loginUseCase(
                            LoginRequest(
                                email = email,
                                password = password,
                                timezone = timezone,
                                platform = ANDROID_SO.uppercase(),
                            ),
                        )
                    }
                }.onSuccess { loginResponse ->
                    val user = loginResponse.toDomain()
                    val session = loginResponse.toSession()
                    LoggerHelperManager.logUser(user)
                    sharedPreferences.saveDueDate(loginResponse.data.dueDate.orEmpty())
                    handleSaveUserAndSession(user, session)
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    setState {
                        copy(
                            isLoading = false,
                            message = it.localizedMessage.orEmpty(),
                        )
                    }
                }
            }
        }

        private fun handleSaveUserAndSession(
            user: User,
            session: Session,
        ) {
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { saveUserUseCase(user) }
                    callUseCase { saveSessionUseCase(session) }
                }.onSuccess {
                    handleSyncFirebaseToken()
                    setDefaultPreferences()
                    setState { copy(isLoading = false, isAuthenticated = true) }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    setState { copy(isLoading = false, message = it.localizedMessage.orEmpty()) }
                }
            }
        }

        private fun setDefaultPreferences() {
            viewModelScope.launch {
                sharedPreferences.saveNetworkPreference(NETWORK_DATA_MOBILE)
            }
        }

        private suspend fun handleSyncFirebaseToken() {
            kotlin.runCatching {
                callUseCase { syncFirebaseTokenUseCase() }
            }.onFailure {
                LoggerHelperManager.logException(it)
                FirebaseCrashlytics.getInstance().recordException(it)
            }
        }
    }
