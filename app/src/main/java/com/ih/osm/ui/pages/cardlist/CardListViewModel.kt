package com.ih.osm.ui.pages.cardlist

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.User
import com.ih.osm.domain.model.filterByStatus
import com.ih.osm.domain.model.toCardFilter
import com.ih.osm.domain.usecase.card.GetCardsUseCase
import com.ih.osm.domain.usecase.user.GetUserUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardListViewModel
    @Inject
    constructor(
        private val getCardsUseCase: GetCardsUseCase,
        private val getUserUseCase: GetUserUseCase,
        @ApplicationContext private val context: Context,
    ) : BaseViewModel<CardListViewModel.UiState>(UiState()) {
        data class UiState(
            val cards: List<Card> = emptyList(),
            val isLoading: Boolean = true,
            val message: String = EMPTY,
            val user: User? = null,
        )

        fun load() {
            handleGeCards()
            handleGetUser()
        }

        fun handleUpdateRemoteCardsAndSave() {
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { getCardsUseCase(syncRemote = true, localCards = false) }
                }.onSuccess {
                    setState {
                        copy(
                            cards = it.sortedByDescending { item -> item.siteCardId },
                            isLoading = false,
                            message = EMPTY,
                        )
                    }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    cleanScreenStates(it.localizedMessage.orEmpty())
                }
            }
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
                            message = EMPTY,
                        )
                    }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    cleanScreenStates(it.localizedMessage.orEmpty())
                }
            }
        }

        private fun handleGetUser() {
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { getUserUseCase() }
                }.onSuccess {
                    setState { copy(user = it) }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    cleanScreenStates(it.localizedMessage.orEmpty())
                }
            }
        }

        fun handleFilterCards(filter: String) {
            viewModelScope.launch {
                val cards = getCardsUseCase(syncRemote = false)
                val filteredCards =
                    cards.filterByStatus(
                        filter = filter.toCardFilter(context = context),
                        userId = getState().user?.userId.orEmpty(),
                    ).sortedByDescending { it.siteCardId }
                setState { copy(cards = filteredCards) }
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
