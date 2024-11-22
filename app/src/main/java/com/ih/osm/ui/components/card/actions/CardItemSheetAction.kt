package com.ih.osm.ui.components.card.actions


sealed class CardItemSheetAction {
    data object ProvisionalSolution : CardItemSheetAction()
    data object DefinitiveSolution : CardItemSheetAction()
    data object AssignMechanic : CardItemSheetAction()
}


fun CardItemSheetAction.toActionString(): String {
    return when (this) {
        is CardItemSheetAction.ProvisionalSolution -> "provisionalSolution"
        is CardItemSheetAction.DefinitiveSolution -> "definitiveSolution"
        is CardItemSheetAction.AssignMechanic -> "assignMechanic"
    }
}

// Define a function to convert a String back to CardItemSheetAction
fun String.toCardItemSheetAction(): CardItemSheetAction {
    return when (this) {
        "provisionalSolution" -> CardItemSheetAction.ProvisionalSolution
        "definitiveSolution" -> CardItemSheetAction.DefinitiveSolution
        "assignMechanic" -> CardItemSheetAction.AssignMechanic
        else -> throw IllegalArgumentException("Unknown action")
    }
}