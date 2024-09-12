package com.ih.osm.domain.repository.firebase

import com.ih.osm.domain.model.Evidence

interface FirebaseStorageRepository {
    suspend fun uploadEvidence(evidence: Evidence): String
}
