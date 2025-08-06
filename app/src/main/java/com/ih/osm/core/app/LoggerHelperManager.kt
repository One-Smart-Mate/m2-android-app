package com.ih.osm.core.app

import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import com.ih.osm.core.file.FileHelper
import com.ih.osm.data.model.CreateCardRequest
import com.ih.osm.data.model.CreateDefinitiveSolutionRequest
import com.ih.osm.data.model.CreateProvisionalSolutionRequest
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.User

object LoggerHelperManager {
    private var fileHelper: FileHelper? = null

    // Lazy initialization of LoggerHelperManager with FileHelper
    fun initialize(fileHelper: FileHelper): LoggerHelperManager {
        if (this.fileHelper == null) {
            this.fileHelper = fileHelper
        }
        return this
    }

    fun logException(exception: Throwable) {
        Log.e("LoggerHelperManager", "Exception logged: ${exception.message}", exception)
        fileHelper?.logException(exception)
    }

    fun logException(exception: String) {
        Log.e("LoggerHelperManager", "Exception logged: $exception")
        fileHelper?.logException(exception)
    }

    fun logUser(user: User) {
        fileHelper?.logUser(user)
    }

    fun logNotification(message: RemoteMessage) {
        fileHelper?.logNotification(message)
    }

    fun logDefinitiveSolution(definitiveSolutionRequest: CreateDefinitiveSolutionRequest) {
        fileHelper?.logDefinitiveSolution(definitiveSolutionRequest)
    }

    fun logProvisionalSolution(provisionalSolutionRequest: CreateProvisionalSolutionRequest) {
        fileHelper?.logProvisionalSolution(provisionalSolutionRequest)
    }

    fun logCreateCard(card: Card) {
        fileHelper?.logCreateCard(card)
    }

    fun logCreateCardRequest(cardRequest: CreateCardRequest) {
        fileHelper?.logCreateCardRequest(cardRequest)
    }

    fun logCreateCardRequestSuccess(card: Card) {
        fileHelper?.logCreateCardRequestSuccess(card)
    }

    fun logToken(token: String) {
        fileHelper?.logToken(token)
    }

    fun getLogFile() = fileHelper?.getFileUri()
}
