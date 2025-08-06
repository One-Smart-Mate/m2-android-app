package com.ih.osm.ui.pages.opl.action

sealed class OplListAction {
    data object UpdateList : OplListAction()

    data class SetLevel(val id: String, val key: Int) : OplListAction()
}
