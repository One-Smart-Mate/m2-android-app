package com.ih.m2.ui.pages.createcard

import android.util.Log
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.m2.domain.model.NodeCardItem
import com.ih.m2.domain.model.isMaintenanceCardType
import com.ih.m2.domain.model.toNodeItemCard
import com.ih.m2.domain.model.toNodeItemList
import com.ih.m2.domain.usecase.cardtype.GetCardTypesUseCase
import com.ih.m2.domain.usecase.preclassifier.GetPreclassifiersUseCase
import com.ih.m2.domain.usecase.priority.GetPrioritiesUseCase
import com.ih.m2.ui.extensions.defaultIfNull
import com.ih.m2.ui.utils.EMPTY
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CreateCardViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
    private val getCardTypesUseCase: GetCardTypesUseCase,
    private val getPrioritiesUseCase: GetPrioritiesUseCase,
    private val getPreclassifiersUseCase: GetPreclassifiersUseCase
) : MavericksViewModel<CreateCardViewModel.UiState>(initialState) {

    init {
        process(Action.GetCardTypes)
    }

    data class UiState(
        val cardTypeList: List<NodeCardItem> = emptyList(),
        val selectedCardType: String = EMPTY,
        val preclassifierList: List<NodeCardItem> = emptyList(),
        val selectedPreclassifier: String = EMPTY,
        val priorityList: List<NodeCardItem> = emptyList(),
        val selectedPriority: String = "",
        val levelList: Map<Int, List<NodeCardItem>> = mutableMapOf(),
        val selectedLevelList: Map<Int, String> = mutableMapOf(),
        val lastSelectedLevel: String = EMPTY,
        val lastLevelCompleted: Boolean = false,
        val comment: String = EMPTY,
        val isSecureCard: Boolean = false,
        val selectedSecureOption: String = EMPTY,
        val message: String = EMPTY
    ) : MavericksState

    sealed class Action {
        data object GetCardTypes : Action()
        data class SetCardType(val id: String) : Action()
        data class GetPreclassifiers(val id: String) : Action()
        data class SetPreclassifier(val id: String) : Action()
        data object GetPriorities : Action()
        data class SetPriority(val id: String) : Action()
        data class SetLevel(val id: String, val key: Int) : Action()
        data class OnCommentChange(val comment: String) : Action()
        data class OnSecureOptionChange(val option: String) : Action()
        data object SaveCard: Action()
    }

    fun process(action: Action) {
        when (action) {
            is Action.GetCardTypes -> handleGetCardTypes()
            is Action.SetCardType -> handleSetCardType(action.id)
            is Action.GetPreclassifiers -> handleGetPreclassifiers(action.id)
            is Action.SetPreclassifier -> handleSetPreclassifier(action.id)
            is Action.GetPriorities -> handleGetPriorities()
            is Action.SetPriority -> handleSetPriority(action.id)
            is Action.SetLevel -> handleSetLevel(action.id, action.key)
            is Action.OnCommentChange -> handleOnCommentChange(action.comment)
            is Action.OnSecureOptionChange -> handleOnSecureOptionChange(action.option)
            is Action.SaveCard -> handleSaveCard()
        }
    }

    private fun handleOnCommentChange(comment: String) {
        setState { copy(comment = comment) }
    }

    private fun handleOnSecureOptionChange(option: String) {
        setState { copy(selectedSecureOption = option) }
    }

    private fun handleSetCardType(id: String) {
        viewModelScope.launch {
            setState {
                copy(
                    selectedCardType = id,
                    preclassifierList = emptyList(),
                    selectedPreclassifier = EMPTY,
                    selectedPriority = EMPTY,
                    priorityList = emptyList(),
                    levelList = emptyMap(),
                    selectedLevelList = emptyMap(),
                    lastSelectedLevel = EMPTY,
                    isSecureCard = false,
                    selectedSecureOption = EMPTY
                )
            }
            process(Action.GetPreclassifiers(id))
            val state = stateFlow.first()
            val cardType = state.cardTypeList.find { it.id == id }?.isMaintenanceCardType()
            if (cardType.defaultIfNull(false)) {
                handleGetPriorities()
            } else {
                val levelList = getLevelById("0", 0)
                setState { copy(levelList = levelList, isSecureCard = true) }
            }
        }
    }

    private fun handleSetPreclassifier(id: String) {
        setState { copy(selectedPreclassifier = id) }
    }

    private fun handleSetPriority(id: String) {
        viewModelScope.launch {
            val levelList = getLevelById("0", 0)
            setState { copy(selectedPriority = id, levelList = levelList) }
        }
    }

    private suspend fun getLevelById(id: String, selectedKey: Int): Map<Int, List<NodeCardItem>> {
        val firstList = mockLevels().filter { it.superiorId == id }
        val state = stateFlow.first()
        val map = state.levelList.toMutableMap()
        val selectedMap = state.selectedLevelList.toMutableMap()
        for (index in selectedKey until map.keys.size) {
            map[index] = emptyList()
            selectedMap[index] = EMPTY
        }
        map[selectedKey] = firstList
        selectedMap[selectedKey.minus(1)] = id
        setState { copy(selectedLevelList = selectedMap, lastSelectedLevel = id) }
        Log.e("Map", "Map List Key -> $selectedKey -- $id")
        return map
    }


    private fun handleSetLevel(id: String, key: Int) {
        viewModelScope.launch {
            val newKey = key.plus(1)
            val list = getLevelById(id, newKey)
            checkLastLevelSection(id)
            setState { copy(levelList = list) }
        }
    }

    private fun checkLastLevelSection(id: String) {
        val isEmpty = mockLevels().none { it.superiorId == id }
        setState { copy(lastLevelCompleted = isEmpty) }
    }

    private fun handleGetPreclassifiers(id: String) {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getPreclassifiersUseCase()
            }.onSuccess {
                setState {
                    copy(preclassifierList = it.filter { it.cardTypeId == id }.toNodeItemCard())
                }
            }.onFailure {
                setState { copy(message = it.localizedMessage.orEmpty()) }
            }
        }
    }

    private fun handleGetCardTypes() {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getCardTypesUseCase()
            }.onSuccess {
                setState { copy(cardTypeList = it.toNodeItemList()) }
            }.onFailure {
                setState { copy(message = it.localizedMessage.orEmpty()) }
            }
        }
    }

    private fun handleGetPriorities() {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getPrioritiesUseCase()
            }.onSuccess {
                setState { copy(priorityList = it.toNodeItemCard()) }
            }.onFailure {
                setState { copy(message = it.localizedMessage.orEmpty()) }
            }
        }
    }

    private fun mockLevels(): List<NodeCardItem> {
        return listOf(
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
            NodeCardItem(
                id = "8",
                name = "Maq 1",
                description = "Area de maq 8",
                superiorId = "6"
            ),
        )
    }


    private fun handleSaveCard() {
        viewModelScope.launch(coroutineContext) {

        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<CreateCardViewModel, UiState> {
        override fun create(state: UiState): CreateCardViewModel
    }

    companion object :
        MavericksViewModelFactory<CreateCardViewModel, UiState> by hiltMavericksViewModelFactory()
}
