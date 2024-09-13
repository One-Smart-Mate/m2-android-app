package com.ih.osm.ui.pages.cardlist

import android.content.Context
import android.util.Log
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.osm.R
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.filterByStatus
import com.ih.osm.domain.model.isAnomalies
import com.ih.osm.domain.model.toAnomaliesList
import com.ih.osm.domain.usecase.card.GetCardsLevelMachineUseCase
import com.ih.osm.domain.usecase.card.GetCardsUseCase
import com.ih.osm.domain.usecase.user.GetUserUseCase
import com.ih.osm.ui.extensions.defaultIfNull
import com.ih.osm.ui.extensions.toFilterStatus
import com.ih.osm.ui.utils.CARD_ANOMALIES
import com.ih.osm.ui.utils.EMPTY
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CardListViewModel
    @AssistedInject
    constructor(
        @Assisted initialState: UiState,
        private val coroutineContext: CoroutineContext,
        private val getCardsUseCase: GetCardsUseCase,
        @ApplicationContext private val context: Context,
        private val getUserUseCase: GetUserUseCase,
        private val getCardsLevelMachineUseCase: GetCardsLevelMachineUseCase,
    ) : MavericksViewModel<CardListViewModel.UiState>(initialState) {
        data class UiState(
            val cards: List<Card> = emptyList(),
            val title: String = EMPTY,
            val isLoading: Boolean = true,
            val message: String = EMPTY,
            val refreshCards: Boolean = true,
            val filter: String = EMPTY,
            val isRefreshing: Boolean = false,
        ) : MavericksState

        sealed class Action {
            data class GetCards(val filter: String) : Action()

            data object OnRefreshCards : Action()

            data class OnFilterChange(val filter: String) : Action()

            data object OnRefreshCardList : Action()
        }

        fun process(action: Action) {
            when (action) {
                is Action.GetCards -> validateFilter(action.filter)
                is Action.OnRefreshCards -> setState { copy(refreshCards = true) }
                is Action.OnFilterChange -> handleOnApplyFilterClick(action.filter)
                is Action.OnRefreshCardList -> handleOnRefreshCardList()
            }
        }

        private fun validateFilter(filter: String) {
            val isFromQr = filter.contains(":")
            if (isFromQr) {
                val levelMachine = filter.substringAfter(":")
                handleGetCardsLevelMachine(levelMachine)
            } else {
                handleGetCards(filter)
            }
        }

        private fun handleGetCards(
            filter: String,
            message: String = context.getString(R.string.loading_data),
            isRefreshing: Boolean = false,
        ) {
            if (isRefreshing.not()) {
                setState { copy(isLoading = true, message = message) }
            }
            viewModelScope.launch(coroutineContext) {
                kotlin.runCatching {
                    getCardsUseCase(syncRemote = stateFlow.first().isRefreshing)
                }.onSuccess {
                    val filterCards =
                        when (filter) {
                            CARD_ANOMALIES -> {
                                it.toAnomaliesList()
                            }

                            else -> it
                        }.sortedByDescending { item -> item.siteCardId }
                    setState {
                        Log.e("test", "Filter $filter - $isRefreshing")
                        copy(
                            cards = filterCards,
                            isLoading = false,
                            message = EMPTY,
                            title = getTitle(filter),
                            refreshCards = false,
                            filter = filter,
                            isRefreshing = false,
                        )
                    }
                }.onFailure {
                    cleanScreenStates(it.localizedMessage.orEmpty())
                }
            }
        }

        private fun handleOnApplyFilterClick(filter: String) {
            setState { copy(filter = filter, isRefreshing = true) }
            viewModelScope.launch(coroutineContext) {
                if (filter.isEmpty()) {
                    handleGetCards(stateFlow.first().filter)
                } else {
                    val cards = getCardsUseCase()
                    val user = getUserUseCase()
                    val filteredList =
                        cards.filterByStatus(filter.toFilterStatus(context), user?.userId.orEmpty())
                    setState { copy(cards = filteredList, isRefreshing = false) }
                }
            }
        }

        private fun getTitle(filter: String): String {
            return when (filter) {
                CARD_ANOMALIES -> context.getString(R.string.anomalies_cards)
                else -> context.getString(R.string.cards)
            }
        }

        private fun handleGetCardsLevelMachine(levelMachine: String) {
            setState { copy(isLoading = true, message = context.getString(R.string.loading_data)) }
            viewModelScope.launch(coroutineContext) {
                kotlin.runCatching {
                    getCardsLevelMachineUseCase(levelMachine)
                }.onSuccess {
                    setState {
                        val isAnomalies = it.firstOrNull()?.isAnomalies().defaultIfNull(false)
                        val result =
                            if (isAnomalies) {
                                Pair(it.toAnomaliesList(), CARD_ANOMALIES)
                            } else {
                                Pair(it, EMPTY)
                            }
                        copy(
                            cards = result.first,
                            isLoading = false,
                            message = EMPTY,
                            title = getTitle(result.second),
                            refreshCards = false,
                            filter = result.second,
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
                    refreshCards = false,
                )
            }
        }

        private fun handleOnRefreshCardList() {
            setState { copy(isRefreshing = true) }
            viewModelScope.launch {
                val state = stateFlow.first()
                handleGetCards(state.filter, context.getString(R.string.syncing_remote_cards), true)
            }
        }

        @AssistedFactory
        interface Factory : AssistedViewModelFactory<CardListViewModel, UiState> {
            override fun create(state: UiState): CardListViewModel
        }

        companion object :
            MavericksViewModelFactory<CardListViewModel, UiState> by hiltMavericksViewModelFactory()
    }
