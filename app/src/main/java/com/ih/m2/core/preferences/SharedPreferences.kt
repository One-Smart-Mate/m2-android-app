package com.ih.m2.core.preferences

import android.content.Context
import android.content.SharedPreferences
import com.ih.m2.domain.model.NetworkStatus
import com.ih.m2.ui.utils.EMPTY
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferences @Inject constructor(
    @ApplicationContext context: Context
) {

    private var sharedPreferences: SharedPreferences? = null

    companion object {
        private const val M2_APP_PREFERENCES = "m2_app_preferences"
        private const val NETWORK_PREFERENCES = "network_preference"
        private const val LOG_FILE_PREFERENCES = "path_log_file"
    }

    init {
        sharedPreferences = context.getSharedPreferences(M2_APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun saveNetworkPreference(network: String) {
        sharedPreferences?.let {
            with(it.edit()) {
                putString(NETWORK_PREFERENCES, network)
                commit()
            }
        }
    }

    fun getNetworkPreference(): String {
        return sharedPreferences?.getString(NETWORK_PREFERENCES, EMPTY).orEmpty()
    }

    fun saveLogFile(path: String) {
        sharedPreferences?.let {
            with(it.edit()) {
                putString(LOG_FILE_PREFERENCES, path)
                commit()
            }
        }
    }

    fun getLogPath(): String {
        return sharedPreferences?.getString(LOG_FILE_PREFERENCES, EMPTY).orEmpty()
    }

}