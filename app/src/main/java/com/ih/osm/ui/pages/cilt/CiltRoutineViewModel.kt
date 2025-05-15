package com.ih.osm.ui.pages.cilt

import androidx.lifecycle.viewModelScope
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.data.model.UserCiltData
import com.ih.osm.domain.usecase.cilt.GetUserCiltDataUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.pages.cardlist.CardListViewModel.UiState
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CiltRoutineViewModel
    @Inject
    constructor(
        private val getUserCiltDataUseCase: GetUserCiltDataUseCase,
    ) : BaseViewModel<CiltRoutineViewModel.UiState>(UiState()) {
        data class UiState(
            val userCiltData: UserCiltData? = null,
            val isLoading: Boolean = true,
            val message: String = EMPTY,
        )

        fun loadUserCiltData(userId: String) {
            viewModelScope.launch {
                setState { copy(isLoading = true) }

                kotlin.runCatching {
                    callUseCase { getUserCiltDataUseCase(userId) }
                }.onSuccess { data ->
                    setState {
                        copy(
                            userCiltData = data,
                            isLoading = false,
                            message = EMPTY,
                        )
                    }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    setState {
                        copy(
                            userCiltData = null,
                            isLoading = false,
                            message = it.localizedMessage.orEmpty(),
                        )
                    }
                }
            }
        }
    }
