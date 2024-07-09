package com.ih.m2.data.repository.firebase

import android.content.Context
import android.util.Log
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.ih.m2.data.model.CreateCardRequest
import com.ih.m2.domain.model.Card
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class FirebaseAnalyticsHelper @Inject constructor(
    @ApplicationContext context: Context
) {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    init {
        firebaseAnalytics.setAnalyticsCollectionEnabled(true)
    }

    fun logCreateCard(card: Card) {
        try {
            firebaseAnalytics.setUserId(card.creatorId)
            val json = Gson().toJson(card)
            firebaseAnalytics.logEvent("create_card_event", bundleOf("card" to json))
        } catch (e: Exception) {
            Log.e("test","Error analytics ${e.localizedMessage}")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun logCreateRemoteCard(card: Card) {
        try {
            firebaseAnalytics.setUserId(card.creatorId)
            val json = Gson().toJson(card)
            firebaseAnalytics.logEvent("create_remote_card_event", bundleOf("card" to json))
        } catch (e: Exception) {
            Log.e("test","Error analytics ${e.localizedMessage}")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun logCreateRemoteCardRequest(card: CreateCardRequest) {
        try {
            val json = Gson().toJson(card)
            firebaseAnalytics.logEvent("create_remote_card_request_event", bundleOf("card" to json))
        } catch (e: Exception) {
            Log.e("test","Error analytics ${e.localizedMessage}")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun logCreateCardException(e: Throwable) {
        try {
            val json = Gson().toJson(e)
            firebaseAnalytics.logEvent("create_card_exception_event", bundleOf("value" to json))
            firebaseAnalytics.logEvent("create_card_exception_message_event", bundleOf("value" to e.localizedMessage.orEmpty()))
        } catch (e: Exception) {
            Log.e("test","Error analytics ${e.localizedMessage}")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun logSyncCardException(e: Exception) {
        try {
            val json = Gson().toJson(e)
            firebaseAnalytics.logEvent("upload_card_exception_event", bundleOf("value" to json))
            firebaseAnalytics.logEvent("upload_card_exception_message_event", bundleOf("value" to e.localizedMessage.orEmpty()))
        } catch (e: Exception) {
            Log.e("test","Error analytics ${e.localizedMessage}")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}