package com.ih.osm.ui.pages.cardlist

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.usecase.card.GetCardsUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class CardListViewModel @Inject constructor(
    private val getCardsUseCase: GetCardsUseCase
) : BaseViewModel<CardListViewModel.UiState>(UiState()) {

    data class UiState(
        val cards: List<Card> = emptyList(),
        val isLoading: Boolean = true,
        val message: String = EMPTY
    )

    fun load() {
        handleGeCards()
    }

    private fun handleGeCards() {
        viewModelScope.launch {
            kotlin.runCatching {
                callUseCase { getCardsUseCase(syncRemote = false) }
            }.onSuccess {
                setState {
                    copy(
                        cards = it.sortedByDescending { item -> item.siteCardId },
                        isLoading = false,
                        message = EMPTY
                    )
                }
            }.onFailure {
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }

    private fun cleanScreenStates(message: String = EMPTY) {
        setState {
            copy(
                isLoading = false,
                message = message,
            )
        }
    }
}
