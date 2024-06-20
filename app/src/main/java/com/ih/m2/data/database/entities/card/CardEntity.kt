package com.ih.m2.data.database.entities.card

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ih.m2.domain.model.Card
import com.ih.m2.ui.utils.STORED_REMOTE


@Entity(tableName = "card_table")
data class CardEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "site_card_id")
    val siteCardID: Long,
    @ColumnInfo(name = "site_id")
    val siteID: String?,
    @ColumnInfo(name = "site_code")
    val siteCode: String?,
    @ColumnInfo(name = "card_uuid")
    val cardUUID: String,
    @ColumnInfo(name = "card_type_color")
    val cardTypeColor: String,
    @ColumnInfo(name = "feasibility")
    val feasibility: String?,
    @ColumnInfo(name = "effect")
    val effect: String?,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "card_creation_date")
    val cardCreationDate: String,
    @ColumnInfo(name = "card_due_date")
    val cardDueDate: String,
    @ColumnInfo(name = "area_id")
    val areaID: Long,
    @ColumnInfo(name = "area_name")
    val areaName: String,
    @ColumnInfo(name = "level")
    val level: Long,
    @ColumnInfo(name = "superior_id")
    val superiorID: String?,
    @ColumnInfo(name = "priority_id")
    val priorityID: String?,
    @ColumnInfo(name = "priority_code")
    val priorityCode: String?,
    @ColumnInfo(name = "priority_description")
    val priorityDescription: String,
    @ColumnInfo(name = "card_methodology")
    val cardTypeMethodology: String,
    @ColumnInfo(name = "card_methodology_name")
    val cardTypeMethodologyName: String,
    @ColumnInfo(name = "card_type_value")
    val cardTypeValue: String,
    @ColumnInfo(name = "card_type_id")
    val cardTypeID: String?,
    @ColumnInfo(name = "card_type_name")
    val cardTypeName: String,
    @ColumnInfo(name = "preclassifier_id")
    val preclassifierId: String,
    @ColumnInfo(name = "preclassifier_code")
    val preclassifierCode: String,
    @ColumnInfo(name = "preclassifier_description")
    val preclassifierDescription: String,
    @ColumnInfo(name = "creator_id")
    val creatorID: String?,
    @ColumnInfo(name = "creator_name")
    val creatorName: String,
    @ColumnInfo(name = "responsable_id")
    val responsableID: String?,
    @ColumnInfo(name = "responsable_name")
    val responsableName: String,
    @ColumnInfo(name = "mechanic_id")
    val mechanicID: String?,
    @ColumnInfo(name = "mechanic_name")
    val mechanicName: String?,
    @ColumnInfo(name = "user_provisional_solution_id")
    val userProvisionalSolutionID: String?,
    @ColumnInfo(name = "user_provisional_solution_name")
    val userProvisionalSolutionName: String?,
    @ColumnInfo(name = "user_app_provisional_solution_id")
    val userAppProvisionalSolutionID: String?,
    @ColumnInfo(name = "user_app_provisional_solution_name")
    val userAppProvisionalSolutionName: String?,
    @ColumnInfo(name = "user_definitive_solution_id")
    val userDefinitiveSolutionID: String?,
    @ColumnInfo(name = "user_definitive_solution_name")
    val userDefinitiveSolutionName: String?,
    @ColumnInfo(name = "user_app_definitive_solution_id")
    val userAppDefinitiveSolutionID: String?,
    @ColumnInfo(name = "user_app_definitive_solution_name")
    val userAppDefinitiveSolutionName: String?,
    @ColumnInfo(name = "manager_id")
    val managerID: String?,
    @ColumnInfo(name = "manager_name")
    val managerName: String,
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

fun CardEntity.toDomain(): Card {
    return Card(
        id = this.id,
        siteCardID = this.siteCardID,
        siteID = this.siteID,
        siteCode = this.siteCode,
        cardUUID = this.cardUUID,
        cardTypeColor = this.cardTypeColor,
        feasibility = this.feasibility,
        effect = this.effect,
        status = this.status,
        cardCreationDate = this.cardCreationDate,
        cardDueDate = this.cardDueDate,
        areaID = this.areaID,
        areaName = this.areaName,
        level = this.level,
        superiorID = this.superiorID,
        priorityID = this.priorityID,
        priorityCode = this.priorityCode,
        priorityDescription = this.priorityDescription,
        cardTypeMethodology = this.cardTypeMethodology,
        cardTypeMethodologyName = this.cardTypeMethodologyName,
        cardTypeValue = this.cardTypeValue,
        cardTypeID = this.cardTypeID,
        cardTypeName = this.cardTypeName,
        preclassifierId = this.preclassifierId,
        preclassifierCode = this.preclassifierCode,
        preclassifierDescription = this.preclassifierDescription,
        creatorID = this.creatorID,
        creatorName = this.creatorName,
        responsableID = this.responsableID,
        responsableName = this.responsableName,
        mechanicID = this.mechanicID,
        mechanicName = this.mechanicName,
        userProvisionalSolutionID = this.userProvisionalSolutionID,
        userProvisionalSolutionName = this.userProvisionalSolutionName,
        userAppProvisionalSolutionID = this.userAppProvisionalSolutionID,
        userAppProvisionalSolutionName = this.userAppProvisionalSolutionName,
        userDefinitiveSolutionID = this.userDefinitiveSolutionID,
        userDefinitiveSolutionName = this.userDefinitiveSolutionName,
        userAppDefinitiveSolutionID = this.userAppDefinitiveSolutionID,
        userAppDefinitiveSolutionName = this.userAppDefinitiveSolutionName,
        managerID = this.managerID,
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
        stored = this.stored ?: STORED_REMOTE
    )
}