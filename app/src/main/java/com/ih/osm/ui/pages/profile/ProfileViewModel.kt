package com.ih.osm.ui.pages.profile

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.osm.core.ui.LCE
import com.ih.osm.domain.model.User
import com.ih.osm.domain.usecase.user.GetUserUseCase
import com.ih.osm.ui.utils.EMPTY
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ProfileViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
    private val getUserUseCase: GetUserUseCase
) : MavericksViewModel<ProfileViewModel.UiState>(initialState) {


    data class UiState(
        val state: LCE<User> = LCE.Uninitialized,
    ): MavericksState

    init {
        process(Action.GetUser)
    }

    sealed class Action {
        data object GetUser: Action()
    }

    fun process(action: Action) {
        when(action) {
            is Action.GetUser -> handleGetUser()
            else -> {}
        }
    }

    private fun handleGetUser() {
        setState { copy(state = LCE.Loading) }
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getUserUseCase()
            }.onSuccess {
                it?.let {
                    setState { copy(state = LCE.Success(it)) }
                }
            }.onFailure {
                setState { copy(state = LCE.Fail(it.localizedMessage.orEmpty())) }
            }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<ProfileViewModel, UiState> {
        override fun create(state: UiState): ProfileViewModel
    }

    companion object :
        MavericksViewModelFactory<ProfileViewModel, UiState> by hiltMavericksViewModelFactory()
}