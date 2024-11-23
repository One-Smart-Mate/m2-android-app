package com.ih.osm.ui.pages.createcard.action

import android.net.Uri
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.EvidenceType

sealed class CreateCardAction {
    data class SetCardType(val id: String) : CreateCardAction()
    data class SetPreclassifier(val id: String) : CreateCardAction()
    data class SetPriority(val id: String) : CreateCardAction()
    data class SetLevel(val id: String, val key: Int) : CreateCardAction()
    data class SetComment(val comment: String) : CreateCardAction()
    data class AddEvidence(val uri: Uri, val type: EvidenceType) : CreateCardAction()
    data class DeleteEvidence(val evidence: Evidence) : CreateCardAction()
    data object Save: CreateCardAction()
}