package com.ih.osm.ui.pages.password

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ih.osm.R
import com.ih.osm.core.notifications.NotificationManager
import com.ih.osm.data.model.RestorePasswordRequest
import com.ih.osm.domain.usecase.password.ResetPasswordUseCase
import com.ih.osm.domain.usecase.password.SendRestorePasswordCodeUseCase
import com.ih.osm.domain.usecase.password.VerifyPasswordCodeUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.pages.password.action.RestoreAction
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestoreAccountViewModel
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val sendRestorePasswordCodeUseCase: SendRestorePasswordCodeUseCase,
        private val verifyPasswordCodeUseCase: VerifyPasswordCodeUseCase,
        private val resetPasswordUseCase: ResetPasswordUseCase,
        private val notificationManager: NotificationManager,
    ) : BaseViewModel<RestoreAccountViewModel.UiState>(UiState()) {
        data class UiState(
            val isLoading: Boolean = false,
            val message: String = EMPTY,
            val currentStep: Int = 1,
            val email: String = EMPTY,
            val code: String = EMPTY,
            val password: String = EMPTY,
            val confirmPassword: String = EMPTY,
            val isComplete: Boolean = false,
            val canResend: Boolean = true,
        )

        fun process(action: RestoreAction) {
            when (action) {
                is RestoreAction.SetEmail -> setState { copy(email = action.email) }
                is RestoreAction.SetAction -> handleOnAction(action.action)
                is RestoreAction.SetCode -> setState { copy(code = action.code) }
                is RestoreAction.SetPassword -> setState { copy(password = action.password) }
                is RestoreAction.ConfirmPassword -> setState { copy(confirmPassword = action.password) }
            }
        }

        private fun handleOnAction(action: String) {
            setState { copy(isLoading = true) }
            viewModelScope.launch {
                val state = getState()
                when (action) {
                    "email_check" -> {
                        if (state.email.isEmpty()) {
                            setState {
                                copy(
                                    isLoading = false,
                                    message = context.getString(R.string.enter_a_valid_email),
                                )
                            }
                            return@launch
                        }
                        handleCheckEmail()
                    }

                    "code_check" -> {
                        if (state.code.isEmpty()) {
                            setState {
                                copy(
                                    isLoading = false,
                                    message = context.getString(R.string.enter_a_valid_code),
                                )
                            }
                            return@launch
                        }
                        handleCheckCode()
                    }

                    "password_check" -> {
                        if (state.password.isEmpty() || state.confirmPassword.isEmpty()) {
                            setState {
                                copy(
                                    isLoading = false,
                                    message = context.getString(R.string.enter_a_valid_password),
                                )
                            }
                            return@launch
                        }
                        if (state.password != state.confirmPassword) {
                            setState {
                                copy(
                                    isLoading = false,
                                    message = context.getString(R.string.different_passwords),
                                )
                            }
                            return@launch
                        }
                        if (state.password.length <= 7) {
                            setState {
                                copy(
                                    isLoading = false,
                                    message =
                                        context.getString(
                                            R.string.password_must_be_at_least_8_characters,
                                        ),
                                )
                            }
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
            viewModelScope.launch {
                val email = getState().email
                val request = RestorePasswordRequest(email = email)
                kotlin.runCatching {
                    callUseCase { sendRestorePasswordCodeUseCase(request) }
                }.onSuccess {
                    setState { copy(isLoading = false, currentStep = 2) }
                }.onFailure {
                    setState {
                        copy(
                            isLoading = false,
                            message = getErrorMessage(it.localizedMessage.orEmpty()),
                        )
                    }
                }
            }
        }

        private fun handleCheckCode() {
            viewModelScope.launch {
                val state = getState()
                val request = RestorePasswordRequest(email = state.email, resetCode = state.code)
                kotlin.runCatching {
                    callUseCase { verifyPasswordCodeUseCase(request) }
                }.onSuccess {
                    setState { copy(isLoading = false, currentStep = 3) }
                }.onFailure {
                    setState {
                        copy(
                            isLoading = false,
                            message = getErrorMessage(it.localizedMessage.orEmpty()),
                        )
                    }
                }
            }
        }

        private fun handleChangePassword() {
            viewModelScope.launch {
                val state = getState()
                val request =
                    RestorePasswordRequest(
                        email = state.email,
                        resetCode = state.code,
                        newPassword = state.password,
                    )
                kotlin.runCatching {
                    callUseCase { resetPasswordUseCase(request) }
                }.onSuccess {
                    notificationManager.buildNotificationSuccessChangePassword()
                    setState { copy(isLoading = false, isComplete = true) }
                }.onFailure {
                    setState {
                        copy(
                            isLoading = false,
                            message = getErrorMessage(it.localizedMessage.orEmpty()),
                        )
                    }
                }
            }
        }

        private fun handleResendCode() {
            setState { copy(canResend = false) }
            handleCheckEmail()
        }

        private fun getErrorMessage(error: String): String {
            return when (error) {
                "User not found" -> context.getString(R.string.user_not_found)
                "Wrong reset code" -> context.getString(R.string.wrong_reset_code)
                "ResetCode must be longer than or equal to 6 characters" ->
                    context.getString(
                        R.string.code_must_be_longer_or_equal_to_6_characters,
                    )

                "NewPassword must be longer than or equal to 8 characters" ->
                    context.getString(
                        R.string.password_must_be_at_least_8_characters,
                    )

                else -> context.getString(R.string.something_went_wrong)
            }
        }

        fun cleanMessage() {
            setState { copy(message = EMPTY) }
        }
    }
