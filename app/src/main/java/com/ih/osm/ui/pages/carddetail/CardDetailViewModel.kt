package com.ih.osm.ui.pages.carddetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ih.osm.core.file.FileHelper
import com.ih.osm.core.ui.LCE
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.usecase.card.GetCardDetailUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.navigation.ARG_CARD_ID
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class CardDetailViewModel @Inject constructor(
    private val getCardDetailUseCase: GetCardDetailUseCase,
    private val fileHelper: FileHelper,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<CardDetailViewModel.UiState>(UiState()) {

    data class UiState(
        val state: LCE<Card> = LCE.Loading,
        val message: String = EMPTY
    )

    init {
        val uuid = savedStateHandle.get<String>(ARG_CARD_ID).orEmpty()
        handleGetCardDetail(uuid)
    }

    private fun handleGetCardDetail(uuid: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                callUseCase { getCardDetailUseCase(uuid = uuid) }
            }.onSuccess {
                setState { copy(state = LCE.Success(it)) }
            }.onFailure {
                fileHelper.logException(it)
                setState { copy(state = LCE.Fail(it.localizedMessage.orEmpty())) }
            }
        }
    }
}
