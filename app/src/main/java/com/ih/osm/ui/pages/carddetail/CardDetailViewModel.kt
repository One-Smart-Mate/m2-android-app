package com.ih.osm.ui.pages.carddetail

import android.util.Log
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.osm.core.file.FileHelper
import com.ih.osm.core.ui.LCE
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.usecase.card.GetCardDetailUseCase
import com.ih.osm.domain.usecase.employee.GetEmployeesUseCase
import com.ih.osm.ui.utils.EMPTY
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.launch

class CardDetailViewModel
@AssistedInject
constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
    private val getCardDetailUseCase: GetCardDetailUseCase,
    private val fileHelper: FileHelper,
    private val getEmployeesUseCase: GetEmployeesUseCase
) : MavericksViewModel<CardDetailViewModel.UiState>(initialState) {
    data class UiState(
        val card: LCE<Card> = LCE.Uninitialized,
        val message: String = EMPTY
    ) : MavericksState

    sealed class Action {
        data class GetCardDetail(val uuid: String) : Action()
        data object ClearMessage : Action()
    }

    fun process(action: Action) {
        when (action) {
            is Action.GetCardDetail -> handleGetCardDetail(action.uuid)
            is Action.ClearMessage -> setState { copy(message = EMPTY) }
        }
    }

    private fun handleGetCardDetail(uuid: String) {
        setState { copy(card = LCE.Loading) }
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getCardDetailUseCase(uuid = uuid)
            }.onSuccess {
                Log.e("test", "$uuid ---- $it")
                it?.let {
                    setState { copy(card = LCE.Success(it)) }
                } ?: setState { copy(card = LCE.Fail("Card with $uuid not found!")) }
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
