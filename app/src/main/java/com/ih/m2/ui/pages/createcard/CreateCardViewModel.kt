package com.ih.m2.ui.pages.createcard

import android.net.Uri
import android.util.Log
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.ih.m2.domain.model.Evidence
import com.ih.m2.domain.model.EvidenceType
import com.ih.m2.domain.model.NodeCardItem
import com.ih.m2.domain.model.isMaintenanceCardType
import com.ih.m2.domain.model.toNodeItemCard
import com.ih.m2.domain.model.toNodeItemList
import com.ih.m2.domain.usecase.cardtype.GetCardTypesUseCase
import com.ih.m2.domain.usecase.preclassifier.GetPreclassifiersUseCase
import com.ih.m2.domain.usecase.priority.GetPrioritiesUseCase
import com.ih.m2.ui.extensions.defaultIfNull
import com.ih.m2.ui.utils.EMPTY
import com.ih.m2.ui.utils.STATUS_A
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
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
        val message: String = EMPTY,
        val evidences: List<Evidence> = emptyList(),
        val cardId: String = UUID.randomUUID().toString()
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
        data object SaveCard : Action()
        data class OnAddEvidence(val uri: Uri, val type: EvidenceType) : Action()
        data class OnDeleteEvidence(val evidence: Evidence): Action()
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
            is Action.OnAddEvidence -> handleOnAddEvidence(action.uri, action.type)
            is Action.OnDeleteEvidence -> handleOnDeleteEvidence(action.evidence)
        }
    }

    private fun handleOnAddEvidence(uri: Uri, type: EvidenceType) {
        viewModelScope.launch {
            val state = stateFlow.first()
            val list = state.evidences.toMutableList()
            list.add(
                Evidence(
                    id = UUID.randomUUID().toString(),
                    cardId = state.cardId,
                    siteId = EMPTY,
                    url = uri.toString(),
                    type = type.name,
                    status = STATUS_A,
                    createdAt = null,
                    updatedAt = null,
                    deletedAt = null
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
                setState { copy(isSecureCard = true) }
            } else {
                val levelList = getLevelById("0", 0)
                setState { copy(levelList = levelList) }
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

            //Valores que si se llenan de la create card/
            //alinear valores
            /*
            //Mantenimiento
                siteCardId = " es el ID de las tarjetas de ese sitio ->
                siteId = site id del usuario
                carUUUID = autogenerado
                feasibility = Valores Alto y Bajo es un checkbox que diga alto y bajo pero cuando se crea se manda null
                effect = Valores Alto y Bajo es un checkbox pero cuando se crea se manda en null
                cardCreationDate = fecha en la que se crea la tarjeta
                areaId = el id del ultimo nodo
                priorityId = el id priority
                cardTypeValue = safe o unsafe
                cardTypeId = id del card type
                preclassifierId = preclassfier id
                creatorId = usuario que la creo
                responsableId =  este se llena hasta el mantenimiento entonces se manda en 0
                mechanicId = este se llena hasta el mantenimiento se madna en 0
                userProvisionalSolutionId = este valor se llena cuando se hace el user provisional solution  quien hizo la reparacion-> no se llena
                userAppProvisionalSolutionId =  este es el mismo de arriba pero es de la app -> no se llena
                userDefinitiveSolutionId = mismo que el de arriba pero el en definitive
                userAppDefinitiveSolutionId =
                managerId  = es el id del responsable del ultimo nivel del level =,
                solucion provisional no lleva evidencia ->
                agregar campos a la solucion provisional de evidencia
                evidenceAucr son 1 o 0 para saber si tiene o no para decirle a caleb que tiene que hacer y consultar con estos




                existing card zone -> al crear en la app se visualiza con status A, P y V solo se visualiza si la puede crear aun asi
            */


            //Provisional solution
            /*
            *
            *user provisional solution id se llena en el combo
            * y el app es el que se llena en la app el usuario logeado
            *
            * el edit de la card es we solo crud web
            * */

//            val createCardUseCase = CreateCardRequest(
            //siteCardId = ""
//                siteId = user.siteId,
//                cardUUID = UUID.randomUUID().toString(),
//                feasibility = "", //Este valor se llena en el mantenimiento de la card
//                effect = "", //revisar este valor
//                cardCreationDate = Date().YYYY_MM_DD_HH_MM_SS,
//                areaId = 0,
//                priorityId = 0,
//                cardTypeValue = "Sage",
//                cardTypeId = cardType.id,
//                preclassifierId = preclassifier.id,
//                creatorId = user.userId,
//                responsableId = 0, //revisar este valor
//                mechanicId = 0, //revsiar este valor
//                userProvisionalSolutionId = 0, //revisar este valor
//                userAppProvisionalSolutionId = 0, //revisar estae valor
//                userDefinitiveSolutionId = 0, //revisar esste valr
//                userAppDefinitiveSolutionId = 0, //revisar este valor
//                managerId = 0, //revisar este valor
//                commentsAtCardCreation = "",
//                evidenceAucl = 0, //revisar estos es el valor maximo que puedes subir
//                evidenceAucr = 0,
//                evidenceImcl = 0,
//                evidenceImcr = 0,
//                evidenceVicl = 0,
//                evidenceVicr = 0
//            )
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<CreateCardViewModel, UiState> {
        override fun create(state: UiState): CreateCardViewModel
    }

    companion object :
        MavericksViewModelFactory<CreateCardViewModel, UiState> by hiltMavericksViewModelFactory()
}
