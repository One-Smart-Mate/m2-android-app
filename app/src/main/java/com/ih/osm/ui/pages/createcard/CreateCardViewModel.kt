package com.ih.osm.ui.pages.createcard

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ih.osm.R
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.core.file.FileHelper
import com.ih.osm.core.firebase.FirebaseNotificationType
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
import com.ih.osm.domain.usecase.level.FindLevelByMachineIdUseCase
import com.ih.osm.domain.usecase.level.GetChildrenLevelsUseCase
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
        private val getChildrenLevelsUseCase: GetChildrenLevelsUseCase,
        private val findLevelByMachineIdUseCase: FindLevelByMachineIdUseCase,
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

        // Cache for dynamically loaded levels (lazy loading)
        private val loadedLevelsCache = mutableMapOf<String, List<NodeCardItem>>()

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
            val levelsLoaded: Boolean = false,
            // Loading states for each section
            val isLoadingCardTypes: Boolean = false,
            val isLoadingPreclassifiers: Boolean = false,
            val isLoadingPriorities: Boolean = false,
            val isLoadingLevels: Boolean = false,
            // Machine ID search fields
            val machineIdSearchQuery: String = EMPTY,
            val isSearchingMachineId: Boolean = false,
            val machineIdSearchError: String = EMPTY,
            val machineIdSearchSuccess: Boolean = false,
        )

        init {
            Log.d(TAG, "===== CreateCardViewModel INIT =====")
            handleGetCardTypes()
        }

        fun process(action: CreateCardAction) {
            Log.d(TAG, "===== PROCESS ACTION: ${action.javaClass.simpleName} =====")
            when (action) {
                is CreateCardAction.SetCardType -> {
                    Log.d(TAG, "Action: SetCardType(id=${action.id})")
                    handleSetCardType(action.id)
                }
                is CreateCardAction.SetPreclassifier -> {
                    Log.d(TAG, "Action: SetPreclassifier(id=${action.id})")
                    handleSetPreclassifier(action.id)
                }
                is CreateCardAction.SetPriority -> {
                    Log.d(TAG, "Action: SetPriority(id=${action.id})")
                    handleSetPriority(action.id)
                }
                is CreateCardAction.SetLevel -> {
                    Log.d(TAG, "Action: SetLevel(id=${action.id}, key=${action.key})")
                    handleSetLevel(action.id, action.key)
                }
                is CreateCardAction.SetComment -> {
                    Log.d(TAG, "Action: SetComment(length=${action.comment.length})")
                    setState { copy(comment = action.comment) }
                }
                is CreateCardAction.DeleteEvidence -> {
                    Log.d(TAG, "Action: DeleteEvidence(id=${action.evidence.id})")
                    handleDeleteEvidence(action.evidence)
                }
                is CreateCardAction.AddEvidence -> {
                    Log.d(TAG, "Action: AddEvidence(type=${action.type})")
                    handleAddEvidence(action.uri, action.type)
                }
                is CreateCardAction.Save -> {
                    Log.d(TAG, "Action: Save")
                    handleSaveCard()
                }
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
                            val maxVideoDuration =
                                cardType?.videosDurationCreate.defaultIfNull(0) * 1000
                            if (state.evidences.toVideos().size == maxVideos) {
                                context.getString(R.string.limit_videos)
                            } else {
                                val videoDuration = fileHelper.getDuration(uri)
                                if (videoDuration > maxVideoDuration) {
                                    context.getString(R.string.limit_video_duration)
                                } else {
                                    EMPTY
                                }
                            }
                        }

                        EvidenceType.AUCR -> {
                            val maxAudios = cardType?.quantityAudiosCreate.defaultIfNull(0)
                            val maxAudioDuration =
                                cardType?.audiosDurationCreate.defaultIfNull(0) * 1000
                            val audioDuration = fileHelper.getDuration(uri)

                            when {
                                audioDuration == 0L -> context.getString(R.string.invalid_audio)
                                state.evidences.toAudios().size == maxAudios -> context.getString(R.string.limit_audios)
                                audioDuration > maxAudioDuration -> context.getString(R.string.limit_audio_duration)
                                else -> EMPTY
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
            Log.d(TAG, "handleSetCardType: START - CardType ID=$id")
            viewModelScope.launch {
                Log.d(TAG, "handleSetCardType: Clearing state and setting selectedCardType=$id")
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
                Log.d(TAG, "handleSetCardType: Calling handleGetPreclassifiers(id=$id)")
                handleGetPreclassifiers(id)
                Log.d(TAG, "handleSetCardType: Calling handleGetCardType(id=$id)")
                handleGetCardType(id)
                Log.d(TAG, "handleSetCardType: END")
            }
        }

        private fun handleSetPreclassifier(id: String) {
            Log.d(TAG, "handleSetPreclassifier: START - Preclassifier ID=$id")
            viewModelScope.launch {
                Log.d(TAG, "handleSetPreclassifier: Setting selectedPreclassifier=$id, clearing priorities")
                setState {
                    copy(
                        selectedPreclassifier = id,
                        selectedPriority = EMPTY,
                        priorityList = emptyList(),
                    )
                }
                Log.d(TAG, "handleSetPreclassifier: Calling handleGetPriorities()")
                handleGetPriorities()
                Log.d(TAG, "handleSetPreclassifier: END")
            }
        }

        fun setCiltMode(enabled: Boolean) {
            isCiltMode = enabled
            if (!enabled) {
                superiorIdCilt = null
            }
        }

        fun setSuperiorIdCilt(id: String?) {
            if (isCiltMode) {
                superiorIdCilt = id
            }
        }

        private fun handleSetPriority(id: String) {
            Log.d(TAG, "handleSetPriority: START - Priority ID=$id, isCiltMode=$isCiltMode, superiorIdCilt=$superiorIdCilt")
            viewModelScope.launch {
                val rootId =
                    if (isCiltMode) {
                        superiorIdCilt ?: "0"
                    } else {
                        "0"
                    }
                Log.d(TAG, "handleSetPriority: Loading levels from rootId=$rootId")
                val levelList = getLevelById(rootId, 0)
                Log.d(TAG, "handleSetPriority: Loaded ${levelList.size} level groups")
                setState { copy(selectedPriority = id, nodeLevelList = levelList) }
                Log.d(TAG, "handleSetPriority: END")
            }
        }

        private suspend fun getLevelById(
            id: String,
            selectedKey: Int,
        ): Map<Int, List<NodeCardItem>> {
            Log.d(TAG, "getLevelById: START - parentId=$id, key=$selectedKey")
            val state = getState()
            val map = state.nodeLevelList.toMutableMap()
            val selectedMap = state.selectedLevelList.toMutableMap()

            // Clear deeper levels
            for (index in selectedKey until map.keys.size) {
                map[index] = emptyList()
                selectedMap[index] = EMPTY
            }

            // Check cache first
            val cachedChildren = loadedLevelsCache[id]
            if (cachedChildren != null) {
                Log.d(TAG, "getLevelById: CACHE HIT - ${cachedChildren.size} children")
                map[selectedKey] = cachedChildren
            } else {
                // Lazy load children from server
                Log.d(TAG, "getLevelById: CACHE MISS - Loading children from server")
                kotlin
                    .runCatching {
                        callUseCase { getChildrenLevelsUseCase(parentId = id) }
                    }.onSuccess { result ->
                        when (result) {
                            is com.ih.osm.domain.model.Result.Success -> {
                                val children = result.data.toNodeItemList()
                                Log.d(TAG, "getLevelById: SUCCESS - Loaded ${children.size} children")
                                loadedLevelsCache[id] = children
                                map[selectedKey] = children
                            }
                            is com.ih.osm.domain.model.Result.Error -> {
                                Log.e(TAG, "getLevelById: ERROR - ${result.message}")
                                map[selectedKey] = emptyList()
                            }
                            else -> {
                                Log.d(TAG, "getLevelById: LOADING state")
                                map[selectedKey] = emptyList()
                            }
                        }
                    }.onFailure { exception ->
                        Log.e(TAG, "getLevelById: EXCEPTION - ${exception.message}", exception)
                        map[selectedKey] = emptyList()
                    }
            }

            selectedMap[selectedKey.minus(1)] = id
            setState { copy(selectedLevelList = selectedMap, lastSelectedLevel = id) }
            Log.d(TAG, "getLevelById: END")
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
            Log.d(TAG, "checkLastLevelSection: START - levelId=$id")

            // Check if this level has children (check cache)
            val hasChildren = loadedLevelsCache[id]?.isNotEmpty() ?: false

            Log.d(TAG, "checkLastLevelSection: hasChildren=$hasChildren")

            if (hasChildren) {
                // Level has children, clear evidences
                setState { copy(evidences = emptyList()) }
            } else {
                // Leaf level reached, load cards for this zone
                Log.d(TAG, "checkLastLevelSection: Leaf level - Loading cards zone")
                handleGetCardsZone()
            }

            setState { copy(lastLevelCompleted = !hasChildren) }
            Log.d(TAG, "checkLastLevelSection: END - lastLevelCompleted=${!hasChildren}")
        }

        private fun handleGetPreclassifiers(id: String) {
            Log.d(TAG, "handleGetPreclassifiers: START - CardType ID=$id")
            setState { copy(isLoadingPreclassifiers = true) }
            viewModelScope.launch {
                kotlin
                    .runCatching {
                        Log.d(TAG, "handleGetPreclassifiers: Calling GetPreclassifiersUseCase")
                        callUseCase { getPreclassifiersUseCase() }
                    }.onSuccess { allPreclassifiers ->
                        Log.d(TAG, "handleGetPreclassifiers: SUCCESS - Received ${allPreclassifiers.size} total preclassifiers")
                        val filtered = allPreclassifiers.filter { it.cardTypeId == id }
                        Log.d(TAG, "handleGetPreclassifiers: Filtered to ${filtered.size} preclassifiers for CardType ID=$id")
                        setState {
                            copy(preclassifierList = filtered.toNodeItemCard(), isLoadingPreclassifiers = false)
                        }
                        Log.d(TAG, "handleGetPreclassifiers: State updated with ${filtered.size} preclassifiers")
                        cleanScreenStates()
                    }.onFailure { exception ->
                        Log.e(TAG, "handleGetPreclassifiers: FAILURE - ${exception.message}", exception)
                        LoggerHelperManager.logException(exception)
                        setState { copy(isLoadingPreclassifiers = false) }
                        cleanScreenStates(exception.localizedMessage.orEmpty())
                    }
                Log.d(TAG, "handleGetPreclassifiers: END")
            }
        }

        private fun handleGetCardTypes() {
            Log.d(TAG, "handleGetCardTypes: START")
            setState { copy(isLoadingCardTypes = true) }
            viewModelScope.launch {
                kotlin
                    .runCatching {
                        Log.d(TAG, "handleGetCardTypes: Calling GetCardTypesUseCase")
                        callUseCase { getCardTypesUseCase() }
                    }.onSuccess { cardTypes ->
                        Log.d(TAG, "handleGetCardTypes: SUCCESS - Received ${cardTypes.size} card types")
                        setState { copy(cardTypeList = cardTypes.toNodeItemList(), isLoadingCardTypes = false) }
                        Log.d(TAG, "handleGetCardTypes: Calling handleGetLevels()")
                        handleGetLevels()
                    }.onFailure { exception ->
                        Log.e(TAG, "handleGetCardTypes: FAILURE - ${exception.message}", exception)
                        LoggerHelperManager.logException(exception)
                        setState { copy(isLoadingCardTypes = false) }
                        cleanScreenStates(exception.localizedMessage.orEmpty())
                    }
                Log.d(TAG, "handleGetCardTypes: END")
            }
        }

        private fun handleGetPriorities() {
            Log.d(TAG, "handleGetPriorities: START")
            setState { copy(isLoadingPriorities = true) }
            viewModelScope.launch {
                kotlin
                    .runCatching {
                        Log.d(TAG, "handleGetPriorities: Calling GetPrioritiesUseCase")
                        callUseCase { getPrioritiesUseCase() }
                    }.onSuccess { priorities ->
                        Log.d(TAG, "handleGetPriorities: SUCCESS - Received ${priorities.size} priorities")
                        setState { copy(priorityList = priorities.toNodeItemCard(), isLoadingPriorities = false) }
                        cleanScreenStates()
                    }.onFailure { exception ->
                        Log.e(TAG, "handleGetPriorities: FAILURE - ${exception.message}", exception)
                        LoggerHelperManager.logException(exception)
                        setState { copy(isLoadingPriorities = false) }
                        cleanScreenStates(exception.localizedMessage.orEmpty())
                    }
                Log.d(TAG, "handleGetPriorities: END")
            }
        }

        private fun handleGetLevels() {
            Log.d(TAG, "handleGetLevels: START - Loading root levels only (lazy loading)")
            setState { copy(isLoadingLevels = true) }
            viewModelScope.launch {
                kotlin
                    .runCatching {
                        Log.d(TAG, "handleGetLevels: Calling GetLevelsUseCase")
                        callUseCase { getLevelsUseCase() }
                    }.onSuccess { levels ->
                        // Filter to only root levels (no superior) to avoid loading all 700+
                        val rootLevels = levels.filter { it.superiorId.isNullOrBlank() }
                        Log.d(TAG, "handleGetLevels: SUCCESS - Received ${levels.size} levels, filtered to ${rootLevels.size} root levels")
                        setState { copy(levelList = rootLevels.toNodeItemList(), levelsLoaded = true, isLoadingLevels = false) }
                        cleanScreenStates()
                        checkCatalogs()
                    }.onFailure { exception ->
                        Log.e(TAG, "handleGetLevels: FAILURE - ${exception.message}", exception)
                        LoggerHelperManager.logException(exception)
                        setState { copy(isLoadingLevels = false) }
                        cleanScreenStates(exception.localizedMessage.orEmpty())
                    }
                Log.d(TAG, "handleGetLevels: END")
            }
        }

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
                kotlin
                    .runCatching {
                        callUseCase { saveCardUseCase(card) }
                    }.onSuccess {
                        setState { copy(isCardSuccess = true) }
                        if (isCiltMode) {
                            sharedPreferences.saveCiltCard(it)
                        }
                        cleanScreenStates()
                    }.onFailure {
                        LoggerHelperManager.logException(it)
                        firebaseAnalyticsHelper.logCreateCardException(it)
                        cleanScreenStates(it.localizedMessage.orEmpty())
                    }
            }
        }

        private fun handleGetCardType(id: String) {
            Log.d(TAG, "handleGetCardType: START - CardType ID=$id")
            viewModelScope.launch {
                kotlin
                    .runCatching {
                        Log.d(TAG, "handleGetCardType: Calling GetCardTypeUseCase(id=$id)")
                        callUseCase { getCardTypeUseCase(id) }
                    }.onSuccess { cardType ->
                        if (cardType != null) {
                            Log.d(TAG, "handleGetCardType: SUCCESS - CardType name=${cardType.name}")
                            setState {
                                copy(
                                    cardType = cardType,
                                    audioDuration = cardType.audiosDurationCreate.defaultIfNull(120),
                                )
                            }
                            cleanScreenStates()
                        } else {
                            Log.w(TAG, "handleGetCardType: WARNING - CardType not found for ID=$id")
                        }
                    }.onFailure { exception ->
                        Log.e(TAG, "handleGetCardType: FAILURE - ${exception.message}", exception)
                        LoggerHelperManager.logException(exception)
                        cleanScreenStates(exception.localizedMessage.orEmpty())
                    }
                Log.d(TAG, "handleGetCardType: END")
            }
        }

        private fun handleGetCardsZone() {
            viewModelScope.launch {
                val id = getState().lastSelectedLevel
                kotlin
                    .runCatching {
                        callUseCase { getCardsZoneUseCase(id) }
                    }.onSuccess {
                        setState { copy(cardsZone = it) }
                        cleanScreenStates()
                    }.onFailure {
                        LoggerHelperManager.logException(it)
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

        fun handleSearchByMachineId() {
            val machineId = getState().machineIdSearchQuery.trim()
            Log.d(TAG, "handleSearchByMachineId: START - machineId='$machineId'")

            // Validate input
            if (machineId.isBlank()) {
                Log.w(TAG, "handleSearchByMachineId: Machine ID is blank")
                setState { copy(machineIdSearchError = context.getString(R.string.required_machine_id)) }
                return
            }

            // Clear previous errors and set loading state
            setState {
                copy(
                    isSearchingMachineId = true,
                    machineIdSearchError = EMPTY,
                    machineIdSearchSuccess = false,
                )
            }

            viewModelScope.launch {
                kotlin
                    .runCatching {
                        Log.d(TAG, "handleSearchByMachineId: Calling FindLevelByMachineIdUseCase")
                        callUseCase { findLevelByMachineIdUseCase(machineId) }
                    }.onSuccess { result ->
                        when (result) {
                            is com.ih.osm.domain.model.Result.Success -> {
                                val hierarchy = result.data
                                Log.d(TAG, "handleSearchByMachineId: SUCCESS - Found hierarchy with ${hierarchy.size} levels")

                                // Build the level hierarchy for UI
                                val newNodeLevelList = mutableMapOf<Int, List<NodeCardItem>>()
                                val newSelectedLevelList = mutableMapOf<Int, String>()

                                hierarchy.forEachIndexed { index, level ->
                                    Log.d(TAG, "  Level $index: id=${level.id}, name=${level.name}")
                                    // Add each level to its corresponding depth
                                    newNodeLevelList[index] = listOf(level).toNodeItemList()
                                    newSelectedLevelList[index] = level.id
                                }

                                // First update state with the hierarchy
                                setState {
                                    copy(
                                        nodeLevelList = newNodeLevelList,
                                        selectedLevelList = newSelectedLevelList,
                                        isSearchingMachineId = true, // Keep loading while we fetch children
                                        machineIdSearchSuccess = false,
                                        machineIdSearchError = EMPTY,
                                    )
                                }

                                // Load children of the last level if it exists
                                val lastLevel = hierarchy.lastOrNull()
                                val updatedNodeLevelList =
                                    if (lastLevel != null) {
                                        Log.d(TAG, "handleSearchByMachineId: Loading children of last level id=${lastLevel.id}")
                                        getLevelById(lastLevel.id, hierarchy.size)
                                    } else {
                                        newNodeLevelList
                                    }

                                // Check if we can continue navigating (last level has children)
                                val childrenOfLastLevel = updatedNodeLevelList[hierarchy.size] ?: emptyList()
                                val hasChildren = childrenOfLastLevel.isNotEmpty()

                                Log.d(
                                    TAG,
                                    "handleSearchByMachineId: Last level has ${childrenOfLastLevel.size} children. " +
                                        "Can continue: $hasChildren",
                                )

                                // Update state with the complete hierarchy including children
                                setState {
                                    copy(
                                        nodeLevelList = updatedNodeLevelList,
                                        selectedLevelList = newSelectedLevelList,
                                        isSearchingMachineId = false,
                                        machineIdSearchSuccess = true,
                                        machineIdSearchError = EMPTY,
                                        lastLevelCompleted = !hasChildren, // Only mark as completed if no children
                                    )
                                }

                                Log.d(TAG, "handleSearchByMachineId: State updated successfully. lastLevelCompleted=${!hasChildren}")
                            }

                            is com.ih.osm.domain.model.Result.Error -> {
                                Log.e(TAG, "handleSearchByMachineId: ERROR - ${result.message}")
                                val errorMessage =
                                    when (result.message) {
                                        "REQUIRED" -> context.getString(R.string.required_machine_id)
                                        "NOT_FOUND" -> context.getString(R.string.machine_id_not_found)
                                        "SEARCH_ERROR" -> context.getString(R.string.machine_id_search_error)
                                        else -> context.getString(R.string.machine_id_not_found)
                                    }
                                setState {
                                    copy(
                                        isSearchingMachineId = false,
                                        machineIdSearchError = errorMessage,
                                    )
                                }
                            }

                            is com.ih.osm.domain.model.Result.Loading -> {
                                Log.d(TAG, "handleSearchByMachineId: Loading...")
                            }
                        }
                    }.onFailure { exception ->
                        LoggerHelperManager.logException(exception)
                        Log.e(TAG, "handleSearchByMachineId: EXCEPTION - ${exception.localizedMessage}", exception)
                        setState {
                            copy(
                                isSearchingMachineId = false,
                                machineIdSearchError = context.getString(R.string.machine_id_search_error),
                            )
                        }
                    }
            }
        }

        fun updateMachineIdSearchQuery(query: String) {
            setState {
                copy(
                    machineIdSearchQuery = query,
                    machineIdSearchError = EMPTY,
                )
            }
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
