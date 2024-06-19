package com.ih.m2.ui.pages.createcard

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.m2.domain.model.NodeCardItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CreateCardViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
) : MavericksViewModel<CreateCardViewModel.UiState>(initialState) {

    init {
        process(Action.GetCardTypes)
    }

    data class UiState(
        val cardTypeList: List<NodeCardItem> = emptyList(),
        val selectedCardType: String = "",
        val preclassifierList: List<NodeCardItem> = emptyList(),
        val selectedPreclassifier: String = "",
        val priorityList: List<NodeCardItem> = emptyList(),
        val selectedPriority: String = "",
    ) : MavericksState

    sealed class Action {
        data object GetCardTypes : Action()
        data class SetCardType(val id: String) : Action()
        data class GetPreclassifiers(val id: String) : Action()
        data class SetPreclassifier(val id: String) : Action()
        data object GetPriorities : Action()
        data class SetPriority(val id: String) : Action()
    }

    fun process(action: Action) {
        when (action) {
            is Action.GetCardTypes -> handleGetCardTypes()
            is Action.SetCardType -> handleSetCardType(action.id)
            is Action.GetPreclassifiers -> handleGetPreclassifiers(action.id)
            is Action.SetPreclassifier -> handleSetPreclassifier(action.id)
            is Action.GetPriorities -> handleGetPriorities()
            is Action.SetPriority -> handleSetPriority(action.id)
        }
    }

    private fun handleSetCardType(id: String) {
        viewModelScope.launch {
            setState {
                copy(
                    selectedCardType = id,
                    preclassifierList = emptyList(),
                    selectedPreclassifier = "",
                    selectedPriority = "",
                    priorityList = emptyList()
                )
            }
            handleGetPreclassifiers(id)
            val state = stateFlow.first()
            if (state.cardTypeList.find { it.id == id }?.name == "Mantenimiento") {
                handleGetPriorities()
            }
        }
    }

    private fun handleSetPreclassifier(id: String) {
        setState { copy(selectedPreclassifier = id) }
    }

    private fun handleSetPriority(id: String) {
        setState { copy(selectedPriority = id) }
    }

    private fun handleGetPreclassifiers(id: String) {
        setState {
            copy(preclassifierList = listOf(
                NodeCardItem(
                    id = "1",
                    name = "A",
                    description = "Perdida aceite, agua, aire",
                    superiorId = "1"
                ),
                NodeCardItem(
                    id = "2",
                    name = "B",
                    description = "Falta zona de trabajo",
                    superiorId = "1"
                ),
                NodeCardItem(
                    id = "3",
                    name = "C",
                    description = "Lubricacion insuficiente",
                    superiorId = "2"
                )
            ).filter { it.superiorId == id })
        }
    }

    private fun handleGetCardTypes() {
        setState {
            copy(
                cardTypeList = listOf(
                    NodeCardItem(id = "1", name = "Mantenimiento", description = "Anomalias"),
                    NodeCardItem(id = "2", name = "Comportamiento", description = "Seguridad"),
                    NodeCardItem(id = "3", name = "Agil", description = "Anomalias")
                )
            )
        }
    }

    private fun handleGetPriorities() {
        setState {
            copy(
                priorityList = listOf(
                    NodeCardItem(id = "1", name = "0d", description = "cero dias"),
                    NodeCardItem(id = "2", name = "7d", description = "7 dias"),
                    NodeCardItem(id = "3", name = "15d", description = "15 dias")
                )
            )
        }
    }

    private fun mockLevels() {
        val levels = listOf(
            NodeCardItem(
                id = "1",
                name = "Procesos A",
                description = "Area de procesos A",
                superiorId = "0"
            ),
            NodeCardItem(
                id = "2",
                name = "Procesos B",
                description = "Area de procesos B",
                superiorId = "0"
            ),
            NodeCardItem(
                id = "3",
                name = "Mixer 1",
                description = "Area de mixer 1",
                superiorId = "1"
            ),
            NodeCardItem(
                id = "4",
                name = "Mixer 2",
                description = "Area de mixer 2",
                superiorId = "1"
            ),
            NodeCardItem(
                id = "5",
                name = "Bomba 1",
                description = "Area de bomba 1",
                superiorId = "2"
            ),
            NodeCardItem(
                id = "6",
                name = "Agitador 1",
                description = "Area de agitador 1",
                superiorId = "4"
            ),
            NodeCardItem(
                id = "7",
                name = "Motor 1",
                description = "Area de motor 1",
                superiorId = "4"
            ),
        )
        // [0,1],[1,4],[2,7]
        //last selected = 7
    }


    @AssistedFactory
    interface Factory : AssistedViewModelFactory<CreateCardViewModel, UiState> {
        override fun create(state: UiState): CreateCardViewModel
    }

    companion object :
        MavericksViewModelFactory<CreateCardViewModel, UiState> by hiltMavericksViewModelFactory()
}
