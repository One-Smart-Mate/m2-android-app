package com.ih.m2.domain.repository.firebase

import com.ih.m2.domain.model.Evidence

interface FirebaseStorageRepository {

    suspend fun uploadEvidence(evidence: Evidence): String
}