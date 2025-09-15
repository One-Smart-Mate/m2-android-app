package com.ih.osm.data.repository.firebase

import androidx.core.net.toUri
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.EvidenceParentType
import com.ih.osm.domain.model.EvidenceType
import com.ih.osm.domain.repository.firebase.FirebaseStorageRepository
import com.ih.osm.domain.usecase.session.GetSessionUseCase
import com.ih.osm.ui.extensions.YYYY_MM_DD_HH_MM_SS
import com.ih.osm.ui.extensions.defaultIfNull
import com.ih.osm.ui.utils.EMPTY
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class FirebaseStorageRepositoryImpl
    @Inject
    constructor(
        private val firebaseStorage: FirebaseStorage,
        private val gerSessionUseCase: GetSessionUseCase,
    ) : FirebaseStorageRepository {
        override suspend fun uploadEvidence(evidence: Evidence): String =
            try {
                val siteId = gerSessionUseCase().siteId.defaultIfNull("0")
                val evidenceType = EvidenceType.valueOf(evidence.type)
                val evidenceName = getEvidenceFileName(evidenceType)
                val evidenceReference =
                    getEvidenceReference(
                        evidenceType,
                        evidenceName,
                        evidence.cardId,
                        siteId,
                        evidence.parentType,
                    )
                evidenceReference.putFile(evidence.url.toUri()).await()
                val url = evidenceReference.downloadUrl.await()
                url.toString()
            } catch (e: Exception) {
                LoggerHelperManager.logException(e)
                FirebaseCrashlytics.getInstance().recordException(e)
                EMPTY
            }

        private fun getEvidenceFileName(type: EvidenceType): String {
            val timeStamp: String = Date().YYYY_MM_DD_HH_MM_SS
            return when (type) {
                EvidenceType.IMCR -> "IMAGE_CR_$timeStamp.jpg"
                EvidenceType.VICR -> "VIDEO_CR_$timeStamp.mp4"
                EvidenceType.AUCR -> "AUDIO_CR_$timeStamp.mp3"
                EvidenceType.IMCL -> "IMAGE_CL_$timeStamp.jpg"
                EvidenceType.VICL -> "VIDEO_CL_$timeStamp.mp4"
                EvidenceType.AUCL -> "AUDIO_CL_$timeStamp.mp3"
                EvidenceType.IMPS -> "IMAGE_PS_$timeStamp.jpg"
                EvidenceType.AUPS -> "AUDIO_PS_$timeStamp.mp3"
                EvidenceType.VIPS -> "VIDEO_PS_$timeStamp.mp4"
                EvidenceType.INITIAL -> "IMAGE_INITIAL_$timeStamp.jpg"
                EvidenceType.FINAL -> "IMAGE_FINAL_$timeStamp.jpg"
            }
        }

        private fun getEvidenceReference(
            type: EvidenceType,
            evidenceName: String,
            parentId: String,
            siteId: String,
            parentType: EvidenceParentType,
        ): StorageReference {
            val basePath =
                when (parentType) {
                    EvidenceParentType.CARD -> "site_$siteId/cards/$parentId"
                    EvidenceParentType.EXECUTION -> "site_$siteId/executions/$parentId"
                }

            val path =
                when (parentType) {
                    EvidenceParentType.EXECUTION -> "$basePath/images/$evidenceName"
                    EvidenceParentType.CARD ->
                        when (type) {
                            EvidenceType.IMCR, EvidenceType.IMCL, EvidenceType.IMPS -> "$basePath/images/$evidenceName"
                            EvidenceType.VICR, EvidenceType.VICL, EvidenceType.VIPS -> "$basePath/videos/$evidenceName"
                            EvidenceType.AUCR, EvidenceType.AUCL, EvidenceType.AUPS -> "$basePath/audios/$evidenceName"
                            else -> throw IllegalArgumentException("EvidenceType $type is not valid for CARD")
                        }
                }

            return firebaseStorage.reference.child(path)
        }

        override suspend fun deleteEvidence(cardUUID: String): Boolean =
            try {
                val siteId = gerSessionUseCase().siteId.defaultIfNull("0")

                val imagesReference =
                    firebaseStorage.getReference("site_$siteId/cards/$cardUUID/images/")
                imagesReference.listAll().await().items.forEach { image ->
                    image.delete()
                }
                val videosReference =
                    firebaseStorage.getReference("site_$siteId/cards/$cardUUID/videos/")
                videosReference.listAll().await().items.forEach { image ->
                    image.delete()
                }

                val audiosReference =
                    firebaseStorage.getReference("site_$siteId/cards/$cardUUID/audios/")
                audiosReference.listAll().await().items.forEach { image ->
                    image.delete()
                }
                true
            } catch (e: Exception) {
                LoggerHelperManager.logException(e)
                FirebaseCrashlytics.getInstance().recordException(e)
                false
            }
    }
