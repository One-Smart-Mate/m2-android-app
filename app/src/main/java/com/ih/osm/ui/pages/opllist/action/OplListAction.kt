package com.ih.osm.ui.pages.opllist.action

sealed class OplListAction {
    data class Detail(val id: Int) : OplListAction()

    data object Create : OplListAction()

    data class Filters(val filter: String) : OplListAction()

    data object UpdateList : OplListAction()

    data class SetLevel(val id: String, val key: Int) : OplListAction()
}
