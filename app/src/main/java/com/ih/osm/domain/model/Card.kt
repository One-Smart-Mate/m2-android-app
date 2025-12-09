package com.ih.osm.domain.model

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.gson.annotations.SerializedName
import com.ih.osm.BuildConfig
import com.ih.osm.R
import com.ih.osm.data.database.entities.card.CardEntity
import com.ih.osm.data.model.CreateCardRequest
import com.ih.osm.data.model.CreateEvidenceRequest
import com.ih.osm.ui.extensions.ISO_FORMAT
import com.ih.osm.ui.extensions.NORMAL_FORMAT
import com.ih.osm.ui.extensions.YYYY_MM_DD_HH_MM_SS_UTC
import com.ih.osm.ui.extensions.defaultIfNull
import com.ih.osm.ui.extensions.toFormatDate
import com.ih.osm.ui.utils.ALL_OPEN_CARDS
import com.ih.osm.ui.utils.ANDROID_SO
import com.ih.osm.ui.utils.ASSIGNED_CARDS
import com.ih.osm.ui.utils.CLOSED_CARDS
import com.ih.osm.ui.utils.EMPTY
import com.ih.osm.ui.utils.EXPIRED_CARDS
import com.ih.osm.ui.utils.MY_OPEN_CARDS
import com.ih.osm.ui.utils.STATUS_A
import com.ih.osm.ui.utils.STATUS_C
import com.ih.osm.ui.utils.STATUS_P
import com.ih.osm.ui.utils.STATUS_R
import com.ih.osm.ui.utils.STATUS_V
import com.ih.osm.ui.utils.STORED_LOCAL
import com.ih.osm.ui.utils.STORED_REMOTE
import com.ih.osm.ui.utils.UNASSIGNED_CARDS
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class Card(
    val id: String,
    val siteCardId: Long,
    @SerializedName("siteId")
    val siteId: String?,
    val siteCode: String?,
    @SerializedName("cardUUID")
    val uuid: String,
    val cardTypeColor: String,
    val feasibility: String?,
    val effect: String?,
    val status: String,
    @SerializedName("cardCreationDate")
    val creationDate: String,
    @SerializedName("cardDueDate")
    val dueDate: String,
    @SerializedName("areaId")
    val areaId: Long,
    val areaName: String,
    val level: Long,
    val levelName: String?,
    @SerializedName("superiorId")
    val superiorId: String?,
    @SerializedName("priorityId")
    val priorityId: String?,
    val priorityCode: String?,
    val priorityDescription: String?,
    val cardTypeMethodology: String?,
    val cardTypeMethodologyName: String?,
    val cardTypeValue: String?,
    @SerializedName("cardTypeId")
    val cardTypeId: String?,
    val cardTypeName: String?,
    val preclassifierId: String,
    val preclassifierCode: String,
    val preclassifierDescription: String,
    @SerializedName("creatorId")
    val creatorId: String?,
    val creatorName: String,
    @SerializedName("responsableId")
    val responsableId: String?,
    val responsableName: String?,
    @SerializedName("mechanicId")
    val mechanicId: String?,
    val mechanicName: String?,
    @SerializedName("userProvisionalSolutionId")
    val userProvisionalSolutionId: String?,
    val userProvisionalSolutionName: String?,
    @SerializedName("userAppProvisionalSolutionId")
    val userAppProvisionalSolutionId: String?,
    val userAppProvisionalSolutionName: String?,
    @SerializedName("userDefinitiveSolutionId")
    val userDefinitiveSolutionId: String?,
    val userDefinitiveSolutionName: String?,
    @SerializedName("userAppDefinitiveSolutionId")
    val userAppDefinitiveSolutionId: String?,
    val userAppDefinitiveSolutionName: String?,
    @SerializedName("managerId")
    val managerId: String?,
    val managerName: String?,
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
    val evidences: List<Evidence>? = emptyList(),
    val stored: String? = STORED_REMOTE,
    val creationDateFormatted: String? = null,
    val cardLocation: String,
    val hasLocalSolutions: Boolean,
) {
    companion object {
        fun mock(): Card =
            Card(
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
                "levelname",
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
                1,
                1,
                1,
                1,
                1,
                1,
                "2021-10-29T03:31:35.000Z",
                "2021-11-08T04:38:21.000Z",
                "",
                emptyList(),
                STORED_LOCAL,
                cardLocation = "Procesos/Mixer 1 /Bomba 1",
                hasLocalSolutions = true,
            )

        fun fromCreateCard(
            areaId: Long,
            level: Long,
            priorityId: String,
            cardTypeValue: String,
            cardTypeId: String,
            preclassifierId: String,
            comment: String,
            hasImages: Int,
            hasVideos: Int,
            hasAudios: Int,
            evidences: List<Evidence>,
            uuid: String,
        ): Card =
            Card(
                id = EMPTY,
                siteCardId = 0,
                siteId = EMPTY,
                siteCode = EMPTY,
                uuid = uuid,
                cardTypeColor = EMPTY,
                feasibility = EMPTY,
                effect = EMPTY,
                status = STATUS_A,
                creationDate = Date().YYYY_MM_DD_HH_MM_SS_UTC,
                dueDate = EMPTY,
                areaId = areaId,
                areaName = EMPTY,
                level = level,
                levelName = EMPTY,
                superiorId = EMPTY,
                priorityId = priorityId,
                priorityCode = EMPTY,
                priorityDescription = EMPTY,
                cardTypeMethodology = EMPTY,
                cardTypeMethodologyName = EMPTY,
                cardTypeValue = cardTypeValue,
                cardTypeId = cardTypeId,
                cardTypeName = EMPTY,
                preclassifierId = preclassifierId,
                preclassifierCode = EMPTY,
                preclassifierDescription = EMPTY,
                creatorId = EMPTY,
                creatorName = EMPTY,
                responsableId = EMPTY,
                responsableName = EMPTY,
                mechanicId = EMPTY,
                mechanicName = EMPTY,
                userProvisionalSolutionId = EMPTY,
                userProvisionalSolutionName = EMPTY,
                userAppProvisionalSolutionId = EMPTY,
                userAppProvisionalSolutionName = EMPTY,
                userDefinitiveSolutionId = EMPTY,
                userDefinitiveSolutionName = EMPTY,
                userAppDefinitiveSolutionId = EMPTY,
                userAppDefinitiveSolutionName = EMPTY,
                managerId = EMPTY,
                managerName = EMPTY,
                cardManagerCloseDate = EMPTY,
                commentsManagerAtCardClose = EMPTY,
                commentsAtCardCreation = comment,
                cardProvisionalSolutionDate = EMPTY,
                commentsAtCardProvisionalSolution = EMPTY,
                cardDefinitiveSolutionDate = EMPTY,
                commentsAtCardDefinitiveSolution = EMPTY,
                evidenceAudioCreation = hasAudios,
                evidenceVideoCreation = hasVideos,
                evidenceImageCreation = hasImages,
                evidenceAudioClose = 0,
                evidenceVideoClose = 0,
                evidenceImageClose = 0,
                createdAt = EMPTY,
                updatedAt = EMPTY,
                deletedAt = EMPTY,
                evidences = evidences,
                stored = STORED_LOCAL,
                cardLocation = EMPTY,
                hasLocalSolutions = false,
            )
    }
}

