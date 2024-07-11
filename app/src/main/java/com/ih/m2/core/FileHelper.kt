package com.ih.m2.core

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.ih.m2.BuildConfig
import com.ih.m2.core.preferences.SharedPreferences
import com.ih.m2.data.model.CreateCardRequest
import com.ih.m2.data.model.CreateDefinitiveSolutionRequest
import com.ih.m2.data.model.CreateProvisionalSolutionRequest
import com.ih.m2.domain.model.Card
import com.ih.m2.ui.extensions.YYYY_MM_DD_HH_MM_SS
import com.ih.m2.ui.utils.EMPTY
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.Calendar
import javax.inject.Inject

class FileHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) {

    private val fileName = "m2_logs_file"
    private var path = EMPTY

    init {
        initFilePath()
    }

    fun getFileUri(): Uri? {
        return getLocalFile()?.let {
            FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                it
            )
        }
    }

    private fun initFilePath() {
        val localPath = sharedPreferences.getLogPath()
        if (localPath.isEmpty()) {
            val filePath = File.createTempFile(
                fileName,
                ".txt",
                this.context.externalCacheDir
            )
            sharedPreferences.saveLogFile(filePath.path)
            this.path = filePath.path
        } else {
            this.path = localPath
        }
    }

    private fun getLocalFile(): File? {
        return if (File(this.path).exists()) {
            File(this.path)
        } else {
            sharedPreferences.saveLogFile(EMPTY)
            null
        }
    }

    fun logCreateCard(card: Card) {
        try {
            getLocalFile()?.let {
                it.appendText("\n********************** Create local card - ${Calendar.getInstance().time.YYYY_MM_DD_HH_MM_SS} **********************")
                it.appendText("${Gson().toJson(card)}\n")
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun logCreateCardRequest(cardRequest: CreateCardRequest) {
        try {
            getLocalFile()?.let {
                it.appendText("\n********************** Card request - ${Calendar.getInstance().time.YYYY_MM_DD_HH_MM_SS} **********************")
                it.appendText("${Gson().toJson(cardRequest)}\n")
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun logException(exception: Throwable) {
        try {
            getLocalFile()?.let {
                it.appendText("\n********************** Exception - ${Calendar.getInstance().time.YYYY_MM_DD_HH_MM_SS} **********************")
                it.appendText("${Gson().toJson(exception)}\n")
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun logProvisionalSolution(provisionalSolutionRequest: CreateProvisionalSolutionRequest) {
        try {
            getLocalFile()?.let {
                it.appendText("\n********************** Provisional Solution - ${Calendar.getInstance().time.YYYY_MM_DD_HH_MM_SS} **********************")
                it.appendText("${Gson().toJson(provisionalSolutionRequest)}\n")
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun logDefinitiveSolution(definitiveSolutionRequest: CreateDefinitiveSolutionRequest) {
        try {
            getLocalFile()?.let {
                it.appendText("\n********************** Definitive Solution - ${Calendar.getInstance().time.YYYY_MM_DD_HH_MM_SS} **********************")
                it.appendText("${Gson().toJson(definitiveSolutionRequest)}\n")
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

}