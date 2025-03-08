package com.ih.osm.ui.pages.createcard

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ih.osm.R
import com.ih.osm.core.file.FileHelper
import com.ih.osm.core.firebase.FirebaseNotificationType
import com.ih.osm.data.repository.firebase.FirebaseAnalyticsHelper
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.CardType
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.EvidenceType
import com.ih.osm.domain.model.NodeCardItem
import com.ih.osm.domain.model.hasAudios
import com.ih.osm.domain.model.hasImages
import com.ih.osm.domain.model.hasVideos
import com.ih.osm.domain.model.toAudios
import com.ih.osm.domain.model.toImages
import com.ih.osm.domain.model.toNodeItemCard
import com.ih.osm.domain.model.toNodeItemList
import com.ih.osm.domain.model.toVideos
import com.ih.osm.domain.usecase.card.GetCardsZoneUseCase
import com.ih.osm.domain.usecase.card.SaveCardUseCase
import com.ih.osm.domain.usecase.cardtype.GetCardTypeUseCase
import com.ih.osm.domain.usecase.cardtype.GetCardTypesUseCase
import com.ih.osm.domain.usecase.level.GetLevelsUseCase
import com.ih.osm.domain.usecase.notifications.GetFirebaseNotificationUseCase
import com.ih.osm.domain.usecase.preclassifier.GetPreclassifiersUseCase
import com.ih.osm.domain.usecase.priority.GetPrioritiesUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.extensions.defaultIfNull
import com.ih.osm.ui.pages.createcard.action.CreateCardAction
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateCardViewModel
    @Inject
    constructor(
        private val getCardTypesUseCase: GetCardTypesUseCase,
        private val getPrioritiesUseCase: GetPrioritiesUseCase,
        private val getPreclassifiersUseCase: GetPreclassifiersUseCase,
        private val saveCardUseCase: SaveCardUseCase,
        private val getLevelsUseCase: GetLevelsUseCase,
        private val getCardTypeUseCase: GetCardTypeUseCase,
        private val getCardsZoneUseCase: GetCardsZoneUseCase,
        private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
        private val getFirebaseNotificationUseCase: GetFirebaseNotificationUseCase,
        private val fileHelper: FileHelper,
        @ApplicationContext private val context: Context,
    ) : BaseViewModel<CreateCardViewModel.UiState>(UiState()) {
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
            val message: String = EMPTY,
            val evidences: List<Evidence> = emptyList(),
            val uuid: String = UUID.randomUUID().toString(),
            val levelList: List<NodeCardItem> = emptyList(),
            val audioDuration: Int = 0,
            val isLoading: Boolean = false,
            val isCardSuccess: Boolean = false,
            val cardsZone: List<Card> = emptyList(),
        )

        init {
            handleGetCardTypes()
        }

        fun process(action: CreateCardAction) {
            when (action) {
                is CreateCardAction.SetCardType -> handleSetCardType(action.id)
                is CreateCardAction.SetPreclassifier -> handleSetPreclassifier(action.id)
                is CreateCardAction.SetPriority -> handleSetPriority(action.id)
                is CreateCardAction.SetLevel -> handleSetLevel(action.id, action.key)
                is CreateCardAction.SetComment -> setState { copy(comment = action.comment) }
                is CreateCardAction.DeleteEvidence -> handleDeleteEvidence(action.evidence)
                is CreateCardAction.AddEvidence -> handleAddEvidence(action.uri, action.type)
                is CreateCardAction.Save -> handleSaveCard()
            }
        }

        private fun handleAddEvidence(
            uri: Uri,
            type: EvidenceType,
        ) {
            viewModelScope.launch {
                val state = getState()
                val cardType = state.cardType
                val errorMessage =
                    when (type) {
                        EvidenceType.IMCR -> {
                            val maxImages = cardType?.quantityImagesCreate.defaultIfNull(0)
                            if ((state.evidences.toImages().size) == maxImages) {
                                context.getString(R.string.limit_images)
                            } else {
                                EMPTY
                            }
                        }

                        EvidenceType.VICR -> {
                            val maxVideos = cardType?.quantityVideosCreate.defaultIfNull(0)
                            if (state.evidences.toVideos().size == maxVideos) {
                                context.getString(R.string.limit_videos)
                            } else {
                                EMPTY
                            }
                        }

                        EvidenceType.AUCR -> {
                            val maxAudios = cardType?.quantityAudiosCreate.defaultIfNull(0)
                            if (state.evidences.toAudios().size == maxAudios) {
                                context.getString(R.string.limit_audios)
                            } else {
                                EMPTY
                            }
                        }

                        else -> EMPTY
                    }
                if (errorMessage.isNotEmpty()) {
                    setState { copy(message = errorMessage, isLoading = false) }
                    return@launch
                }

                val list = state.evidences.toMutableList()
                list.add(
                    Evidence.fromCreateEvidence(
                        cardId = state.uuid,
                        url = uri.toString(),
                        type = type.name,
                    ),
                )
                setState { copy(evidences = list) }
            }
        }

        private fun handleDeleteEvidence(evidence: Evidence) {
            viewModelScope.launch {
                val state = getState()
                val list = state.evidences.filter { it.id != evidence.id }
                setState { copy(evidences = list) }
            }
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
                        evidences = emptyList(),
                        lastLevelCompleted = false,
                        comment = EMPTY,
                    )
                }
                handleGetPreclassifiers(id)
                handleGetCardType(id)
            }
        }

        private fun handleSetPreclassifier(id: String) {
            viewModelScope.launch {
                setState { copy(selectedPreclassifier = id, selectedPriority = EMPTY, priorityList = emptyList()) }
                handleGetPriorities()
            }
        }

        private fun handleSetPriority(id: String) {
            viewModelScope.launch {
                val levelList = getLevelById("0", 0)
                setState { copy(selectedPriority = id, nodeLevelList = levelList) }
            }
        }

        private suspend fun getLevelById(
            id: String,
            selectedKey: Int,
        ): Map<Int, List<NodeCardItem>> {
            val state = getState()
            val firstList = state.levelList.filter { it.superiorId == id }
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

        private fun handleSetLevel(
            id: String,
            key: Int,
        ) {
            viewModelScope.launch {
                val newKey = key.plus(1)
                val list = getLevelById(id, newKey)
                checkLastLevelSection(id)
                setState { copy(nodeLevelList = list) }
            }
        }

        private suspend fun checkLastLevelSection(id: String) {
            val isEmpty = getState().levelList.none { it.superiorId == id }
            if (isEmpty.not()) {
                setState { copy(evidences = emptyList()) }
            } else {
                handleGetCardsZone()
            }
            setState { copy(lastLevelCompleted = isEmpty) }
        }

        private fun handleGetPreclassifiers(id: String) {
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { getPreclassifiersUseCase() }
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

        private fun handleGetCardTypes() {
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { getCardTypesUseCase() }
                }.onSuccess {
                    setState { copy(cardTypeList = it.toNodeItemList()) }
                    handleGetLevels()
                }.onFailure {
                    cleanScreenStates(it.localizedMessage.orEmpty())
                }
            }
        }

        private fun handleGetPriorities() {
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { getPrioritiesUseCase() }
                }.onSuccess {
                    setState { copy(priorityList = it.toNodeItemCard()) }
                    cleanScreenStates()
                }.onFailure {
                    cleanScreenStates(it.localizedMessage.orEmpty())
                }
            }
        }

        private fun handleGetLevels() {
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { getLevelsUseCase() }
                }.onSuccess {
                    setState { copy(levelList = it.toNodeItemList()) }
                    cleanScreenStates()
                    checkCatalogs()
                }.onFailure {
                    cleanScreenStates(it.localizedMessage.orEmpty())
                }
            }
        }

        private fun handleSaveCard() {
            setState { copy(isLoading = true, message = context.getString(R.string.saving_card)) }
            viewModelScope.launch {
                val state = getState()
                val card =
                    Card.fromCreateCard(
                        areaId = state.lastSelectedLevel.toLong(),
                        level = state.selectedLevelList.keys.last().toLong(),
                        priorityId = state.selectedPriority,
                        cardTypeValue = EMPTY,
                        cardTypeId = state.selectedCardType,
                        preclassifierId = state.selectedPreclassifier,
                        comment = state.comment,
                        hasImages = state.evidences.hasImages(),
                        hasVideos = state.evidences.hasVideos(),
                        hasAudios = state.evidences.hasAudios(),
                        evidences = state.evidences,
                        uuid = state.uuid,
                    )
                kotlin.runCatching {
                    callUseCase { saveCardUseCase(card) }
                }.onSuccess {
                    Log.e("Test", "Success $it")
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
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { getCardTypeUseCase(id) }
                }.onSuccess {
                    it?.let {
                        setState {
                            copy(
                                cardType = it,
                                audioDuration = it.audiosDurationCreate.defaultIfNull(120),
                            )
                        }
                        cleanScreenStates()
                    }
                }.onFailure {
                    cleanScreenStates(it.localizedMessage.orEmpty())
                }
            }
        }

        private fun handleGetCardsZone() {
            viewModelScope.launch {
                val id = getState().lastSelectedLevel
                kotlin.runCatching {
                    callUseCase { getCardsZoneUseCase(id) }
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

        fun cleanMessage() {
            setState { copy(message = EMPTY) }
        }

        private fun checkCatalogs() {
            viewModelScope.launch {
                kotlin.runCatching {
                    delay(2000L)
                    when (callUseCase { getFirebaseNotificationUseCase() }) {
                        FirebaseNotificationType.SYNC_REMOTE_CATALOGS -> {
                            cleanScreenStates(
                                context.getString(R.string.update_catalogs_action_message),
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    }
