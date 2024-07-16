package com.ih.m2.data.repository.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ih.m2.domain.model.Evidence
import com.ih.m2.domain.model.EvidenceType
import com.ih.m2.domain.repository.firebase.FirebaseStorageRepository
import com.ih.m2.domain.usecase.user.GetUserUseCase
import com.ih.m2.ui.extensions.YYYY_MM_DD_HH_MM_SS
import com.ih.m2.ui.extensions.defaultIfNull
import com.ih.m2.ui.extensions.timeStamp
import com.ih.m2.ui.utils.EMPTY
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class FirebaseStorageRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val getUserUseCase: GetUserUseCase
) : FirebaseStorageRepository {

    override suspend fun uploadEvidence(evidence: Evidence): String {
        return try {
            val siteId = getUserUseCase()?.siteId.defaultIfNull("0")
            val evidenceType = EvidenceType.valueOf(evidence.type)
            val evidenceName = getEvidenceFileName(evidenceType)
            val evidenceReference =
                getEvidenceReference(evidenceType, evidenceName, evidence.cardId, siteId)
            evidenceReference.putFile(Uri.parse(evidence.url)).await()
            val url = evidenceReference.downloadUrl.await()
            Log.e("test", "Result image ${url}")
            url.toString()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log(e.localizedMessage.orEmpty())
            EMPTY
        }
    }

    private fun getEvidenceFileName(
        type: EvidenceType
    ): String {
        val timeStamp: String = Date().YYYY_MM_DD_HH_MM_SS
        return when (type) {
            EvidenceType.IMCR -> "IMAGE_CR_${timeStamp}.jpg"
            EvidenceType.VICR -> "VIDEO_CR_${timeStamp}.mp4"
            EvidenceType.AUCR -> "AUDIO_CR_${timeStamp}.mp3"
            EvidenceType.IMCL -> "IMAGE_CL_${timeStamp}.jpg"
            EvidenceType.VICL -> "VIDEO_CL_${timeStamp}.mp4"
            EvidenceType.AUCL -> "AUDIO_CL_${timeStamp}.mp3"
            EvidenceType.IMPS -> "IMAGE_PS_${timeStamp}.jpg"
            EvidenceType.AUPS -> "AUDIO_PS_${timeStamp}.mp4"
            EvidenceType.VIPS -> "VIDEO_PS_${timeStamp}.mp4"
        }
    }

    private fun getEvidenceReference(
        type: EvidenceType,
        evidenceName: String,
        cardId: String,
        siteId: String,
    ): StorageReference {
        val prefixImages = "site-${siteId}/$cardId/images/$evidenceName"
        val prefixVideos = "site-${siteId}/$cardId/videos/$evidenceName"
        val prefixAudios = "site-${siteId}/$cardId/audios/$evidenceName"
        return when (type) {
            EvidenceType.IMCR, EvidenceType.IMCL, EvidenceType.IMPS -> firebaseStorage.reference.child(
                prefixImages
            )

            EvidenceType.VICR, EvidenceType.VICL, EvidenceType.VIPS -> firebaseStorage.reference.child(
                prefixVideos
            )

            EvidenceType.AUCR, EvidenceType.AUCL, EvidenceType.AUPS -> firebaseStorage.reference.child(
                prefixAudios
            )
        }
    }
}