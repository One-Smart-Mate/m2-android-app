package com.ih.osm.data.database.entities.card

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.Evidence
import com.ih.osm.ui.extensions.ISO_FORMAT
import com.ih.osm.ui.extensions.NORMAL_FORMAT
import com.ih.osm.ui.extensions.toFormatDate
import com.ih.osm.ui.utils.STORED_REMOTE


@Entity(tableName = "card_table")
data class CardEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "site_card_id")
    val siteCardId: Long,
    @ColumnInfo(name = "site_id")
    val siteId: String?,
    @ColumnInfo(name = "site_code")
    val siteCode: String?,
    @ColumnInfo(name = "card_uuid")
    val uuid: String,
    @ColumnInfo(name = "card_type_color")
    val cardTypeColor: String,
    @ColumnInfo(name = "feasibility")
    val feasibility: String?,
    @ColumnInfo(name = "effect")
    val effect: String?,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "card_creation_date")
    val creationDate: String,
    @ColumnInfo(name = "card_due_date")
    val dueDate: String,
    @ColumnInfo(name = "area_id")
    val areaId: Long,
    @ColumnInfo(name = "area_name")
    val areaName: String,
    @ColumnInfo(name = "level")
    val level: Long,
    @ColumnInfo(name = "level_name")
    val levelName: String?,
    @ColumnInfo(name = "superior_id")
    val superiorId: String?,
    @ColumnInfo(name = "priority_id")
    val priorityId: String?,
    @ColumnInfo(name = "priority_code")
    val priorityCode: String?,
    @ColumnInfo(name = "priority_description")
    val priorityDescription: String?,
    @ColumnInfo(name = "card_methodology")
    val cardTypeMethodology: String?,
    @ColumnInfo(name = "card_methodology_name")
    val cardTypeMethodologyName: String?,
    @ColumnInfo(name = "card_type_value")
    val cardTypeValue: String?,
    @ColumnInfo(name = "card_type_id")
    val cardTypeId: String?,
    @ColumnInfo(name = "card_type_name")
    val cardTypeName: String?,
    @ColumnInfo(name = "preclassifier_id")
    val preclassifierId: String,
    @ColumnInfo(name = "preclassifier_code")
    val preclassifierCode: String,
    @ColumnInfo(name = "preclassifier_description")
    val preclassifierDescription: String,
    @ColumnInfo(name = "creator_id")
    val creatorId: String?,
    @ColumnInfo(name = "creator_name")
    val creatorName: String,
    @ColumnInfo(name = "responsable_id")
    val responsableId: String?,
    @ColumnInfo(name = "responsable_name")
    val responsableName: String?,
    @ColumnInfo(name = "mechanic_id")
    val mechanicId: String?,
    @ColumnInfo(name = "mechanic_name")
    val mechanicName: String?,
    @ColumnInfo(name = "user_provisional_solution_id")
    val userProvisionalSolutionId: String?,
    @ColumnInfo(name = "user_provisional_solution_name")
    val userProvisionalSolutionName: String?,
    @ColumnInfo(name = "user_app_provisional_solution_id")
    val userAppProvisionalSolutionId: String?,
    @ColumnInfo(name = "user_app_provisional_solution_name")
    val userAppProvisionalSolutionName: String?,
    @ColumnInfo(name = "user_definitive_solution_id")
    val userDefinitiveSolutionId: String?,
    @ColumnInfo(name = "user_definitive_solution_name")
    val userDefinitiveSolutionName: String?,
    @ColumnInfo(name = "user_app_definitive_solution_id")
    val userAppDefinitiveSolutionId: String?,
    @ColumnInfo(name = "user_app_definitive_solution_name")
    val userAppDefinitiveSolutionName: String?,
    @ColumnInfo(name = "manager_id")
    val managerId: String?,
    @ColumnInfo(name = "manager_name")
    val managerName: String?,
    @ColumnInfo(name = "card_manager_close_date")
    val cardManagerCloseDate: String?,
    @ColumnInfo(name = "comments_manager_at_card_close")
    val commentsManagerAtCardClose: String?,
    @ColumnInfo(name = "comments_at_card_creation")
    val commentsAtCardCreation: String?,
    @ColumnInfo(name = "card_provisional_solution_date")
    val cardProvisionalSolutionDate: String?,
    @ColumnInfo(name = "comments_at_card_provisional_solution")
    val commentsAtCardProvisionalSolution: String?,
    @ColumnInfo(name = "card_definitive_solution_date")
    val cardDefinitiveSolutionDate: String?,
    @ColumnInfo(name = "comments_at_card_definitive_solution")
    val commentsAtCardDefinitiveSolution: String?,
    @ColumnInfo(name = "evidence_audio_creation")
    @SerializedName("evidenceAucr")
    val evidenceAudioCreation: Int,
    @ColumnInfo(name = "evidence_video_creation")
    @SerializedName("evidenceVicr")
    val evidenceVideoCreation: Int,
    @ColumnInfo(name = "evidence_image_creation")
    @SerializedName("evidenceImcr")
    val evidenceImageCreation: Int,
    @ColumnInfo(name = "evidence_audio_close")
    @SerializedName("evidenceAucl")
    val evidenceAudioClose: Int,
    @ColumnInfo(name = "evidence_video_close")
    @SerializedName("evidenceVicl")
    val evidenceVideoClose: Int,
    @ColumnInfo(name = "evidence_image_close")
    @SerializedName("evidenceImcl")
    val evidenceImageClose: Int,
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String?,
    @ColumnInfo(name = "deleted_at")
    val deletedAt: String?,
    @ColumnInfo(name = "stored")
    val stored: String?
)

fun CardEntity.validateDate(): String {
    return if (this.stored == STORED_REMOTE) {
        creationDate.toFormatDate(ISO_FORMAT)
    } else {
        creationDate.toFormatDate(NORMAL_FORMAT)
    }
}

fun CardEntity.toDomain(evidences: List<Evidence> = emptyList()): Card {
    return Card(
        id = this.id,
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
        evidences = evidences,
        creationDateFormatted = validateDate()
    )
}