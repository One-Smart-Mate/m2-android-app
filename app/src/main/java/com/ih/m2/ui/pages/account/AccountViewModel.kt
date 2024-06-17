package com.ih.m2.ui.pages.account

import android.content.Context
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class AccountViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
) : MavericksViewModel<AccountViewModel.UiState>(initialState) {


    data class UiState(
        val logout: Boolean = false
    ) : MavericksState

    sealed class Action {

    }

    fun process(action: Action) {

    }


    @AssistedFactory
    interface Factory : AssistedViewModelFactory<AccountViewModel, UiState> {
        override fun create(state: UiState): AccountViewModel
    }

    companion object :
        MavericksViewModelFactory<AccountViewModel, UiState> by hiltMavericksViewModelFactory()
}
