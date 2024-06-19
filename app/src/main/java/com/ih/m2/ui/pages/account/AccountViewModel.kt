package com.ih.m2.ui.pages.account

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.m2.domain.usecase.logout.LogoutUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AccountViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
    private val logoutUseCase: LogoutUseCase
) : MavericksViewModel<AccountViewModel.UiState>(initialState) {


    data class UiState(
        val logout: Boolean = false,
        val errorMessage: String = ""
    ) : MavericksState

    sealed class Action {
        data object Logout: Action()
    }

    fun process(action: Action) {
        when(action) {
            is Action.Logout -> handleLogout()
        }
    }
    private fun handleLogout() {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                logoutUseCase()
            }.onSuccess {
                setState { copy(logout = true) }
            }.onFailure {
                setState { copy(errorMessage = it.localizedMessage.orEmpty()) }
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
