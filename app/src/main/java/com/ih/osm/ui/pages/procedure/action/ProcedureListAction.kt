package com.ih.osm.ui.pages.procedure.action

import com.ih.osm.domain.model.CiltProcedureData

sealed class ProcedureListAction {
    data object UpdateList : ProcedureListAction()

    data class SetLevel(
        val id: String,
        val key: Int,
    ) : ProcedureListAction()

    data class CreateExecution(
        val sequence: CiltProcedureData.Sequence,
        val positionId: Int,
        val levelId: String,
    ) : ProcedureListAction()

    data object ClearAllExecutionState : ProcedureListAction()
}
