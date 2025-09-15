package com.ih.osm.ui.pages.opl

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ih.osm.R
import com.ih.osm.domain.model.NodeCardItem
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.model.toNodeItemList
import com.ih.osm.domain.usecase.level.GetLevelsUseCase
import com.ih.osm.domain.usecase.opl.GetOplsByLevelUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.pages.opl.action.OplListAction
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OplListViewModel
    @Inject
    constructor(
        private val getLevelsUseCase: GetLevelsUseCase,
        private val getOplsByLevelUseCase: GetOplsByLevelUseCase,
        @ApplicationContext private val context: Context,
    ) : BaseViewModel<OplListViewModel.UiState>(UiState()) {
        data class UiState(
            val oplList: List<Opl> = emptyList(),
            val isLoading: Boolean = false,
            val message: String = EMPTY,
            val currentFilter: String = EMPTY,
            val levelList: List<NodeCardItem> = emptyList(),
            val nodeLevelList: Map<Int, List<NodeCardItem>> = mutableMapOf(),
            val selectedLevelList: Map<Int, String> = mutableMapOf(),
            val lastSelectedLevel: String = EMPTY,
            val lastLevelCompleted: Boolean = false,
        )

        init {
            handleGetLevels()
        }

        fun handleAction(action: OplListAction) {
            when (action) {
                is OplListAction.UpdateList -> handleUpdateOplList()
                is OplListAction.SetLevel -> handleSetLevel(action.id, action.key)
            }
        }

        private fun handleGetLevels() {
            viewModelScope.launch {
                kotlin
                    .runCatching {
                        callUseCase { getLevelsUseCase() }
                    }.onSuccess { levels ->
                        val levelList = levels.toNodeItemList()

                        // Initialize the first level with root elements (superiorId = "0")
                        val rootLevels = levelList.filter { it.superiorId == "0" }

                        val initialNodeLevelList =
                            if (rootLevels.isNotEmpty()) {
                                mapOf(0 to rootLevels)
                            } else {
                                emptyMap()
                            }

                        setState {
                            copy(
                                levelList = levelList,
                                nodeLevelList = initialNodeLevelList,
                                isLoading = false,
                            )
                        }
                    }.onFailure {
                        setState {
                            copy(
                                isLoading = false,
                                message = it.localizedMessage.orEmpty(),
                            )
                        }
                    }
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

            setState {
                copy(
                    selectedLevelList = selectedMap,
                    lastSelectedLevel = id,
                )
            }

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
                // Load OPLs for the selected level
                handleGetOplsByLevel(id)
                setState { copy(nodeLevelList = list) }
            }
        }

        private suspend fun checkLastLevelSection(id: String) {
            val isEmpty = getState().levelList.none { it.superiorId == id }
            setState { copy(lastLevelCompleted = isEmpty) }
        }

        private fun handleGetOplsByLevel(levelId: String) {
            viewModelScope.launch {
                kotlin
                    .runCatching {
                        callUseCase { getOplsByLevelUseCase(levelId) }
                    }.onSuccess { oplList ->
                        setState {
                            copy(
                                oplList = oplList,
                                message = EMPTY,
                            )
                        }
                    }.onFailure { exception ->
                        setState {
                            copy(
                                oplList = emptyList(),
                                message =
                                    context.getString(
                                        R.string.error_loading_opls,
                                        exception.localizedMessage,
                                    ),
                            )
                        }
                    }
            }
        }

        private fun handleUpdateOplList() {
            viewModelScope.launch {
                // Reload from the current selected level
                val currentSelectedLevel = getState().selectedLevelList.values.lastOrNull()
                if (currentSelectedLevel?.isNotEmpty() == true) {
                    handleGetOplsByLevel(currentSelectedLevel)
                } else {
                    setState {
                        copy(
                            oplList = emptyList(),
                            message = context.getString(R.string.select_level),
                        )
                    }
                }
            }
        }
    }
