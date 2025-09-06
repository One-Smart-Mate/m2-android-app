package com.ih.osm.ui.pages.procedure.action

sealed class ProcedureListAction {
    data object UpdateList : ProcedureListAction()

    data class SetLevel(val id: String, val key: Int) : ProcedureListAction()
}
