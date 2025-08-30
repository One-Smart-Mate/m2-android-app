package com.ih.osm.ui.pages.procedimiento.action

sealed class ProcedimientoListAction {
    data object UpdateList : ProcedimientoListAction()

    data class SetLevel(val id: String, val key: Int) : ProcedimientoListAction()
}
