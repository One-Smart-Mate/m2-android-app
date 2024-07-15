package com.ih.m2.ui.pages.createcard

import android.content.Context
import android.net.Uri
import android.util.Log
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.m2.R
import com.ih.m2.core.FileHelper
import com.ih.m2.data.repository.firebase.FirebaseAnalyticsHelper
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.CardType
import com.ih.m2.domain.model.Evidence
import com.ih.m2.domain.model.EvidenceType
import com.ih.m2.domain.model.NodeCardItem
import com.ih.m2.domain.model.hasAudios
import com.ih.m2.domain.model.hasImages
import com.ih.m2.domain.model.hasVideos
import com.ih.m2.domain.model.isBehavior
import com.ih.m2.domain.model.isBehaviorCardType
import com.ih.m2.domain.model.isMaintenanceCardType
import com.ih.m2.domain.model.toAudios
import com.ih.m2.domain.model.toImages
import com.ih.m2.domain.model.toNodeItemCard
import com.ih.m2.domain.model.toNodeItemList
import com.ih.m2.domain.model.toVideos
import com.ih.m2.domain.usecase.card.GetCardsZoneUseCase
import com.ih.m2.domain.usecase.card.SaveCardUseCase
import com.ih.m2.domain.usecase.cardtype.GetCardTypeUseCase
import com.ih.m2.domain.usecase.cardtype.GetCardTypesUseCase
import com.ih.m2.domain.usecase.level.GetLevelsUseCase
import com.ih.m2.domain.usecase.preclassifier.GetPreclassifiersUseCase
import com.ih.m2.domain.usecase.priority.GetPrioritiesUseCase
import com.ih.m2.ui.extensions.defaultIfNull
import com.ih.m2.ui.utils.CARD_ANOMALIES
import com.ih.m2.ui.utils.CARD_TYPE_METHODOLOGY_C
import com.ih.m2.ui.utils.CARD_TYPE_METHODOLOGY_M
import com.ih.m2.ui.utils.EMPTY
import com.ih.m2.ui.utils.SAFE
import com.ih.m2.ui.utils.UNSAFE
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.coroutines.CoroutineContext

