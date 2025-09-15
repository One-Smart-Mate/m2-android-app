package com.ih.osm.ui.pages.cilt.action

import android.net.Uri

sealed class CiltAction {
    data object GetCilts : CiltAction()

    data class StartExecution(
        val executionId: Int,
    ) : CiltAction()

    data class StopExecution(
        val executionId: Int,
    ) : CiltAction()

    data class SetParameterFound(
        val value: String,
    ) : CiltAction()

    data class SetFinalParameter(
        val value: String,
    ) : CiltAction()

    data class SetParameterOk(
        val isOk: Boolean,
    ) : CiltAction()

    data class AddEvidenceBefore(
        val executionId: Int,
        val uri: Uri,
    ) : CiltAction()

    data class AddEvidenceAfter(
        val executionId: Int,
        val uri: Uri,
    ) : CiltAction()

    data class RemoveEvidenceBefore(
        val url: String,
    ) : CiltAction()

    data class RemoveEvidenceAfter(
        val url: String,
    ) : CiltAction()

    data class SetEvidenceAtCreation(
        val value: Boolean,
    ) : CiltAction()

    data class SetEvidenceAtFinal(
        val value: Boolean,
    ) : CiltAction()

    data class GetOplById(
        val id: String,
    ) : CiltAction()

    data class GetRemediationOplById(
        val id: String,
    ) : CiltAction()

    data object CleanMessage : CiltAction()

    data object SetStarted : CiltAction()

    data object SetFinished : CiltAction()
}
