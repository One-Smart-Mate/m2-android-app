package com.osm.ui.pages.carddetail

import android.util.Log
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.osm.core.file.FileHelper
import com.osm.core.ui.LCE
import com.osm.domain.model.Card
import com.osm.domain.usecase.card.GetCardDetailUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CardDetailViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
    private val getCardDetailUseCase: GetCardDetailUseCase,
    private val fileHelper: FileHelper
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
                Log.e("test","$cardId ---- $it")
                setState { copy(card = LCE.Success(it)) }
            }.onFailure {
                fileHelper.logException(it)
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