class CreateCardViewModel @AssistedInject constructor(
    @Assisted initialState: UiState,
    private val coroutineContext: CoroutineContext,
    private val getCardTypesUseCase: GetCardTypesUseCase,
    private val getPrioritiesUseCase: GetPrioritiesUseCase,
    private val getPreclassifiersUseCase: GetPreclassifiersUseCase,
    private val saveCardUseCase: SaveCardUseCase,
    private val getLevelsUseCase: GetLevelsUseCase,
    private val getCardTypeUseCase: GetCardTypeUseCase,
    private val getCardsZoneUseCase: GetCardsZoneUseCase,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    @ApplicationContext private val context: Context,
    private val fileHelper: FileHelper
) : MavericksViewModel<CreateCardViewModel.UiState>(initialState) {


    data class UiState(
        val cardTypeList: List<NodeCardItem> = emptyList(),
        val selectedCardType: String = EMPTY,
        val cardType: CardType? = null,
        val preclassifierList: List<NodeCardItem> = emptyList(),
        val selectedPreclassifier: String = EMPTY,
        val priorityList: List<NodeCardItem> = emptyList(),
        val selectedPriority: String = EMPTY,
        val nodeLevelList: Map<Int, List<NodeCardItem>> = mutableMapOf(),
        val selectedLevelList: Map<Int, String> = mutableMapOf(),
        val lastSelectedLevel: String = EMPTY,
        val lastLevelCompleted: Boolean = false,
        val comment: String = EMPTY,
        val isSecureCard: Boolean = false,
        val selectedSecureOption: String = EMPTY,
        val message: String = EMPTY,
        val evidences: List<Evidence> = emptyList(),
        val cardId: String = UUID.randomUUID().toString(),
        val levelList: List<NodeCardItem> = emptyList(),
        val audioDuration: Int = 0,
        val isLoading: Boolean = false,
        val isCardSuccess: Boolean = false,
        val cardsZone: List<Card> = emptyList()
    ) : MavericksState

    sealed class Action {
        data class GetCardTypes(val filter: String) : Action()
        data class SetCardType(val id: String) : Action()
        data class GetPreclassifiers(val id: String) : Action()
        data class SetPreclassifier(val id: String) : Action()
        data object GetPriorities : Action()
        data class SetPriority(val id: String) : Action()
        data class SetLevel(val id: String, val key: Int) : Action()
        data class OnCommentChange(val comment: String) : Action()
        data class OnSecureOptionChange(val option: String) : Action()
        data object OnSaveCard : Action()
        data class OnAddEvidence(val uri: Uri, val type: EvidenceType) : Action()
        data class OnDeleteEvidence(val evidence: Evidence) : Action()
        data object GetLevels : Action()
        data class GetCardsZone(val superiorId: String) : Action()
        data object ClearMessage : Action()
    }

    fun process(action: Action) {
        when (action) {
            is Action.GetCardTypes -> handleGetCardTypes(action.filter)
            is Action.SetCardType -> handleSetCardType(action.id)
            is Action.GetPreclassifiers -> handleGetPreclassifiers(action.id)
            is Action.SetPreclassifier -> handleSetPreclassifier(action.id)
            is Action.GetPriorities -> handleGetPriorities()
            is Action.SetPriority -> handleSetPriority(action.id)
            is Action.SetLevel -> handleSetLevel(action.id, action.key)
            is Action.OnCommentChange -> handleOnCommentChange(action.comment)
            is Action.OnSecureOptionChange -> handleOnSecureOptionChange(action.option)
            is Action.OnSaveCard -> handleOnSaveCard()
            is Action.OnAddEvidence -> handleOnAddEvidence(action.uri, action.type)
            is Action.OnDeleteEvidence -> handleOnDeleteEvidence(action.evidence)
            is Action.GetLevels -> handleGetLevels()
            is Action.GetCardsZone -> handleGetCardsZone(action.superiorId)
            is Action.ClearMessage -> cleanScreenStates()
            else -> {}
        }
    }

    private fun handleOnAddEvidence(uri: Uri, type: EvidenceType) {
        viewModelScope.launch {
            val state = stateFlow.first()
            val cardType = state.cardType
            val maxImages = cardType?.quantityImagesCreate.defaultIfNull(0)
            if (state.evidences.toImages().size == maxImages) {
                setState { copy(message = context.getString(R.string.limit_images)) }
                return@launch
            }
            val maxVideos = cardType?.quantityVideosCreate.defaultIfNull(0)
            if (state.evidences.toVideos().size == maxVideos) {
                setState { copy(message = context.getString(R.string.limit_videos)) }
                return@launch
            }
            val maxAudios = cardType?.quantityAudiosCreate.defaultIfNull(0)
            if (state.evidences.toAudios().size == maxAudios) {
                setState { copy(message = context.getString(R.string.limit_audios)) }
                return@launch
            }
            val list = state.evidences.toMutableList()
            list.add(
                Evidence.fromCreateEvidence(
                    cardId = state.cardId,
                    url = uri.toString(),
                    type = type.name
                )
            )
            setState { copy(evidences = list) }
        }
    }

    private fun handleOnDeleteEvidence(evidence: Evidence) {
        viewModelScope.launch {
            val state = stateFlow.first()
            val list = state.evidences.filter { it.id != evidence.id }
            setState { copy(evidences = list) }
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
                    nodeLevelList = emptyMap(),
                    selectedLevelList = emptyMap(),
                    lastSelectedLevel = EMPTY,
                    isSecureCard = false,
                    selectedSecureOption = EMPTY,
                    evidences = emptyList(),
                    lastLevelCompleted = false,
                    comment = EMPTY
                )
            }
            process(Action.GetPreclassifiers(id))
            handleGetCardType(id)
            val state = stateFlow.first()
            val cardType = state.cardTypeList.find { it.id == id }

            if (cardType.isBehaviorCardType()) {
                setState { copy(isSecureCard = true) }
            }
            if (cardType.isMaintenanceCardType().defaultIfNull(false)) {
                handleGetPriorities()
            } else {
                val levelList = getLevelById("0", 0)
                setState { copy(nodeLevelList = levelList) }
            }
        }
    }

    private fun handleSetPreclassifier(id: String) {
        setState { copy(selectedPreclassifier = id) }
    }

    private fun handleSetPriority(id: String) {
        viewModelScope.launch {
            val levelList = getLevelById("0", 0)
            setState { copy(selectedPriority = id, nodeLevelList = levelList) }
        }
    }

    private suspend fun getLevelById(id: String, selectedKey: Int): Map<Int, List<NodeCardItem>> {
        val firstList = stateFlow.first().levelList.filter { it.superiorId == id }
        val state = stateFlow.first()
        val map = state.nodeLevelList.toMutableMap()
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
            setState { copy(nodeLevelList = list) }
        }
    }

    private suspend fun checkLastLevelSection(id: String) {
        val isEmpty = stateFlow.first().levelList.none { it.superiorId == id }
        if (isEmpty.not()) {
            setState { copy(evidences = emptyList()) }
        } else {
            process(Action.GetCardsZone(id))
        }
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
                cleanScreenStates()
            }.onFailure {
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }

    private fun handleGetCardTypes(filter: String) {
        viewModelScope.launch(coroutineContext) {
            val cardType = if (filter == CARD_ANOMALIES) {
                CARD_TYPE_METHODOLOGY_M
            } else {
                CARD_TYPE_METHODOLOGY_C
            }
            kotlin.runCatching {
                getCardTypesUseCase(filter = cardType)
            }.onSuccess {
                setState { copy(cardTypeList = it.toNodeItemList()) }
                process(Action.GetLevels)
            }.onFailure {
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }

    private fun handleGetPriorities() {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getPrioritiesUseCase()
            }.onSuccess {
                setState { copy(priorityList = it.toNodeItemCard()) }
                cleanScreenStates()
            }.onFailure {
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }

    private fun handleGetLevels() {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getLevelsUseCase()
            }.onSuccess {
                setState { copy(levelList = it.toNodeItemList()) }
                cleanScreenStates()
            }.onFailure {
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }

    private fun handleOnSaveCard() {
        setState { copy(isLoading = true, message = context.getString(R.string.saving_card)) }
        viewModelScope.launch(coroutineContext) {
            val state = stateFlow.first()
            val isBehavior = state.cardType?.isBehavior().defaultIfNull(false)
            if (isBehavior && state.selectedSecureOption.isEmpty()) {
                setState {
                    copy(
                        isLoading = false,
                        message = "Select if the card is Safe or UnSafe"
                    )
                }
                return@launch
            }

            val cardTypeValue = when (state.selectedSecureOption) {
                context.getString(R.string.safe) -> SAFE
                context.getString(R.string.unsafe) -> UNSAFE
                else -> EMPTY
            }

            val card = Card.fromCreateCard(
                cardId = state.cardId,
                areaId = state.lastSelectedLevel.toLong(),
                level = state.selectedLevelList.keys.last().toLong(),
                priorityId = state.selectedPriority,
                cardTypeValue = cardTypeValue,
                cardTypeId = state.selectedCardType,
                preclassifierId = state.selectedPreclassifier,
                comment = state.comment,
                hasImages = state.evidences.hasImages(),
                hasVideos = state.evidences.hasVideos(),
                hasAudios = state.evidences.hasAudios(),
                evidences = state.evidences
            )
            kotlin.runCatching {
                saveCardUseCase(card)
            }.onSuccess {
                Log.e("Test", "Success ${it}")
                setState { copy(isCardSuccess = true) }
                cleanScreenStates()
            }.onFailure {
                Log.e("test", "Failure $it")
                fileHelper.logException(it)
                firebaseAnalyticsHelper.logCreateCardException(it)
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }


    private fun handleGetCardType(id: String) {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getCardTypeUseCase(id)
            }.onSuccess {
                it?.let {
                    setState {
                        copy(
                            cardType = it,
                            audioDuration = it.audiosDurationCreate.defaultIfNull(60)
                        )
                    }
                    cleanScreenStates()
                }
            }.onFailure {
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }

    private fun handleGetCardsZone(superiorId: String) {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getCardsZoneUseCase(superiorId)
            }.onSuccess {
                setState { copy(cardsZone = it) }
                cleanScreenStates()
            }.onFailure {
                cleanScreenStates(it.localizedMessage.orEmpty())
            }
        }
    }

    private fun cleanScreenStates(message: String = EMPTY) {
        setState { copy(isLoading = false, message = message) }
    }


    @AssistedFactory
    interface Factory : AssistedViewModelFactory<CreateCardViewModel, UiState> {
        override fun create(state: UiState): CreateCardViewModel
    }

    companion object :
        MavericksViewModelFactory<CreateCardViewModel, UiState> by hiltMavericksViewModelFactory()
}
