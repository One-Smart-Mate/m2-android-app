package com.ih.osm.ui.pages.cilt

import androidx.lifecycle.viewModelScope
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.Sequence
import com.ih.osm.domain.usecase.cilt.GetCiltsUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CiltRoutineViewModel
    @Inject
    constructor(
        private val getCiltsUseCase: GetCiltsUseCase,
    ) : BaseViewModel<CiltRoutineViewModel.UiState>(UiState()) {
        init {
            handleGetCilts()
        }

        data class UiState(
            val ciltData: CiltData? = null,
            val isLoading: Boolean = false,
            val message: String = EMPTY,
        )

        private fun handleGetCilts() {
            viewModelScope.launch {
                setState { copy(isLoading = true) }
                kotlin.runCatching {
                    callUseCase { getCiltsUseCase() }
                }.onSuccess { data ->
                    setState {
                        copy(
                            ciltData = data,
                            isLoading = false,
                            message = EMPTY,
                        )
                    }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    setState {
                        copy(
                            ciltData = null,
                            isLoading = false,
                            message = it.localizedMessage.orEmpty(),
                        )
                    }
                }
            }
        }

        fun getSequenceById(sequenceId: Int): Sequence? {
            return state.value.ciltData?.positions
                ?.flatMap { it.ciltMasters }
                ?.flatMap { it.sequences }
                ?.find { it.id == sequenceId }
        }
    }
