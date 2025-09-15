package com.ih.osm.ui.pages.cardaction.action

import android.net.Uri
import com.ih.osm.domain.model.Employee
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.EvidenceType

sealed class CardAction {
    data class SearchEmployee(
        val query: String,
    ) : CardAction()

    data class SetEmployee(
        val employee: Employee,
    ) : CardAction()

    data class SetComment(
        val comment: String,
    ) : CardAction()

    data class AddEvidence(
        val uri: Uri,
        val type: EvidenceType,
    ) : CardAction()

    data class DeleteEvidence(
        val evidence: Evidence,
    ) : CardAction()

    data object Save : CardAction()
}
