package com.ih.osm.ui.pages.opllist

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ih.osm.domain.model.NodeCardItem
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.model.toNodeItemList
import com.ih.osm.domain.usecase.level.GetLevelsUseCase
import com.ih.osm.domain.usecase.opl.GetOplsByLevelUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import com.ih.osm.ui.pages.opllist.action.OplListAction
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
            val filteredOplList: List<Opl> = emptyList(),
            val isLoading: Boolean = true,
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
                is OplListAction.Filters -> handleFilterOpl(action.filter)
                is OplListAction.SetLevel -> handleSetLevel(action.id, action.key)
                OplListAction.Create -> TODO()
                is OplListAction.Detail -> TODO()
            }
        }

        private fun handleGetLevels() {
            viewModelScope.launch {
                kotlin.runCatching {
                    callUseCase { getLevelsUseCase() }
                }.onSuccess { levels ->
                    val levelList = levels.toNodeItemList()
                    println("DEBUG OPL - Total levels received: ${levelList.size}")
                    levelList.forEach { level ->
                        println("DEBUG OPL - Level: id=${level.id}, name=${level.name}, superiorId=${level.superiorId}")
                    }

                    // Inicializar el primer nivel con elementos raíz (superiorId = "0")
                    val rootLevels = levelList.filter { it.superiorId == "0" }
                    println("DEBUG OPL - Root levels found: ${rootLevels.size}")

                    val initialNodeLevelList =
                        if (rootLevels.isNotEmpty()) {
                            mapOf(0 to rootLevels)
                        } else {
                            emptyMap()
                        }

                    println("DEBUG OPL - Initial node level list: $initialNodeLevelList")

                    setState {
                        copy(
                            levelList = levelList,
                            nodeLevelList = initialNodeLevelList,
                            isLoading = false,
                        )
                    }
                    // No cargar OPLs estáticos al inicio
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
                // Cargar OPLs para el nivel seleccionado
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
                setState { copy(isLoading = true, message = "Cargando OPLs...") }
                kotlin.runCatching {
                    callUseCase { getOplsByLevelUseCase(levelId) }
                }.onSuccess { oplList ->
                    setState {
                        copy(
                            oplList = oplList,
                            filteredOplList = applyFilter(oplList, currentFilter),
                            isLoading = false,
                            message = EMPTY,
                        )
                    }
                }.onFailure { exception ->
                    setState {
                        copy(
                            oplList = emptyList(),
                            filteredOplList = emptyList(),
                            isLoading = false,
                            message = "Error al cargar OPLs: ${exception.localizedMessage}",
                        )
                    }
                }
            }
        }

        private fun handleUpdateOplList() {
            viewModelScope.launch {
                setState { copy(isLoading = true, message = "Actualizando lista...") }
                // Recargar desde el nivel seleccionado actual
                val currentSelectedLevel = getState().selectedLevelList.values.lastOrNull()
                if (currentSelectedLevel?.isNotEmpty() == true) {
                    handleGetOplsByLevel(currentSelectedLevel)
                } else {
                    setState {
                        copy(
                            oplList = emptyList(),
                            filteredOplList = emptyList(),
                            isLoading = false,
                            message = "Selecciona un nivel para ver OPLs",
                        )
                    }
                }
            }
        }

        private fun handleFilterOpl(filter: String) {
            viewModelScope.launch {
                val filteredList = applyFilter(getState().oplList, filter)
                setState {
                    copy(
                        filteredOplList = filteredList,
                        currentFilter = filter,
                    )
                }
            }
        }

        private fun applyFilter(
            oplList: List<Opl>,
            filter: String,
        ): List<Opl> {
            return when (filter.lowercase()) {
                "todo", "all", "" -> oplList
                "seguridad" -> oplList.filter { it.title.contains("Seguridad", ignoreCase = true) }
                "mantenimiento" -> oplList.filter { it.title.contains("Mantenimiento", ignoreCase = true) }
                "calidad" -> oplList.filter { it.title.contains("Calidad", ignoreCase = true) }
                else ->
                    oplList.filter {
                        it.title.contains(filter, ignoreCase = true) ||
                            it.objetive.contains(filter, ignoreCase = true)
                    }
            }
        }
    }
