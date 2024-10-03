package com.ih.osm.core.file

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.ih.osm.BuildConfig
import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.data.model.CreateCardRequest
import com.ih.osm.data.model.CreateDefinitiveSolutionRequest
import com.ih.osm.data.model.CreateProvisionalSolutionRequest
import com.ih.osm.domain.model.Card
import com.ih.osm.ui.extensions.YYYY_MM_DD_HH_MM_SS
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.Calendar
import javax.inject.Inject

@Suppress("ktlint:standard:max-line-length")
class FileHelper
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) {
    private val fileName = "osm_logs_file"
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
            val filePath =
                File.createTempFile(
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
                it.appendText(
                    "\n********************** Create local card - ${Calendar.getInstance().time.YYYY_MM_DD_HH_MM_SS} **********************"
                )
                it.appendText("${Gson().toJson(card)}\n")
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun logCreateCardRequest(cardRequest: CreateCardRequest) {
        try {
            getLocalFile()?.let {
                it.appendText(
                    "\n********************** Card request - ${Calendar.getInstance().time.YYYY_MM_DD_HH_MM_SS} **********************"
                )
                it.appendText("${Gson().toJson(cardRequest)}\n")
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun logCreateCardRequestSuccess(card: Card) {
        try {
            getLocalFile()?.let {
                it.appendText(
                    "\n********************** Card Success Sync - ${Calendar.getInstance().time.YYYY_MM_DD_HH_MM_SS} **********************"
                )
                it.appendText("${Gson().toJson(card)}\n")
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun logException(exception: Throwable) {
        try {
            getLocalFile()?.let {
                it.appendText(
                    "\n********************** Exception - ${Calendar.getInstance().time.YYYY_MM_DD_HH_MM_SS} **********************"
                )
                it.appendText("${Gson().toJson(exception)}\n")
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun logProvisionalSolution(provisionalSolutionRequest: CreateProvisionalSolutionRequest) {
        try {
            getLocalFile()?.let {
                it.appendText(
                    "\n********************** Provisional Solution - ${Calendar.getInstance().time.YYYY_MM_DD_HH_MM_SS} **********************"
                )
                it.appendText("${Gson().toJson(provisionalSolutionRequest)}\n")
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun logDefinitiveSolution(definitiveSolutionRequest: CreateDefinitiveSolutionRequest) {
        try {
            getLocalFile()?.let {
                it.appendText(
                    "\n********************** Definitive Solution - ${Calendar.getInstance().time.YYYY_MM_DD_HH_MM_SS} **********************"
                )
                it.appendText("${Gson().toJson(definitiveSolutionRequest)}\n")
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}
