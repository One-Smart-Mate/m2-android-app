package com.ih.m2.ui.pages.login

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.m2.data.model.LoginRequest
import com.ih.m2.domain.usecase.LoginUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


class LoginViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
    private val loginUseCase: LoginUseCase,
) : MavericksViewModel<LoginViewModel.UiState>(initialState) {


    data class UiState(
        val isLoading: Boolean = false
    ): MavericksState

    sealed class Action {
        data class Login(val email: String, val password: String): Action()
    }

    fun process(action: Action) {
        when (action) {
            is Action.Login -> handleLogin(action.email, action.password)
        }
    }

    private fun handleLogin(email: String, password: String) {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                loginUseCase(LoginRequest(email, password))
            }.onSuccess {
                Log.e("","Data $it")
            }.onFailure {
                Log.e("","Error ${it.localizedMessage}")
            }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<LoginViewModel, UiState> {
        override fun create(state: UiState): LoginViewModel
    }

    companion object : MavericksViewModelFactory<LoginViewModel, UiState> by hiltMavericksViewModelFactory()
}





