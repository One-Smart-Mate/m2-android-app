package com.ih.osm.ui.pages.createcard

import android.content.Context
import android.net.Uri
import android.util.Log
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

private const val TAG = "CreateCardViewModel"

data class ScrollTarget(
    val levelIndex: Int? = null,
    val itemIndex: Int? = null,
    val verticalIndex: Int? = null,
)

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
        // One-shot flow para notificar a la UI dónde hacer scroll
        private val _scrollTarget = MutableSharedFlow<ScrollTarget>(replay = 0)
        val scrollTarget = _scrollTarget.asSharedFlow()

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
            val levelsByParent: Map<String, List<NodeCardItem>> = emptyMap(),
            val filteredNodeLevelList: Map<Int, List<NodeCardItem>> = mutableMapOf(),
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
            // Loading indicators
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

        // ============================================================
        // Actions
        // ============================================================
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

        // ============================================================
        // CILT Mode
        // ============================================================
        fun setCiltMode(enabled: Boolean) {
            isCiltMode = enabled
            if (!enabled) superiorIdCilt = null
        }

        fun setSuperiorIdCilt(id: String?) {
            if (isCiltMode) superiorIdCilt = id
        }

        // ============================================================
        // Scroll request helpers (también puedes llamar estas desde UI si lo necesitas)
        // ============================================================
        fun requestHorizontalScroll(
            levelIndex: Int,
            itemIndex: Int,
            verticalIndex: Int? = null,
        ) {
            viewModelScope.launch {
                _scrollTarget.emit(ScrollTarget(levelIndex = levelIndex, itemIndex = itemIndex, verticalIndex = verticalIndex))
            }
        }

        fun requestVerticalScroll(verticalIndex: Int) {
            viewModelScope.launch {
                _scrollTarget.emit(ScrollTarget(verticalIndex = verticalIndex))
            }
        }

        // ============================================================
        // Search Text
        // ============================================================
        fun updateSearchText(text: String) {
            setState { copy(searchText = text) }
            filterLevels()
        }

        // ============================================================
        // Card Types
        // ============================================================
        private fun handleSetCardType(id: String) {
            viewModelScope.launch {
                setState {
                    copy(
                        selectedCardType = id,
                        selectedPreclassifier = "",
                        selectedPriority = "",
                        selectedLevelList = emptyMap(),
                        filteredNodeLevelList = emptyMap(),
                        evidences = emptyList(),
                        comment = "",
                        lastSelectedLevel = "",
                        lastLevelCompleted = false,
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
                        setState {
                            copy(
                                cardTypeList = list.toNodeItemList(),
                                isLoadingCardTypes = false,
                            )
                        }
                    }.onFailure {
                        setState { copy(isLoadingCardTypes = false, message = it.localizedMessage ?: "") }
                    }
            }
        }

        private fun handleGetCardType(id: String) {
            viewModelScope.launch {
                runCatching { callUseCase { getCardTypeUseCase(id) } }
                    .onSuccess { cardType ->
                        cardType?.let {
                            setState { copy(audioDuration = it.audiosDurationCreate.defaultIfNull(120), cardType = it) }
                        }
                    }.onFailure { LoggerHelperManager.logException(it) }
            }
        }

        // ============================================================
        // Preclassifiers
        // ============================================================
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
                        setState {
                            copy(
                                preclassifierList = filtered.toNodeItemCard(),
                                isLoadingPreclassifiers = false,
                            )
                        }
                    }.onFailure {
                        setState { copy(isLoadingPreclassifiers = false, message = it.localizedMessage ?: "") }
                    }
            }
        }

        // ============================================================
        // Priorities
        // ============================================================
        private fun handleSetPriority(id: String) {
            val root = getState().levelsByParent["0"].orEmpty()
            setState {
                copy(
                    selectedPriority = id,
                    selectedLevelList = emptyMap(),
                    filteredNodeLevelList = mapOf(0 to root),
                )
            }
            // opcional: pedir scroll vertical al primer nivel (0)
            viewModelScope.launch {
                _scrollTarget.emit(ScrollTarget(verticalIndex = 0, levelIndex = 0, itemIndex = 0))
            }
        }

        private fun handleGetPriorities() {
            setState { copy(isLoadingPriorities = true) }
            viewModelScope.launch {
                runCatching { callUseCase { getPrioritiesUseCase() } }
                    .onSuccess { list ->
                        setState {
                            copy(
                                priorityList = list.toNodeItemCard(),
                                isLoadingPriorities = false,
                            )
                        }
                    }.onFailure {
                        setState { copy(isLoadingPriorities = false, message = it.localizedMessage ?: "") }
                    }
            }
        }

        // ============================================================
        // Levels
        // ============================================================
        private fun handleGetLevels() {
            setState { copy(isLoadingLevels = true) }
            viewModelScope.launch {
                runCatching { callUseCase { getLevelsUseCase() } }
                    .onSuccess { levels ->
                        val byParent =
                            levels
                                .groupBy { it.superiorId }
                                .mapValues { it.value.toNodeItemList() }

                        setState { copy(levelsByParent = byParent, isLoadingLevels = false, levelsLoaded = true) }
                        filterLevels()
                    }.onFailure {
                        setState { copy(isLoadingLevels = false, message = it.localizedMessage ?: "") }
                    }
            }
        }

        private fun filterLevels() {
            val levels = getState().levelsByParent
            val search = getState().searchText.trim()

            // Si no hay búsqueda → mostrar solo el nivel raíz
            if (search.isBlank()) {
                setState {
                    copy(
                        filteredNodeLevelList = mapOf(0 to levels["0"].orEmpty()),
                        selectedLevelList = emptyMap(),
                        lastSelectedLevel = "",
                        lastLevelCompleted = false,
                    )
                }
                return
            }

            // Mapa plano id → nodo
            val allNodes = levels.values.flatten().associateBy { it.id }

            // Buscar coincidencias por nombre
            val matches =
                allNodes.values.filter {
                    it.name.contains(search, ignoreCase = true)
                }

            if (matches.isEmpty()) {
                setState { copy(filteredNodeLevelList = emptyMap()) }
                return
            }

            // Vamos a construir SOLO la ruta del PRIMER match
            val target = matches.first()

            // Obtener path desde root → target
            val path = mutableListOf<NodeCardItem>()
            var current: NodeCardItem? = target

            while (current != null) {
                path.add(current)
                current = current.superiorId?.let { allNodes[it] }
            }

            path.reverse()

            // Construir columnas
            val newFiltered = mutableMapOf<Int, List<NodeCardItem>>()
            val newSelected = mutableMapOf<Int, String>()
            var matchedLevelIndex = 0
            var matchedIndexInLevel = 0

            path.forEachIndexed { levelIndex, node ->

                // Los hermanos en ese nivel:
                val siblings =
                    if (levelIndex == 0) {
                        levels["0"].orEmpty() // nivel raíz
                    } else {
                        levels[path[levelIndex - 1].id].orEmpty() // hijos del padre
                    }

                newFiltered[levelIndex] = siblings
                newSelected[levelIndex] = node.id

                if (node.id == target.id) {
                    matchedLevelIndex = levelIndex
                    matchedIndexInLevel = siblings.indexOfFirst { it.id == node.id }.let { if (it >= 0) it else 0 }
                }
            }

            setState {
                copy(
                    filteredNodeLevelList = newFiltered,
                    selectedLevelList = newSelected,
                    lastSelectedLevel = target.id,
                    lastLevelCompleted = levels[target.id].isNullOrEmpty(),
                )
            }

            // Emitir evento de scroll: horizontal (nivel) + vertical (mismo index) para que la UI lo interprete
            viewModelScope.launch {
                val realLevelIndex = getVisibleLevelIndex(target.id) ?: matchedLevelIndex
                _scrollTarget.emit(
                    ScrollTarget(
                        levelIndex = realLevelIndex,
                        itemIndex = matchedIndexInLevel,
                        verticalIndex = realLevelIndex,
                    ),
                )
            }

            // Si es hoja → cargar cards
            if (levels[target.id].isNullOrEmpty()) {
                handleGetCardsZone()
            }
        }

        private fun handleSetLevel(
            id: String,
            key: Int,
        ) {
            Log.e("test", "id $id --key $key")
            val full = getState().levelsByParent
            val allNodes = full.values.flatten().associateBy { it.id }

            // Obtener path root → id
            val path = mutableListOf<NodeCardItem>()
            var current = allNodes[id]

            while (current != null) {
                path.add(current)
                current = current.superiorId?.let { allNodes[it] }
            }
            path.reverse()

            val newFiltered = mutableMapOf<Int, List<NodeCardItem>>()
            val newSelected = mutableMapOf<Int, String>()

            path.forEachIndexed { levelIndex, node ->
                // Hermanos del nivel
                val siblings =
                    if (levelIndex == 0) {
                        full["0"].orEmpty()
                    } else {
                        full[path[levelIndex - 1].id].orEmpty()
                    }

                newFiltered[levelIndex] = siblings
                newSelected[levelIndex] = node.id

                // Si tiene hijos → agregar siguiente nivel
                val children = full[node.id].orEmpty()
                if (children.isNotEmpty()) {
                    newFiltered[levelIndex + 1] = children
                }
            }

            setState {
                copy(
                    filteredNodeLevelList = newFiltered,
                    selectedLevelList = newSelected,
                    lastSelectedLevel = id,
                    lastLevelCompleted = full[id].orEmpty().isEmpty(),
                )
            }

            // Encontrar el índice dentro de la ruta (posición visible del nivel)
            val selectedLevelIndex = path.indexOfFirst { it.id == id }.let { if (it >= 0) it else 0 }

            // Calcular índice dentro del nivel (para scroll horizontal) usando selectedLevelIndex
            val indexInLevel = newFiltered[selectedLevelIndex]?.indexOfFirst { it.id == id }?.let { if (it >= 0) it else 0 } ?: 0

            // Emitir evento de scroll: usa selectedLevelIndex (índice visible) y indexInLevel
            viewModelScope.launch {
                val levelIndex = getVisibleLevelIndex(id) ?: key
                _scrollTarget.emit(
                    ScrollTarget(
                        levelIndex = levelIndex,
                        itemIndex = indexInLevel,
                        verticalIndex = levelIndex,
                    ),
                )
            }

            if (full[id].isNullOrEmpty()) {
                handleGetCardsZone()
            }
        }

        // ============================================================
        // Cards Zone
        // ============================================================
        private fun handleGetCardsZone() {
            viewModelScope.launch {
                val id = getState().lastSelectedLevel
                runCatching { callUseCase { getCardsZoneUseCase(id) } }
                    .onSuccess { setState { copy(cardsZone = it) } }
                    .onFailure { LoggerHelperManager.logException(it) }
            }
        }

        // ============================================================
        // Evidences
        // ============================================================
        private fun handleAddEvidence(
            uri: Uri,
            type: EvidenceType,
        ) {
            viewModelScope.launch {
                val state = getState()
                val cardType = state.cardType

                val error =
                    when (type) {
                        EvidenceType.IMCR -> {
                            val max = cardType?.quantityImagesCreate.defaultIfNull(0)
                            if (state.evidences.toImages().size >= max) context.getString(R.string.limit_images) else ""
                        }

                        EvidenceType.VICR -> {
                            val max = cardType?.quantityVideosCreate.defaultIfNull(0)
                            val maxDur = cardType?.videosDurationCreate.defaultIfNull(0) * 1000
                            val dur = fileHelper.getDuration(uri)

                            when {
                                state.evidences.toVideos().size >= max -> context.getString(R.string.limit_videos)
                                dur > maxDur -> context.getString(R.string.limit_video_duration)
                                else -> ""
                            }
                        }

                        EvidenceType.AUCR -> {
                            val max = cardType?.quantityAudiosCreate.defaultIfNull(0)
                            val maxDur = cardType?.audiosDurationCreate.defaultIfNull(0) * 1000
                            val dur = fileHelper.getDuration(uri)

                            when {
                                dur == 0L -> context.getString(R.string.invalid_audio)
                                state.evidences.toAudios().size >= max -> context.getString(R.string.limit_audios)
                                dur > maxDur -> context.getString(R.string.limit_audio_duration)
                                else -> ""
                            }
                        }

                        else -> ""
                    }

                if (error.isNotEmpty()) {
                    setState { copy(message = error) }
                    return@launch
                }

                val updated = state.evidences.toMutableList()
                updated.add(Evidence.fromCreateEvidence(state.uuid, uri.toString(), type.name))
                setState { copy(evidences = updated) }
            }
        }

        private fun handleDeleteEvidence(evidence: Evidence) {
            viewModelScope.launch {
                val updated = getState().evidences.filter { it.id != evidence.id }
                setState { copy(evidences = updated) }
            }
        }

        // ============================================================
        // Save Card
        // ============================================================
        private fun handleSaveCard() {
            setState { copy(isLoading = true, message = context.getString(R.string.saving_card)) }

            viewModelScope.launch {
                val s = getState()

                val card =
                    Card.fromCreateCard(
                        areaId = s.lastSelectedLevel.toLong(),
                        level =
                            s.selectedLevelList.keys
                                .last()
                                .toLong(),
                        priorityId = s.selectedPriority,
                        cardTypeValue = "",
                        cardTypeId = s.selectedCardType,
                        preclassifierId = s.selectedPreclassifier,
                        comment = s.comment,
                        hasImages = s.evidences.hasImages(),
                        hasVideos = s.evidences.hasVideos(),
                        hasAudios = s.evidences.hasAudios(),
                        evidences = s.evidences,
                        uuid = s.uuid,
                    )

                runCatching { callUseCase { saveCardUseCase(card) } }
                    .onSuccess {
                        setState { copy(isCardSuccess = true) }
                        if (isCiltMode) sharedPreferences.saveCiltCard(it)
                    }.onFailure {
                        firebaseAnalyticsHelper.logCreateCardException(it)
                        setState { copy(message = it.localizedMessage ?: "") }
                    }
            }
        }

        // ============================================================
        // Utilities
        // ============================================================
        fun cleanMessage() {
            setState { copy(message = "") }
        }

        /**
         * Devuelve el nivel/columna donde vive un nodo dentro del mapa filteredNodeLevelList
         */
        private fun getVisibleLevelIndex(nodeId: String): Int? {
            val columns = getState().filteredNodeLevelList
            for ((levelIndex, list) in columns) {
                if (list.any { it.id == nodeId }) return levelIndex
            }
            return null
        }
    }
