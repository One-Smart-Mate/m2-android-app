package com.ih.m2.ui.pages.cardlist

import android.content.Context
import android.util.Log
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.m2.R
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.filterByStatus
import com.ih.m2.domain.model.isAnomalies
import com.ih.m2.domain.model.isBehavior
import com.ih.m2.domain.model.toAnomaliesList
import com.ih.m2.domain.model.toBehaviorList
import com.ih.m2.domain.usecase.card.GetCardsUseCase
import com.ih.m2.domain.usecase.card.GetCardsZoneUseCase
import com.ih.m2.domain.usecase.user.GetUserUseCase
import com.ih.m2.ui.extensions.defaultIfNull
import com.ih.m2.ui.extensions.toFilterStatus
import com.ih.m2.ui.utils.CARD_ANOMALIES
import com.ih.m2.ui.utils.CARD_BEHAVIOR
import com.ih.m2.ui.utils.EMPTY
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CardListViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
    private val getCardsUseCase: GetCardsUseCase,
    @ApplicationContext private val context: Context,
    private val getUserUseCase: GetUserUseCase,
    private val getCardsZoneUseCase: GetCardsZoneUseCase
) : MavericksViewModel<CardListViewModel.UiState>(initialState) {

    data class UiState(
        val cards: List<Card> = emptyList(),
        val title: String = EMPTY,
        val isLoading: Boolean = false,
        val message: String = EMPTY,
        val refreshCards: Boolean = true,
        val filter: String = EMPTY,
        val cardTypes: String = EMPTY
    ) : MavericksState

    sealed class Action {
        data class GetCards(val filter: String) : Action()
        data object OnRefreshCards : Action()
        data class OnFilterChange(val filter: String) : Action()
        data object OnApplyFilterClick : Action()
    }

    fun process(action: Action) {
        when (action) {
            is Action.GetCards -> validateFilter(action.filter)
            is Action.OnRefreshCards -> setState { copy(refreshCards = true) }
            is Action.OnFilterChange -> setState { copy(filter = action.filter) }
            is Action.OnApplyFilterClick -> handleOnApplyFilterClick()
        }
    }

    private fun validateFilter(filter: String) {
        val isFromQr = filter.contains(":")
        if (isFromQr) {
            val superiorId = filter.substringAfter(":")
            handleGetCardsZone(superiorId)
        } else {
            handleGetCards(filter)
        }
    }

    private fun handleGetCards(filter: String) {
        setState { copy(isLoading = true, message = context.getString(R.string.loading_data)) }
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getCardsUseCase()
            }.onSuccess {
                val filterCards = when (filter) {
                    CARD_ANOMALIES -> {
                        it.toAnomaliesList()
                    }

                    CARD_BEHAVIOR -> {
                        it.toBehaviorList()
                    }

                    else -> it
                }.sortedByDescending { item -> item.siteCardId }
                setState {
                    copy(
                        cards = filterCards,
                        isLoading = false,
                        message = EMPTY,
                        title = getTitle(filter),
                        refreshCards = false,
                        cardTypes = filter
                    )
                }
            }.onFailure {
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }

    private fun handleOnApplyFilterClick() {
        viewModelScope.launch(coroutineContext) {
            val filter = stateFlow.first().filter
            if (filter.isEmpty()) {
                handleGetCards(stateFlow.first().cardTypes)
            } else {
                val cards = getCardsUseCase()
                val user = getUserUseCase()
                val filteredList =
                    cards.filterByStatus(filter.toFilterStatus(context), user?.userId.orEmpty())
                setState { copy(cards = filteredList) }
            }
        }
    }

    private fun getTitle(filter: String): String {
        return when (filter) {
            CARD_ANOMALIES -> context.getString(R.string.anomalies_cards)
            CARD_BEHAVIOR -> context.getString(R.string.behaviour_cards)
            else -> context.getString(R.string.cards)
        }
    }

    private fun handleGetCardsZone(superiorId: String) {
        setState { copy(isLoading = true, message = context.getString(R.string.loading_data)) }
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getCardsZoneUseCase(superiorId)
            }.onSuccess {
                setState {
                    val isBehavior = it.firstOrNull()?.isBehavior().defaultIfNull(false)
                    val isAnomalies = it.firstOrNull()?.isAnomalies().defaultIfNull(false)
                    val result = if (isBehavior) {
                        Pair(it.toBehaviorList(), CARD_BEHAVIOR)
                    } else if (isAnomalies) {
                        Pair(it.toAnomaliesList(), CARD_ANOMALIES)
                    } else {
                        Pair(it, EMPTY)
                    }
                    Log.e("test", "Cards zones $it -- $isBehavior ?? $isAnomalies")
                    copy(
                        cards = result.first,
                        isLoading = false,
                        message = EMPTY,
                        title = getTitle(result.second),
                        refreshCards = false,
                        cardTypes = result.second
                    )
                }
            }.onFailure {
                cleanScreenStates()
            }
        }
    }

    private fun cleanScreenStates(message: String = EMPTY) {
        setState {
            copy(
                isLoading = false,
                message = message,
                refreshCards = false
            )
        }
    }


    @AssistedFactory
    interface Factory : AssistedViewModelFactory<CardListViewModel, UiState> {
        override fun create(state: UiState): CardListViewModel
    }

    companion object :
        MavericksViewModelFactory<CardListViewModel, UiState> by hiltMavericksViewModelFactory()
}