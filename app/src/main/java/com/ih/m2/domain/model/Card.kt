package com.ih.m2.domain.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.gson.annotations.SerializedName
import com.ih.m2.R
import com.ih.m2.ui.utils.ALL_OPEN_CARDS
import com.ih.m2.ui.utils.ASSIGNED_CARDS
import com.ih.m2.ui.utils.CLOSED_CARDS
import com.ih.m2.ui.utils.EMPTY
import com.ih.m2.ui.utils.EXPIRED_CARDS
import com.ih.m2.ui.utils.MY_OPEN_CARDS
import com.ih.m2.ui.utils.STATUS_A
import com.ih.m2.ui.utils.STATUS_C
import com.ih.m2.ui.utils.STATUS_P
import com.ih.m2.ui.utils.STATUS_R
import com.ih.m2.ui.utils.STATUS_V
import com.ih.m2.ui.utils.UNASSIGNED_CARDS

data class Card(
    val id: String,
    val siteCardID: Long,
    val siteID: String?,
    val siteCode: String?,
    val cardUUID: String,
    val cardTypeColor: String,
    val feasibility: String?,
    val effect: String?,
    val status: String,
    val cardCreationDate: String,
    val cardDueDate: String,
    val areaID: Long,
    val areaName: String,
    val level: Long,
    val superiorID: String?,
    val priorityID: String?,
    val priorityCode: String?,
    val priorityDescription: String,
    val cardTypeMethodology: String,
    val cardTypeMethodologyName: String,
    val cardTypeValue: String,
    val cardTypeID: String?,
    val cardTypeName: String,
    val preclassifierId: String,
    val preclassifierCode: String,
    val preclassifierDescription: String,
    val creatorID: String?,
    val creatorName: String,
    val responsableID: String?,
    val responsableName: String,
    val mechanicID: String?,
    val mechanicName: String?,
    val userProvisionalSolutionID: String?,
    val userProvisionalSolutionName: String?,
    val userAppProvisionalSolutionID: String?,
    val userAppProvisionalSolutionName: String?,
    val userDefinitiveSolutionID: String?,
    val userDefinitiveSolutionName: String?,
    val userAppDefinitiveSolutionID: String?,
    val userAppDefinitiveSolutionName: String?,
    val managerID: String?,
    val managerName: String,
    val cardManagerCloseDate: String?,
    val commentsManagerAtCardClose: String?,
    val commentsAtCardCreation: String?,
    val cardProvisionalSolutionDate: String?,
    val commentsAtCardProvisionalSolution: String?,
    val cardDefinitiveSolutionDate: String?,
    val commentsAtCardDefinitiveSolution: String?,
    @SerializedName("evidenceAucr")
    val evidenceAudioCreation: Int,
    @SerializedName("evidenceVicr")
    val evidenceVideoCreation: Int,
    @SerializedName("evidenceImcr")
    val evidenceImageCreation: Int,
    @SerializedName("evidenceAucl")
    val evidenceAudioClose: Int,
    @SerializedName("evidenceVicl")
    val evidenceVideoClose: Int,
    @SerializedName("evidenceImcl")
    val evidenceImageClose: Int,
    val createdAt: String,
    val updatedAt: String?,
    val deletedAt: String?,
    val evidences: List<Evidence>? = emptyList()
) {
    companion object {
        fun mock(): Card {
            return Card(
                "179",
                1,
                "1",
                "AAAAAA",
                "f6504367-46bd-4dc2-b5c6-7bd6e6b12f8c",
                "0000FF",
                "Alto",
                "Bajo",
                "A",
                "2021-10-28T20:31:35.000Z",
                "2021-11-04",
                14,
                "Envasado",
                2,
                "22",
                "2",
                "7d",
                "7 dias",
                "M",
                "Mantenimiento",
                "safe",
                "1",
                "Anomalias",
                "7",
                "G",
                "Dificultad de inspeccion",
                "1",
                "fausto",
                "1",
                "fausto",
                "2",
                "Juan Mecanico",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "1",
                "fausto",
                "2021-11-07T21:38:21.000Z",
                "Se cierra por descartarse la necesidad (prueba)",
                "",
                "",
                "",
                "",
                "",
                0,
                0,
                0,
                0,
                0,
                0,
                "2021-10-29T03:31:35.000Z",
                "2021-11-08T04:38:21.000Z",
                "",
                emptyList()
            )
        }
    }
}

@Composable
fun Card.getStatus(): String {
    return when (status) {
        STATUS_P, STATUS_A, STATUS_V -> stringResource(id = R.string.open)
        STATUS_R, STATUS_C -> stringResource(id = R.string.open)
        else -> stringResource(id = R.string.open)
    }
}


fun List<Card>.filterByStatus(filter: String, userId: String): List<Card> {
    return when(filter) {
        ALL_OPEN_CARDS -> {
            this.filter { it.status == STATUS_A || it.status == STATUS_P || it.status == STATUS_V }
        }
        MY_OPEN_CARDS -> {
            this.filter { (it.status == STATUS_A || it.status == STATUS_P || it.status == STATUS_V) && it.creatorID == userId }
        }
        ASSIGNED_CARDS -> {
            this.filter { (it.status == STATUS_A || it.status == STATUS_P || it.status == STATUS_V) && it.mechanicID == userId }
        }
        UNASSIGNED_CARDS -> {
            this.filter { (it.status == STATUS_A || it.status == STATUS_P || it.status == STATUS_V) && (it.mechanicID == null || it.mechanicID == EMPTY) }
        }
        EXPIRED_CARDS -> {
            this.filter { it.status == STATUS_V }
        }
        CLOSED_CARDS -> {
            this.filter { it.status == STATUS_R || it.status == STATUS_C }
        }
        else -> this
    }
}