fun Card.priorityValue(): String =
    if (
        this.priorityId.isNullOrBlank().not() &&
        this.priorityCode.isNullOrBlank().not() &&
        this.priorityDescription.isNullOrBlank().not()
    ) {
        "${this.priorityCode} - ${this.priorityDescription}"
    } else {
        EMPTY
    }

fun Card.preclassifierValue(): String =
    if (
        this.preclassifierId.isBlank().not() &&
        this.preclassifierCode.isBlank().not() &&
        this.preclassifierDescription.isBlank().not()
    ) {
        "${this.preclassifierCode} - ${this.preclassifierDescription}"
    } else {
        EMPTY
    }

@Composable
fun Card.getStatus(): String =
    when (status) {
        STATUS_P, STATUS_A, STATUS_V -> stringResource(id = R.string.open)
        STATUS_R -> stringResource(id = R.string.closed)
        STATUS_C -> stringResource(id = R.string.canceled)
        else -> stringResource(id = R.string.open)
    }

fun String.toCardFilter(context: Context): String =
    when (this) {
        context.getString(R.string.all_open_cards) -> ALL_OPEN_CARDS
        context.getString(R.string.my_open_cards) -> MY_OPEN_CARDS
        context.getString(R.string.assigned_cards) -> ASSIGNED_CARDS
        context.getString(R.string.unassigned_cards) -> UNASSIGNED_CARDS
        context.getString(R.string.expired_cards) -> EXPIRED_CARDS
        context.getString(R.string.closed_cards) -> CLOSED_CARDS
        else -> EMPTY
    }

