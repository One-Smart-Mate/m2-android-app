package com.ih.m2.ui.pages.home

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.m2.domain.model.User
import com.ih.m2.domain.usecase.getuser.GetUserUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class HomeViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
    private val getUserUseCase: GetUserUseCase
) : MavericksViewModel<HomeViewModel.UiState>(initialState) {


    init {
        handleGetUser()
    }

    data class UiState(
        val user: User = User.mockUser(),
        val errorMessage: String = "",
        val isLoading: Boolean = false
    ) : MavericksState

    sealed class Action {
        data object GetUser: Action()
    }

    fun process(action: Action) {
        when(action) {
            is Action.GetUser -> handleGetUser()
        }
    }
    private fun handleGetUser() {
        setState { copy(isLoading = true) }
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getUserUseCase()
            }.onSuccess {
                it?.let { setState { copy(user = it, isLoading = false) } }
            }.onFailure {
                setState { copy(errorMessage = it.localizedMessage.orEmpty(), isLoading = false) }
            }
        }
    }


    @AssistedFactory
    interface Factory : AssistedViewModelFactory<HomeViewModel, UiState> {
        override fun create(state: UiState): HomeViewModel
    }

    companion object :
        MavericksViewModelFactory<HomeViewModel, UiState> by hiltMavericksViewModelFactory()
}
