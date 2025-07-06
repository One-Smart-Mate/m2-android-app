package com.ih.osm.ui.pages.cardlist

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ih.osm.MainActivity
import com.ih.osm.R
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.NetworkStatus
import com.ih.osm.domain.model.User
import com.ih.osm.domain.model.filterByStatus
import com.ih.osm.domain.model.toCardFilter
import com.ih.osm.domain.usecase.card.GetCardsUseCase
import com.ih.osm.domain.usecase.catalogs.SyncCatalogsUseCase
import com.ih.osm.domain.usecase.user.GetUserUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.extensions.getActivity
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CardListViewModel
    @Inject
    constructor(
        private val getCardsUseCase: GetCardsUseCase,
        private val getUserUseCase: GetUserUseCase,
        private val syncCatalogsUseCase: SyncCatalogsUseCase,
        private val sharedPreferences: SharedPreferences,
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
                setState { copy(isLoading = true, message = context.getString(R.string.loading_data)) }

                val (isExpired, errorMessage) = isSubscriptionExpired()
                if (isExpired) {
                    setState {
                        copy(isLoading = false, message = errorMessage.orEmpty())
                    }
                    return@launch
                }

                if (!NetworkConnection.isConnected()) {
                    setState {
                        copy(isLoading = false, message = context.getString(R.string.please_connect_to_internet))
                    }
                    return@launch
                }

                val networkStatus = NetworkConnection.networkStatus(context)
                if (networkStatus == NetworkStatus.DATA_CONNECTED &&
                    sharedPreferences.getNetworkPreference().isEmpty()
                ) {
                    setState {
                        copy(isLoading = false, message = context.getString(R.string.network_preferences_allowed))
                    }
                    return@launch
                }

                context.getActivity<MainActivity>()?.enqueueSyncCardsWork()

                kotlinx.coroutines.delay(3000)

                kotlin.runCatching {
                    callUseCase { syncCatalogsUseCase(syncCards = true) }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                }

                kotlin.runCatching {
                    callUseCase { getCardsUseCase(syncRemote = true, localCards = false) }
                }.onSuccess { cards ->
                    setState {
                        copy(
                            cards = cards.sortedByDescending { it.siteCardId },
                            isLoading = false,
                            message = EMPTY,
                        )
                    }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    setState {
                        copy(isLoading = false, message = it.localizedMessage.orEmpty())
                    }
                }
            }
        }

        private fun isSubscriptionExpired(): Pair<Boolean, String?> {
            val dueDateString = sharedPreferences.getDueDate()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val dueDate =
                try {
                    sdf.parse(dueDateString)
                } catch (e: Exception) {
                    null
                }

            val today = Date()

            return if (dueDate != null && dueDate.before(today)) {
                true to context.getString(R.string.cards_cannot_be_uploaded)
            } else {
                false to null
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
