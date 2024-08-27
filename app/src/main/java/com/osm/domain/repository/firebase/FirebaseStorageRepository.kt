package com.osm.domain.repository.firebase

import com.osm.domain.model.Evidence

interface FirebaseStorageRepository {

    suspend fun uploadEvidence(evidence: Evidence): String
}