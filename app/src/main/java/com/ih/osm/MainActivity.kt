package com.ih.osm

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.airbnb.mvrx.Mavericks
import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.core.workmanager.CardWorker
import com.ih.osm.core.workmanager.WorkManagerUUID
import com.ih.osm.ui.navigation.AppNavigation
import com.ih.osm.ui.pages.splash.SplashViewModel
import com.ih.osm.ui.theme.OsmAppTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.Duration

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Mavericks.initialize(this)
        Timber.plant(Timber.DebugTree())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermissions()
        }
        checkNetworkPermissions()
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            splashViewModel.isAuthenticated.value.not()
        }
        observeNetworkChanges()
        setContent {
            OsmAppTheme {
                val state = splashViewModel.startRoute.collectAsState()
                AppNavigation(startDestination = state.value)
            }
        }
    }

    private fun observeNetworkChanges() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        connectivityManager?.requestNetwork(networkRequest, NetworkConnection.networkCallback)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun workRequest() {
        val uuid = WorkManagerUUID.get()
        uuid?.let {
            val workRequest = OneTimeWorkRequestBuilder<CardWorker>()
                .setId(uuid)
                .setBackoffCriteria(
                    backoffPolicy = BackoffPolicy.LINEAR,
                    duration = Duration.ofSeconds(5)
                ).build()
            WorkManager.getInstance(this@MainActivity).enqueue(workRequest)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
    }

    private fun checkNetworkPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE
            ), 2
        )
    }
}

