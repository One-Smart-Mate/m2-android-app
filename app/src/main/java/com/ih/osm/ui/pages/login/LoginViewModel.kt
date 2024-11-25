package com.ih.osm.ui.pages.login

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ih.osm.R
import com.ih.osm.core.file.FileHelper
import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.data.model.LoginRequest
import com.ih.osm.domain.model.User
import com.ih.osm.domain.usecase.firebase.SyncFirebaseTokenUseCase
import com.ih.osm.domain.usecase.login.LoginUseCase
import com.ih.osm.domain.usecase.saveuser.SaveUserUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.pages.login.action.LoginAction
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val loginUseCase: LoginUseCase,
        private val saveUserUseCase: SaveUserUseCase,
        private val syncFirebaseTokenUseCase: SyncFirebaseTokenUseCase,
        private val fileHelper: FileHelper,
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

                kotlin.runCatching {
                    callUseCase { loginUseCase(LoginRequest(email, password)) }
                }.onSuccess {
                    fileHelper.logUser(it)
                    handleSaveUser(it)
                }.onFailure {
                    setState {
                        copy(
                            isLoading = false,
                            message = it.localizedMessage.orEmpty(),
                        )
                    }
                }
            }
        }

        private fun handleSaveUser(user: User) {
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { saveUserUseCase(user) }
                }.onSuccess {
                    handleSyncFirebaseToken()
                    setState { copy(isLoading = false, isAuthenticated = true) }
                }.onFailure {
                    setState { copy(isLoading = false, message = it.localizedMessage.orEmpty()) }
                }
            }
        }

        private fun handleSyncFirebaseToken() {
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { syncFirebaseTokenUseCase() }
                }
            }
        }
    }