fun List<Card>.filterByStatus(
    filter: String,
    userId: String,
): List<Card> =
    when (filter) {
        ALL_OPEN_CARDS -> {
            this.filter {
                it.status == STATUS_A ||
                    it.status == STATUS_P ||
                    it.status == STATUS_V
            }
        }

        MY_OPEN_CARDS -> {
            this.filter {
                (
                    it.status == STATUS_A ||
                        it.status == STATUS_P ||
                        it.status == STATUS_V
                ) &&
                    it.creatorId == userId
            }
        }

        ASSIGNED_CARDS -> {
            this.filter {
                (
                    it.status == STATUS_A
                ) &&
                    it.mechanicId == userId
            }
        }

        UNASSIGNED_CARDS -> {
            this.filter {
                (
                    it.mechanicId == null ||
                        it.mechanicId == EMPTY
                )
            }
        }

        EXPIRED_CARDS -> {
            this.filter {
                try {
                    if (it.dueDate.isNullOrBlank()) {
                        false
                    } else {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val dueDate = dateFormat.parse(it.dueDate)
                        val today = Calendar.getInstance().time

                        dueDate != null && dueDate.before(today)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
        }

        CLOSED_CARDS -> {
            this.filter { it.status == STATUS_R || it.status == STATUS_C }
        }

        else -> this
    }

fun Card.isClosed() = this.status == STATUS_R || this.cardManagerCloseDate.isNullOrEmpty().not()

fun Card.toEntity(): CardEntity =
    CardEntity(
        cardId = this.id,
        siteCardId = this.siteCardId,
        siteId = this.siteId,
        siteCode = this.siteCode,
        uuid = this.uuid,
        cardTypeColor = this.cardTypeColor,
        feasibility = this.feasibility,
        effect = this.effect,
        status = this.status,
        creationDate = this.creationDate,
        dueDate = this.dueDate,
        areaId = this.areaId,
        areaName = this.areaName,
        level = this.level,
        levelName = this.levelName,
        superiorId = this.superiorId,
        priorityId = this.priorityId,
        priorityCode = this.priorityCode,
        priorityDescription = this.priorityDescription,
        cardTypeMethodology = this.cardTypeMethodology,
        cardTypeMethodologyName = this.cardTypeMethodologyName,
        cardTypeValue = this.cardTypeValue,
        cardTypeId = this.cardTypeId,
        cardTypeName = this.cardTypeName,
        preclassifierId = this.preclassifierId,
        preclassifierCode = this.preclassifierCode,
        preclassifierDescription = this.preclassifierDescription,
        creatorId = this.creatorId,
        creatorName = this.creatorName,
        responsableId = this.responsableId,
        responsableName = this.responsableName,
        mechanicId = this.mechanicId,
        mechanicName = this.mechanicName,
        userProvisionalSolutionId = this.userProvisionalSolutionId,
        userProvisionalSolutionName = this.userProvisionalSolutionName,
        userAppProvisionalSolutionId = this.userAppProvisionalSolutionId,
        userAppProvisionalSolutionName = this.userAppProvisionalSolutionName,
        userDefinitiveSolutionId = this.userDefinitiveSolutionId,
        userDefinitiveSolutionName = this.userDefinitiveSolutionName,
        userAppDefinitiveSolutionId = this.userAppDefinitiveSolutionId,
        userAppDefinitiveSolutionName = this.userAppDefinitiveSolutionName,
        managerId = this.managerId,
        managerName = this.managerName,
        cardManagerCloseDate = this.cardManagerCloseDate,
        commentsManagerAtCardClose = this.commentsManagerAtCardClose,
        commentsAtCardCreation = this.commentsAtCardCreation,
        cardProvisionalSolutionDate = this.cardProvisionalSolutionDate,
        commentsAtCardProvisionalSolution = this.commentsAtCardProvisionalSolution,
        cardDefinitiveSolutionDate = this.cardDefinitiveSolutionDate,
        commentsAtCardDefinitiveSolution = this.commentsAtCardDefinitiveSolution,
        evidenceAudioCreation = this.evidenceAudioCreation,
        evidenceVideoCreation = this.evidenceVideoCreation,
        evidenceImageCreation = this.evidenceImageCreation,
        evidenceAudioClose = this.evidenceAudioClose,
        evidenceVideoClose = this.evidenceVideoClose,
        evidenceImageClose = this.evidenceImageClose,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        deletedAt = this.deletedAt,
        stored = this.stored ?: STORED_REMOTE,
        cardLocation = this.cardLocation,
    )

fun List<Card>.toLocalCards() = this.filter { it.stored == STORED_LOCAL || it.hasLocalSolutions }

fun Card.toCardRequest(evidences: List<CreateEvidenceRequest>): CreateCardRequest =
    CreateCardRequest(
        siteId = this.siteId?.toInt().defaultIfNull(0),
        cardUUID = this.uuid,
        cardCreationDate = this.creationDate,
        nodeId = this.areaId.toInt(),
        priorityId =
            if (this.priorityId.isNullOrBlank().not()) {
                this.priorityId
                    ?.toInt()
                    .defaultIfNull(0)
            } else {
                0
            },
        cardTypeValue = this.cardTypeValue?.lowercase().orEmpty(),
        cardTypeId = this.cardTypeId?.toInt().defaultIfNull(0),
        preclassifierId = this.preclassifierId.toInt(),
        creatorId = this.creatorId?.toInt().defaultIfNull(0),
        comments = this.commentsAtCardCreation.orEmpty(),
        evidences = evidences,
        appSo = ANDROID_SO,
        appVersion = BuildConfig.VERSION_NAME,
    )

fun Card.getCreationDate(): String = creationDateFormatted.defaultIfNull(creationDate)

fun Card.validateProvisionalDate(): String =
    if (this.stored == STORED_REMOTE) {
        cardProvisionalSolutionDate.toFormatDate(ISO_FORMAT)
    } else {
        cardProvisionalSolutionDate.toFormatDate(NORMAL_FORMAT)
    }

fun Card.validateCloseDate(): String =
    if (this.stored == STORED_REMOTE) {
        cardDefinitiveSolutionDate.toFormatDate(ISO_FORMAT)
    } else {
        cardDefinitiveSolutionDate.toFormatDate(NORMAL_FORMAT)
    }

fun Card.cardTitle(): String = this.cardTypeName.orEmpty()

fun Card.cardSiteTitle(): String =
    if (this.isLocalCard()) {
        EMPTY
    } else {
        "#${this.siteCardId}"
    }

fun Card.enableProvisionalSolution(): Boolean =
    this.userProvisionalSolutionId.isNullOrEmpty() ||
        this.userProvisionalSolutionName.isNullOrBlank() ||
        this.userAppProvisionalSolutionId.isNullOrBlank() ||
        this.userAppProvisionalSolutionName.isNullOrBlank()

fun Card.enableDefinitiveSolution(): Boolean =
    this.userDefinitiveSolutionId.isNullOrEmpty() ||
        this.userDefinitiveSolutionName.isNullOrBlank() ||
        this.userAppDefinitiveSolutionId.isNullOrBlank() ||
        this.userAppDefinitiveSolutionName.isNullOrBlank()

fun Card.enableAssignMechanic(): Boolean = this.mechanicId.isNullOrEmpty() || this.mechanicName.isNullOrEmpty()

fun Card.isLocalCard() = stored == STORED_LOCAL

fun Card.isRemoteCard() = stored == STORED_REMOTE
