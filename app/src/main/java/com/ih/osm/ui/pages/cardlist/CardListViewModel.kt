package com.ih.osm.ui.pages.cardlist

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ih.osm.MainActivity
import com.ih.osm.R
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.NetworkStatus
import com.ih.osm.domain.model.Result
import com.ih.osm.domain.model.Session
import com.ih.osm.domain.model.User
import com.ih.osm.domain.model.filterByStatus
import com.ih.osm.domain.model.toCardFilter
import com.ih.osm.domain.usecase.card.GetAllPagedCardsUseCase
import com.ih.osm.domain.usecase.card.GetCardsUseCase
import com.ih.osm.domain.usecase.catalogs.SyncCatalogsUseCase
import com.ih.osm.domain.usecase.session.GetSessionUseCase
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
        private val getAllPagedCardsUseCase: GetAllPagedCardsUseCase,
        private val getCardsUseCase: GetCardsUseCase,
        private val getSessionUseCase: GetSessionUseCase,
        private val syncCatalogsUseCase: SyncCatalogsUseCase,
        private val sharedPreferences: SharedPreferences,
        @ApplicationContext private val context: Context,
    ) : BaseViewModel<CardListViewModel.UiState>(UiState()) {
        companion object {
            private const val TAG = "CardListViewModel"
            private const val PAGE_SIZE = 20
        }

        init {
            load()
        }

        data class UiState(
            val cards: List<Card> = emptyList(),
            val isLoading: Boolean = true,
            val isLoadingMore: Boolean = false,
            val currentPage: Int = 1,
            val hasMorePages: Boolean = true,
            val message: String = EMPTY,
            val user: User? = null,
            val session: Session? = null,
        )

        private var currentFilter: String = EMPTY

        fun load() {
            Log.d(TAG, "load() called")
            handleGetCards()
            handleGetSession()
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
                        copy(
                            isLoading = false,
                            message = context.getString(R.string.please_connect_to_internet),
                        )
                    }
                    return@launch
                }

                val networkStatus = NetworkConnection.networkStatus(context)
                if (networkStatus == NetworkStatus.DATA_CONNECTED &&
                    sharedPreferences.getNetworkPreference().isEmpty()
                ) {
                    setState {
                        copy(
                            isLoading = false,
                            message = context.getString(R.string.network_preferences_allowed),
                        )
                    }
                    return@launch
                }

                context.getActivity<MainActivity>()?.enqueueSyncCardsWork()

                kotlinx.coroutines.delay(3000)

                kotlin
                    .runCatching {
                        callUseCase { syncCatalogsUseCase(syncCards = true) }
                    }.onFailure {
                        LoggerHelperManager.logException(it)
                    }

                kotlin
                    .runCatching {
                        callUseCase { getCardsUseCase(syncRemote = true, localCards = false) }
                    }.onSuccess { cards ->
                        Log.d(TAG, "Sync complete: ${cards.size} cards synced to local DB")
                        // After syncing, reload page 1 with pagination
                        loadPage(page = 1, replace = true, syncRemote = false)
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

        private fun handleGetCards() {
            Log.d(TAG, "handleGetCards() - Loading initial page")
            viewModelScope.launch {
                // Reset state and load first page
                setState { copy(currentPage = 1, hasMorePages = true) }
                loadPage(page = 1, replace = true, syncRemote = false)
            }
        }

        private fun loadPage(
            page: Int,
            replace: Boolean = false,
            syncRemote: Boolean = false,
        ) {
            Log.d(TAG, "loadPage() - page=$page, replace=$replace, syncRemote=$syncRemote")

            viewModelScope.launch {
                kotlin
                    .runCatching {
                        callUseCase {
                            getAllPagedCardsUseCase(
                                page = page,
                                limit = PAGE_SIZE,
                                syncRemote = syncRemote,
                            )
                        }
                    }.onSuccess { result ->
                        when (result) {
                            is Result.Success -> {
                                val paginatedCards = result.data
                                Log.d(
                                    TAG,
                                    "SUCCESS - Loaded ${paginatedCards.cards.size} cards for page $page. " +
                                        "hasNextPage=${paginatedCards.hasNextPage}",
                                )

                                // Apply filter if active
                                val filteredCards =
                                    if (currentFilter.isNotEmpty()) {
                                        paginatedCards.cards.filterByStatus(
                                            filter = currentFilter,
                                            userId = getState().session?.userId.orEmpty(),
                                        )
                                    } else {
                                        paginatedCards.cards
                                    }.sortedByDescending { it.siteCardId }

                                setState {
                                    copy(
                                        cards = if (replace) filteredCards else cards + filteredCards,
                                        currentPage = page,
                                        hasMorePages = paginatedCards.hasNextPage,
                                        isLoading = false,
                                        isLoadingMore = false,
                                        message = EMPTY,
                                    )
                                }
                            }

                            is Result.Error -> {
                                Log.e(TAG, "ERROR - ${result.message}")
                                setState {
                                    copy(
                                        isLoading = false,
                                        isLoadingMore = false,
                                        message = result.message,
                                    )
                                }
                            }

                            is Result.Loading -> {
                                // Loading state already handled by isLoading/isLoadingMore flags
                                Log.d(TAG, "Loading state")
                            }
                        }
                    }.onFailure { exception ->
                        LoggerHelperManager.logException(exception)
                        Log.e(TAG, "EXCEPTION - ${exception.localizedMessage}", exception)
                        setState {
                            copy(
                                isLoading = false,
                                isLoadingMore = false,
                                message = exception.localizedMessage.orEmpty(),
                            )
                        }
                    }
            }
        }

        fun loadMore() {
            val state = getState()
            Log.d(
                TAG,
                "loadMore() - currentPage=${state.currentPage}, hasMorePages=${state.hasMorePages}, isLoadingMore=${state.isLoadingMore}",
            )

            if (state.isLoadingMore || !state.hasMorePages) {
                Log.d(TAG, "loadMore() - SKIPPED (already loading or no more pages)")
                return
            }

            val nextPage = state.currentPage + 1
            Log.d(TAG, "loadMore() - Loading page $nextPage")
            setState { copy(isLoadingMore = true) }
            loadPage(page = nextPage, replace = false, syncRemote = false)
        }

        private fun handleGetSession() {
            viewModelScope.launch {
                kotlin
                    .runCatching {
                        callUseCase { getSessionUseCase() }
                    }.onSuccess { session ->
                        setState { copy(session = session) }
                    }.onFailure {
                        LoggerHelperManager.logException(it)
                        cleanScreenStates(it.localizedMessage.orEmpty())
                    }
            }
        }

        fun handleFilterCards(filter: String) {
            viewModelScope.launch {
                currentFilter = filter.toCardFilter(context = context)
                Log.d(TAG, "handleFilterCards() - filter='$currentFilter'")

                // Reset pagination state and reload from page 1
                setState { copy(currentPage = 1, hasMorePages = true, isLoading = true) }
                loadPage(page = 1, replace = true, syncRemote = false)
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
