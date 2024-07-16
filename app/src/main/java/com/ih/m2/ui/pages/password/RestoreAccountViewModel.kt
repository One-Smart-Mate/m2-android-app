package com.ih.m2.ui.pages.password

import android.util.Log
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.m2.ui.utils.EMPTY
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class RestoreAccountViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
) : MavericksViewModel<RestoreAccountViewModel.UiState>(initialState) {


    data class UiState(
        val isLoading: Boolean = false,
        val message: String = EMPTY,
        val currentStep: Int = 1,
        val email: String = EMPTY,
        val code: String = EMPTY,
        val password: String = EMPTY,
        val confirmPassword: String = EMPTY,
        val isComplete: Boolean = false,
        val canResend: Boolean = true
    ) : MavericksState

    sealed class Action {
        data object ClearMessage : Action()
        data class OnEmailChange(val email: String) : Action()
        data class OnActionClick(val action: String): Action()
        data class OnCodeChange(val code: String): Action()
        data class OnPasswordChange(val password: String) : Action()
        data class OnConfirmPasswordChange(val password: String) : Action()
    }

    fun process(action: Action) {
        when(action) {
            is Action.ClearMessage -> setState { copy(message = EMPTY) }
            is Action.OnEmailChange -> setState { copy(email = action.email) }
            is Action.OnActionClick -> handleOnAction(action.action)
            is Action.OnCodeChange -> setState { copy(code = action.code) }
            is Action.OnPasswordChange -> setState { copy(password = action.password) }
            is Action.OnConfirmPasswordChange -> setState { copy(confirmPassword = action.password) }
        }
    }

    private fun handleOnAction(action: String) {
        Log.e("test","Action $action")
        setState { copy(isLoading = true) }
        viewModelScope.launch(coroutineContext) {
            val state = stateFlow.first()
            when(action) {
                "email_check" -> {
                    if (state.email.isEmpty()) {
                        setState { copy(isLoading = false, message= "Enter a valid email") }
                        return@launch
                    }
                    handleCheckEmail()
                }
                "code_check" -> {
                    if (state.code.isEmpty()) {
                        setState { copy(isLoading = false, message= "Enter a valid code") }
                        return@launch
                    }
                    handleCheckCode()
                }
                "password_check" -> {
                    if (state.password.isEmpty() || state.confirmPassword.isEmpty()) {
                        setState { copy(isLoading = false, message= "Enter a valid password") }
                        return@launch
                    }
                    if (state.password != state.confirmPassword) {
                        setState { copy(isLoading = false, message= "Different passwords") }
                        return@launch
                    }
                    handleChangePassword()
                }
                "resend_check" -> {
                    handleResendCode()
                }
                else -> {}
            }

        }
    }

    private fun handleCheckEmail() {
        setState { copy(isLoading = false, currentStep = 2) }
    }
    private fun handleCheckCode() {
        setState { copy(isLoading = false, currentStep = 3) }
    }

    private fun handleChangePassword() {
        setState { copy(isLoading = false, isComplete = true) }
    }

    private fun handleResendCode() {
        setState { copy(canResend = false) }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<RestoreAccountViewModel, UiState> {
        override fun create(state: UiState): RestoreAccountViewModel
    }

    companion object :
        MavericksViewModelFactory<RestoreAccountViewModel, UiState> by hiltMavericksViewModelFactory()
}
