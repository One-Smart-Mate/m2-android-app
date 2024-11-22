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

    init {
        handleGeCards()
    }

//    sealed class Action {
//        data class GetCards(val filter: String) : Action()
//        data object OnRefreshCards : Action()
//        data class OnFilterChange(val filter: String) : Action()
//        data object OnRefreshCardList : Action()
//        data object ClearMessage : Action()
//    }
//
//    fun process(action: Action) {
//        when (action) {
//            is Action.GetCards -> validateFilter(action.filter)
//            is Action.OnRefreshCards -> setState { copy(refreshCards = true) }
//            is Action.OnFilterChange -> handleOnApplyFilterClick(action.filter)
//            is Action.OnRefreshCardList -> handleOnRefreshCardList()
//            is Action.ClearMessage -> setState { copy(message = EMPTY) }
//        }
//    }
//
//    private fun validateFilter(filter: String) {
//        setState { copy(refreshCards = false) }
//        val isFromQr = filter.contains(":")
//        if (isFromQr) {
//            val levelMachine = filter.substringAfter(":")
//            handleGetCardsLevelMachine(levelMachine)
//        } else {
//            handleGetCards(filter)
//        }
//    }
//
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

//
//    private fun handleOnApplyFilterClick(filter: String) {
//        setState { copy(filter = filter, isRefreshing = true) }
//        viewModelScope.launch(coroutineContext) {
//            if (filter.isEmpty()) {
//                handleGetCards(stateFlow.first().filter)
//            } else {
//                val cards = getCardsUseCase()
//                val user = getUserUseCase()
//                val filteredList =
//                    cards.filterByStatus(filter.toFilterStatus(context), user?.userId.orEmpty())
//                setState { copy(cards = filteredList, isRefreshing = false) }
//            }
//        }
//    }
//
//    private fun getTitle(filter: String): String {
//        return when (filter) {
//            CARD_ANOMALIES -> context.getString(R.string.anomalies_cards)
//            else -> context.getString(R.string.cards)
//        }
//    }
//
//    private fun handleGetCardsLevelMachine(levelMachine: String) {
//        setState { copy(isLoading = true, message = context.getString(R.string.loading_data)) }
//        viewModelScope.launch(coroutineContext) {
//            kotlin.runCatching {
//                getCardsLevelMachineUseCase(levelMachine)
//            }.onSuccess {
//                setState {
//                    val isAnomalies = it.firstOrNull()?.isAnomalies().defaultIfNull(false)
//                    val result =
//                        if (isAnomalies) {
//                            Pair(it.toAnomaliesList(), CARD_ANOMALIES)
//                        } else {
//                            Pair(it, EMPTY)
//                        }
//                    copy(
//                        cards = result.first,
//                        isLoading = false,
//                        message = EMPTY,
//                        title = getTitle(result.second),
//                        refreshCards = false,
//                        filter = result.second
//                    )
//                }
//            }.onFailure {
//                cleanScreenStates()
//            }
//        }
//    }
//
    private fun cleanScreenStates(message: String = EMPTY) {
        setState {
            copy(
                isLoading = false,
                message = message,
            )
        }
    }
//
//    private fun handleOnRefreshCardList() {
//        setState { copy(isRefreshing = true) }
//        viewModelScope.launch {
//            if (NetworkConnection.isConnected().not()) {
//                setState {
//                    copy(
//                        isRefreshing = false,
//                        message = context.getString(R.string.please_connect_to_internet)
//                    )
//                }
//                return@launch
//            }
//            val state = stateFlow.first()
//            handleGetCards(state.filter, context.getString(R.string.syncing_remote_cards), true)
//        }
//    }
}
