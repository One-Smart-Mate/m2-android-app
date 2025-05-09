package com.ih.osm.ui.pages.profile

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ih.osm.R
import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.core.ui.LCE
import com.ih.osm.domain.model.User
import com.ih.osm.domain.usecase.user.GetUserUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val getUserUseCase: GetUserUseCase,
        private val sharedPreferences: SharedPreferences,
        @ApplicationContext private val context: Context,
    ) : BaseViewModel<ProfileViewModel.UiState>(UiState()) {
        data class UiState(
            val state: LCE<User> = LCE.Loading,
            val subscriptionText: String = "",
        )

        init {
            handleGetUser()
        }

        private fun handleGetUser() {
            setState { copy(state = LCE.Loading) }
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { getUserUseCase() }
                }.onSuccess { user ->
                    user?.let {
                        val dueDateString = sharedPreferences.getDueDate()
                        val remainingDays = calculateRemainingDays(dueDateString)
                        val subscriptionText = context.getString(R.string.subscription_remaining_days, remainingDays)
                        setState {
                            copy(
                                state = LCE.Success(it),
                                subscriptionText = subscriptionText,
                            )
                        }
                    }
                }.onFailure {
                    setState { copy(state = LCE.Fail(it.localizedMessage.orEmpty())) }
                }
            }
        }

        private fun calculateRemainingDays(dueDateString: String): Int {
            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dueDate = sdf.parse(dueDateString)
                val today = Date()

                val diffInMillis = dueDate.time - today.time
                (diffInMillis / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
            } catch (e: Exception) {
                0
            }
        }
    }
