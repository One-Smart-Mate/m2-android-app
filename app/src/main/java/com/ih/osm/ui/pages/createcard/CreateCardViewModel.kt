package com.ih.osm.ui.pages.createcard

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.ih.osm.R
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.core.file.FileHelper
import com.ih.osm.core.preferences.SharedPreferences
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
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

private const val TAG = "CreateCardViewModel"

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
        private val sharedPreferences: SharedPreferences,
        @ApplicationContext private val context: Context,
    ) : BaseViewModel<CreateCardViewModel.UiState>(UiState()) {
        private var isCiltMode = false
        private var superiorIdCilt: String? = null

        data class UiState(
            val cardTypeList: List<NodeCardItem> = emptyList(),
            val selectedCardType: String = "",
            val cardType: CardType? = null,
            val preclassifierList: List<NodeCardItem> = emptyList(),
            val selectedPreclassifier: String = "",
            val priorityList: List<NodeCardItem> = emptyList(),
            val selectedPriority: String = "",
            val levelsByParent: Map<String, List<NodeCardItem>> = emptyMap(), // parentId -> children
            val filteredNodeLevelList: Map<Int, List<NodeCardItem>> = mutableMapOf(), // for UI after search
            val selectedLevelList: Map<Int, String> = mutableMapOf(),
            val lastSelectedLevel: String = "",
            val lastLevelCompleted: Boolean = false,
            val comment: String = "",
            val message: String = "",
            val evidences: List<Evidence> = emptyList(),
            val uuid: String = UUID.randomUUID().toString(),
            val isLoading: Boolean = false,
            val isCardSuccess: Boolean = false,
            val cardsZone: List<Card> = emptyList(),
            val levelsLoaded: Boolean = false,
            val searchText: String = "",
            // Loading states for sections
            val isLoadingCardTypes: Boolean = false,
            val isLoadingPreclassifiers: Boolean = false,
            val isLoadingPriorities: Boolean = false,
            val isLoadingLevels: Boolean = false,
            val audioDuration: Int = 0,
        )

        init {
            handleGetCardTypes()
            handleGetLevels()
        }

        // =======================
        // Actions
        // =======================
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

        fun setCiltMode(enabled: Boolean) {
            isCiltMode = enabled
            if (!enabled) superiorIdCilt = null
        }

        fun setSuperiorIdCilt(id: String?) {
            if (isCiltMode) superiorIdCilt = id
        }

        fun updateSearchText(text: String) {
            setState { copy(searchText = text) }
            filterLevels()
        }

        // =======================
        // CardType
        // =======================
        private fun handleSetCardType(id: String) {
            viewModelScope.launch {
                setState {
                    copy(
                        selectedCardType = id,
                        preclassifierList = emptyList(),
                        selectedPreclassifier = "",
                        selectedPriority = "",
                        priorityList = emptyList(),
                        filteredNodeLevelList = emptyMap(),
                        selectedLevelList = emptyMap(),
                        lastSelectedLevel = "",
                        evidences = emptyList(),
                        lastLevelCompleted = false,
                        comment = "",
                    )
                }
                handleGetPreclassifiers(id)
                handleGetCardType(id)
            }
        }

        private fun handleGetCardTypes() {
            setState { copy(isLoadingCardTypes = true) }
            viewModelScope.launch {
                runCatching { callUseCase { getCardTypesUseCase() } }
                    .onSuccess { list ->
                        setState { copy(cardTypeList = list.toNodeItemList(), isLoadingCardTypes = false) }
                        handleGetLevels()
                    }.onFailure {
                        setState { copy(isLoadingCardTypes = false, message = it.localizedMessage ?: "") }
                    }
            }
        }

        private fun handleGetCardType(id: String) {
            viewModelScope.launch {
                runCatching { callUseCase { getCardTypeUseCase(id) } }
                    .onSuccess { cardType ->
                        cardType?.let { setState { copy(cardType = it, audioDuration = it.audiosDurationCreate.defaultIfNull(120)) } }
                    }.onFailure { LoggerHelperManager.logException(it) }
            }
        }

        // =======================
        // Preclassifier
        // =======================
        private fun handleSetPreclassifier(id: String) {
            viewModelScope.launch {
                setState { copy(selectedPreclassifier = id, selectedPriority = "", priorityList = emptyList()) }
                handleGetPriorities()
            }
        }

        private fun handleGetPreclassifiers(cardTypeId: String) {
            setState { copy(isLoadingPreclassifiers = true) }
            viewModelScope.launch {
                runCatching { callUseCase { getPreclassifiersUseCase() } }
                    .onSuccess { all ->
                        val filtered = all.filter { it.cardTypeId == cardTypeId }
                        setState { copy(preclassifierList = filtered.toNodeItemCard(), isLoadingPreclassifiers = false) }
                    }.onFailure { setState { copy(isLoadingPreclassifiers = false, message = it.localizedMessage ?: "") } }
            }
        }

        // =======================
        // Priority
        // =======================
        private fun handleSetPriority(id: String) {
            viewModelScope.launch {
                val rootLevels = getState().levelsByParent["0"].orEmpty()
                setState {
                    copy(
                        selectedPriority = id,
                        filteredNodeLevelList = mapOf(0 to rootLevels),
                        selectedLevelList = emptyMap(),
                    )
                }
            }
        }

        private fun handleGetPriorities() {
            setState { copy(isLoadingPriorities = true) }
            viewModelScope.launch {
                runCatching { callUseCase { getPrioritiesUseCase() } }
                    .onSuccess { list -> setState { copy(priorityList = list.toNodeItemCard(), isLoadingPriorities = false) } }
                    .onFailure { setState { copy(isLoadingPriorities = false, message = it.localizedMessage ?: "") } }
            }
        }

        // =======================
        // Levels
        // =======================
        private fun handleGetLevels() {
            setState { copy(isLoadingLevels = true) }
            viewModelScope.launch {
                runCatching { callUseCase { getLevelsUseCase() } }
                    .onSuccess { levels ->
                        val byParent = levels.groupBy { it.superiorId }.mapValues { it.value.toNodeItemList() }
                        setState { copy(levelsByParent = byParent) }
                        filterLevels()
                        setState { copy(levelsLoaded = true, isLoadingLevels = false) }
                    }.onFailure { setState { copy(isLoadingLevels = false, message = it.localizedMessage ?: "") } }
            }
        }

        // Build filtered levels with full path and proper root-to-child order
        private fun filterLevels() {
            val fullLevelsByParent = getState().levelsByParent
            val search = getState().searchText
            val filtered = mutableMapOf<Int, List<NodeCardItem>>()

            if (search.isBlank()) {
                filtered[0] = fullLevelsByParent["0"].orEmpty()
            } else {
                val allNodes = fullLevelsByParent.values.flatten().associateBy { it.id }
                val matchedNodes = allNodes.values.filter { it.name.contains(search, ignoreCase = true) }

                matchedNodes.forEach { node ->
                    val path = mutableListOf<NodeCardItem>()
                    var current: NodeCardItem? = node
                    while (current != null) {
                        path.add(current)
                        current = current.superiorId?.let { allNodes[it] }
                    }
                    path.reverse() // root -> leaf

                    path.forEachIndexed { levelIndex, item ->
                        val list = filtered.getOrDefault(levelIndex, emptyList())
                        if (list.none { it.id == item.id }) {
                            filtered[levelIndex] = list + item
                        }
                    }
                }
            }

            // Sort filtered map by keys to ensure root-first order
            val sortedFiltered = filtered.toSortedMap()
            setState { copy(filteredNodeLevelList = sortedFiltered) }
        }

        // When selecting a level (either manually or from search), select full path
        private fun handleSetLevel(
            id: String,
            key: Int,
        ) {
            val fullLevelsByParent = getState().levelsByParent
            val allNodes = fullLevelsByParent.values.flatten().associateBy { it.id }

            // Build full path from root to selected node
            val path = mutableListOf<NodeCardItem>()
            var current: NodeCardItem? = allNodes[id]
            while (current != null) {
                path.add(current)
                current = current.superiorId?.let { allNodes[it] }
            }
            path.reverse() // root -> selected node

            val updatedSelectedLevelList = getState().selectedLevelList.toMutableMap()
            val updatedFilteredNodeLevelList = getState().filteredNodeLevelList.toMutableMap()

            path.forEachIndexed { levelIndex, item ->
                // mark the selected node in each level
                updatedSelectedLevelList[levelIndex] = item.id

                // ensure children of each level are visible
                val children = fullLevelsByParent[item.id].orEmpty()
                if (children.isNotEmpty()) {
                    updatedFilteredNodeLevelList[levelIndex + 1] = children
                }
            }

            // Remove deeper levels beyond the selected path
            val maxLevel = path.size
            val keysToRemove = updatedFilteredNodeLevelList.keys.filter { it >= maxLevel + 1 }
            keysToRemove.forEach { updatedFilteredNodeLevelList.remove(it) }

            setState {
                copy(
                    selectedLevelList = updatedSelectedLevelList,
                    filteredNodeLevelList = updatedFilteredNodeLevelList,
                    lastSelectedLevel = id,
                    lastLevelCompleted = fullLevelsByParent[id].orEmpty().isEmpty(),
                )
            }

            if (fullLevelsByParent[id].orEmpty().isEmpty()) handleGetCardsZone()
        }

        // =======================
        // Cards Zone
        // =======================
        private fun handleGetCardsZone() {
            viewModelScope.launch {
                val id = getState().lastSelectedLevel
                runCatching { callUseCase { getCardsZoneUseCase(id) } }
                    .onSuccess { setState { copy(cardsZone = it) } }
                    .onFailure { LoggerHelperManager.logException(it) }
            }
        }

        // =======================
        // Evidences
        // =======================
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
                            if (state.evidences.toImages().size >= maxImages) context.getString(R.string.limit_images) else ""
                        }
                        EvidenceType.VICR -> {
                            val maxVideos = cardType?.quantityVideosCreate.defaultIfNull(0)
                            val maxVideoDuration = cardType?.videosDurationCreate.defaultIfNull(0) * 1000
                            val duration = fileHelper.getDuration(uri)
                            when {
                                state.evidences.toVideos().size >= maxVideos -> context.getString(R.string.limit_videos)
                                duration > maxVideoDuration -> context.getString(R.string.limit_video_duration)
                                else -> ""
                            }
                        }
                        EvidenceType.AUCR -> {
                            val maxAudios = cardType?.quantityAudiosCreate.defaultIfNull(0)
                            val maxAudioDuration = cardType?.audiosDurationCreate.defaultIfNull(0) * 1000
                            val duration = fileHelper.getDuration(uri)
                            when {
                                duration == 0L -> context.getString(R.string.invalid_audio)
                                state.evidences.toAudios().size >= maxAudios -> context.getString(R.string.limit_audios)
                                duration > maxAudioDuration -> context.getString(R.string.limit_audio_duration)
                                else -> ""
                            }
                        }
                        else -> ""
                    }

                if (errorMessage.isNotEmpty()) {
                    setState { copy(message = errorMessage) }
                    return@launch
                }

                val list = state.evidences.toMutableList()
                list.add(Evidence.fromCreateEvidence(state.uuid, uri.toString(), type.name))
                setState { copy(evidences = list) }
            }
        }

        private fun handleDeleteEvidence(evidence: Evidence) {
            viewModelScope.launch {
                val list = getState().evidences.filter { it.id != evidence.id }
                setState { copy(evidences = list) }
            }
        }

        // =======================
        // Save Card
        // =======================
        private fun handleSaveCard() {
            setState { copy(isLoading = true, message = context.getString(R.string.saving_card)) }
            viewModelScope.launch {
                val state = getState()
                val card =
                    Card.fromCreateCard(
                        areaId = state.lastSelectedLevel.toLong(),
                        level =
                            state.selectedLevelList.keys
                                .last()
                                .toLong(),
                        priorityId = state.selectedPriority,
                        cardTypeValue = "",
                        cardTypeId = state.selectedCardType,
                        preclassifierId = state.selectedPreclassifier,
                        comment = state.comment,
                        hasImages = state.evidences.hasImages(),
                        hasVideos = state.evidences.hasVideos(),
                        hasAudios = state.evidences.hasAudios(),
                        evidences = state.evidences,
                        uuid = state.uuid,
                    )

                runCatching { callUseCase { saveCardUseCase(card) } }
                    .onSuccess {
                        setState { copy(isCardSuccess = true) }
                        if (isCiltMode) sharedPreferences.saveCiltCard(it)
                    }.onFailure {
                        LoggerHelperManager.logException(it)
                        firebaseAnalyticsHelper.logCreateCardException(it)
                        setState { copy(message = it.localizedMessage ?: "") }
                    }
            }
        }

        // =======================
        // Utilities
        // =======================
        fun cleanMessage() {
            setState { copy(message = "") }
        }

        fun lastLevelCompleted(): Boolean =
            state.value.filteredNodeLevelList.isNotEmpty() &&
                state.value.selectedLevelList.size == state.value.filteredNodeLevelList.size
    }
