package com.ih.osm.ui.pages.cilt

import androidx.lifecycle.viewModelScope
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.data.model.GetCiltsRequest
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.Sequence
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.usecase.cilt.CreateCiltEvidenceUseCase
import com.ih.osm.domain.usecase.cilt.GetCiltsUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CiltRoutineViewModel
    @Inject
    constructor(
        private val getCiltsUseCase: GetCiltsUseCase,
        private val createCiltEvidenceUseCase: CreateCiltEvidenceUseCase,
        private val authRepository: AuthRepository,
    ) : BaseViewModel<CiltRoutineViewModel.UiState>(UiState()) {
        init {
            handleGetCilts()
        }

        data class UiState(
            val ciltData: CiltData? = null,
            val isLoading: Boolean = false,
            val message: String = EMPTY,
            val isCreatingEvidence: Boolean = false,
            val createEvidenceMessage: String = EMPTY,
        )

        private fun handleGetCilts() {
            viewModelScope.launch {
                setState { copy(isLoading = true) }

                val userId = authRepository.get()?.userId?.toIntOrNull()
                val date = getCurrentDate()

                if (userId == null) {
                    setState {
                        copy(
                            isLoading = false,
                            message = "Error: No se pudo obtener el ID del usuario",
                        )
                    }
                    return@launch
                }

                val body = GetCiltsRequest(userId, date)

                kotlin.runCatching {
                    callUseCase { getCiltsUseCase(body) }
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

        private fun startSequence() {
        }

        fun getSequenceById(sequenceId: Int): Sequence? {
            return state.value.ciltData?.positions
                ?.flatMap { it.ciltMasters }
                ?.flatMap { it.sequences }
                ?.find { it.id == sequenceId }
        }

        fun createEvidence(request: CiltEvidenceRequest) {
            viewModelScope.launch {
                setState { copy(isCreatingEvidence = true, createEvidenceMessage = EMPTY) }
                kotlin.runCatching {
                    createCiltEvidenceUseCase(request)
                }.onSuccess {
                    setState {
                        copy(
                            isCreatingEvidence = false,
                            createEvidenceMessage = "Evidencia creada correctamente",
                        )
                    }
                }.onFailure {
                    LoggerHelperManager.logException(it)
                    setState {
                        copy(
                            isCreatingEvidence = false,
                            createEvidenceMessage = it.localizedMessage.orEmpty(),
                        )
                    }
                }
            }
        }

        private fun getCurrentDate(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }
    }
