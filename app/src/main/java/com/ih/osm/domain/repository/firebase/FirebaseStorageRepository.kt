package com.ih.osm.domain.repository.firebase

import android.net.Uri
import com.ih.osm.domain.model.Evidence

interface FirebaseStorageRepository {
    suspend fun uploadEvidence(evidence: Evidence): String

    suspend fun deleteEvidence(cardUUID: String): Boolean

    suspend fun uploadLogFile(
        userId: String,
        appVersion: String,
        fileUri: Uri,
    ): String
}
