package com.ih.osm.data.repository.firebase

import androidx.core.net.toUri
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.EvidenceType
import com.ih.osm.domain.repository.firebase.FirebaseStorageRepository
import com.ih.osm.domain.usecase.user.GetUserUseCase
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
        private val getUserUseCase: GetUserUseCase,
    ) : FirebaseStorageRepository {
        override suspend fun uploadEvidence(evidence: Evidence): String {
            return try {
                val siteId = getUserUseCase()?.siteId.defaultIfNull("0")
                val evidenceType = EvidenceType.valueOf(evidence.type)
                val evidenceName = getEvidenceFileName(evidenceType)
                val evidenceReference =
                    getEvidenceReference(evidenceType, evidenceName, evidence.cardId, siteId)
                evidenceReference.putFile(evidence.url.toUri()).await()
                val url = evidenceReference.downloadUrl.await()
                url.toString()
            } catch (e: Exception) {
                LoggerHelperManager.logException(e)
                FirebaseCrashlytics.getInstance().recordException(e)
                EMPTY
            }
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
            }
        }

        private fun getEvidenceReference(
            type: EvidenceType,
            evidenceName: String,
            cardId: String,
            siteId: String,
        ): StorageReference {
            val prefixImages = "site-$siteId/$cardId/images/$evidenceName"
            val prefixVideos = "site-$siteId/$cardId/videos/$evidenceName"
            val prefixAudios = "site-$siteId/$cardId/audios/$evidenceName"
            return when (type) {
                EvidenceType.IMCR, EvidenceType.IMCL, EvidenceType.IMPS ->
                    firebaseStorage.reference.child(
                        prefixImages,
                    )

                EvidenceType.VICR, EvidenceType.VICL, EvidenceType.VIPS ->
                    firebaseStorage.reference.child(
                        prefixVideos,
                    )

                EvidenceType.AUCR, EvidenceType.AUCL, EvidenceType.AUPS ->
                    firebaseStorage.reference.child(
                        prefixAudios,
                    )
            }
        }

        override suspend fun deleteEvidence(cardUUID: String): Boolean {
            return try {
                val siteId = getUserUseCase()?.siteId.defaultIfNull("0")

                val imagesReference =
                    firebaseStorage.getReference("site-$siteId/$cardUUID/images/")
                imagesReference.listAll().await().items.forEach { image ->
                    image.delete()
                }
                val videosReference =
                    firebaseStorage.getReference("site-$siteId/$cardUUID/videos/")
                videosReference.listAll().await().items.forEach { image ->
                    image.delete()
                }

                val audiosReference =
                    firebaseStorage.getReference("site-$siteId/$cardUUID/audios/")
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
    }
