package com.ih.m2.core.preferences

import android.content.Context
import android.content.SharedPreferences
import com.ih.m2.domain.model.NetworkStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferences @Inject constructor(
    @ApplicationContext context: Context
) {

    private var sharedPreferences: SharedPreferences? = null

    init {
        sharedPreferences = context.getSharedPreferences("m2_app_preferences", Context.MODE_PRIVATE)
    }

    suspend fun saveNetworkPreference(network: String)  {
        sharedPreferences?.let {
            with(it.edit()) {
                putString("network_preference", network)
                commit()
            }
        }
    }

    fun getNetworkPreference(): String {
        return sharedPreferences?.getString("network_preference","").orEmpty()
    }


}