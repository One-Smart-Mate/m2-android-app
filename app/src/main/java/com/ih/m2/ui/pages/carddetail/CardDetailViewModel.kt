package com.ih.m2.ui.pages.carddetail

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.m2.core.network.NetworkConnection
import com.ih.m2.core.ui.LCE
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.usecase.card.GetCardDetailUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CardDetailViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
    private val getCardDetailUseCase: GetCardDetailUseCase
) : MavericksViewModel<CardDetailViewModel.UiState>(initialState) {


    data class UiState(
        val card: LCE<Card> = LCE.Uninitialized
    ) : MavericksState

    sealed class Action {
        data class GetCardDetail(val cardId: String) : Action()
    }

    fun process(action: Action) {
        when (action) {
            is Action.GetCardDetail -> handleGetCardDetail(action.cardId)
        }

    }

    private fun handleGetCardDetail(cardId: String) {
        setState { copy(card = LCE.Loading) }
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getCardDetailUseCase(cardId = cardId)
            }.onSuccess {
                setState { copy(card = LCE.Success(it)) }
            }.onFailure {
                setState { copy(card = LCE.Fail(it.localizedMessage.orEmpty())) }
            }
        }
    }


    @AssistedFactory
    interface Factory : AssistedViewModelFactory<CardDetailViewModel, UiState> {
        override fun create(state: UiState): CardDetailViewModel
    }

    companion object :
        MavericksViewModelFactory<CardDetailViewModel, UiState> by hiltMavericksViewModelFactory()
}