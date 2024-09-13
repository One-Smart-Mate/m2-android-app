package com.ih.osm.ui.pages.login

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.osm.data.model.LoginRequest
import com.ih.osm.domain.model.User
import com.ih.osm.domain.usecase.firebase.SyncFirebaseTokenUseCase
import com.ih.osm.domain.usecase.login.LoginUseCase
import com.ih.osm.domain.usecase.saveuser.SaveUserUseCase
import com.ih.osm.ui.utils.EMPTY
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoginViewModel
    @AssistedInject
    constructor(
        @Assisted initialState: UiState,
        private val coroutineContext: CoroutineContext,
        private val loginUseCase: LoginUseCase,
        private val saveUserUseCase: SaveUserUseCase,
        private val syncFirebaseTokenUseCase: SyncFirebaseTokenUseCase,
    ) : MavericksViewModel<LoginViewModel.UiState>(initialState) {
        data class UiState(
            val isLoading: Boolean = false,
            val message: String = EMPTY,
            val isAuthenticated: Boolean = false,
            val email: String = EMPTY,
            val password: String = EMPTY,
        ) : MavericksState

        sealed class Action {
            data class Login(val email: String, val password: String) : Action()

            data class SetEmail(val email: String) : Action()

            data class SetPassword(val password: String) : Action()

            data object ClearMessage : Action()
        }

        fun process(action: Action) {
            when (action) {
                is Action.Login -> handleLogin(action.email, action.password)
                is Action.SetEmail -> handleSetEmail(action.email)
                is Action.SetPassword -> handleSetPassword(action.password)
                is Action.ClearMessage -> setState { copy(message = EMPTY) }
            }
        }

        private fun handleLogin(
            email: String,
            password: String,
        ) {
            setState { copy(isLoading = true) }
            viewModelScope.launch(coroutineContext) {
                kotlin.runCatching {
                    loginUseCase(LoginRequest(email, password))
                }.onSuccess {
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
            viewModelScope.launch(coroutineContext) {
                kotlin.runCatching {
                    saveUserUseCase(user)
                }.onSuccess {
                    handleSyncFirebaseToken()
                    setState { copy(isLoading = false, isAuthenticated = true) }
                }.onFailure {
                    setState { copy(isLoading = false, message = it.localizedMessage.orEmpty()) }
                }
            }
        }

        private fun handleSyncFirebaseToken() {
            viewModelScope.launch(coroutineContext) {
                kotlin.runCatching {
                    syncFirebaseTokenUseCase()
                }
            }
        }

        private fun handleSetEmail(email: String) {
            setState { copy(email = email) }
        }

        private fun handleSetPassword(password: String) {
            setState { copy(password = password) }
        }

        @AssistedFactory
        interface Factory : AssistedViewModelFactory<LoginViewModel, UiState> {
            override fun create(state: UiState): LoginViewModel
        }

        companion object :
            MavericksViewModelFactory<LoginViewModel, UiState> by hiltMavericksViewModelFactory()
    }
