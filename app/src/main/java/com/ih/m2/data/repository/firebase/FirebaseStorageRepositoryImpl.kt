package com.ih.m2.data.repository.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ih.m2.domain.model.Evidence
import com.ih.m2.domain.model.EvidenceType
import com.ih.m2.domain.repository.firebase.FirebaseStorageRepository
import com.ih.m2.ui.extensions.timeStamp
import com.ih.m2.ui.utils.EMPTY
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class FirebaseStorageRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage
) : FirebaseStorageRepository {

    override suspend fun uploadEvidence(evidence: Evidence): String {
        return try {
            val evidenceType = EvidenceType.valueOf(evidence.type)
            val evidenceName = getEvidenceFileName(evidenceType)
            val evidenceReference =
                getEvidenceReference(evidenceType, evidenceName, evidence.cardId)
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
        val timeStamp: String = Date().timeStamp()
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
        cardId: String
    ): StorageReference {
        return when (type) {
            EvidenceType.IMCR -> firebaseStorage.reference.child("evidence/created/images/$cardId/$evidenceName")
            EvidenceType.VICR -> firebaseStorage.reference.child("evidence/created/videos/$cardId/$evidenceName")
            EvidenceType.AUCR -> firebaseStorage.reference.child("evidence/created/audios/$cardId/$evidenceName")
            EvidenceType.IMCL -> firebaseStorage.reference.child("evidence/closed/images/$cardId/$evidenceName")
            EvidenceType.VICL -> firebaseStorage.reference.child("evidence/closed/videos/$cardId/$evidenceName")
            EvidenceType.AUCL -> firebaseStorage.reference.child("evidence/closed/audios/$cardId/$evidenceName")
            EvidenceType.IMPS -> firebaseStorage.reference.child("evidence/provisional_solution/images/$cardId/$evidenceName")
            EvidenceType.AUPS -> firebaseStorage.reference.child("evidence/provisional_solution/audios/$cardId/$evidenceName")
            EvidenceType.VIPS ->  firebaseStorage.reference.child("evidence/provisional_solution/videos/$cardId/$evidenceName")
        }
    }
}