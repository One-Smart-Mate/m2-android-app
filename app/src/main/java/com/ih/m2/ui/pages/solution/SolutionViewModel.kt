package com.ih.m2.ui.pages.solution

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.m2.ui.utils.EMPTY
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlin.coroutines.CoroutineContext

class SolutionViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
) : MavericksViewModel<SolutionViewModel.UiState>(initialState) {


    data class UiState(
        val solutionType: String = EMPTY,
        val cardId: String = EMPTY
    ) : MavericksState

    sealed class Action {
        data class SetSolutionInfo(val solutionType: String, val cardId: String): Action()

    }

    fun process(action: Action) {
        when(action) {
            is Action.SetSolutionInfo -> handleSetSolutionInfo(action.solutionType, action.cardId)
        }
    }

    private fun handleSetSolutionInfo(solutionType: String, cardId: String) {
        setState {
            copy(
                solutionType = solutionType,
                cardId = cardId
            )
        }
    }


    @AssistedFactory
    interface Factory : AssistedViewModelFactory<SolutionViewModel, UiState> {
        override fun create(state: UiState): SolutionViewModel
    }

    companion object :
        MavericksViewModelFactory<SolutionViewModel, UiState> by hiltMavericksViewModelFactory()
}
