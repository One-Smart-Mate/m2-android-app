package com.ih.m2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkManager
import androidx.work.await
import com.airbnb.mvrx.Mavericks
import com.ih.m2.core.FileHelper
import com.ih.m2.core.network.NetworkConnection
import com.ih.m2.core.workmanager.CardWorker
import com.ih.m2.ui.extensions.EEE_MMM_DD_YYYY
import com.ih.m2.ui.navigation.AppNavigation
import com.ih.m2.ui.pages.splash.SplashViewModel
import com.ih.m2.ui.theme.M2androidappTheme
import com.ih.m2.ui.utils.EMPTY
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.Duration
import java.util.UUID
import javax.inject.Inject

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
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//           // workRequest(applicationContext)
//        }
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            splashViewModel.isAuthenticated.value.not()
        }
        observeNetworkChanges()
        setContent {
            M2androidappTheme {
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
    fun workRequest(context: Context) {
        val uuid = TestObject.getUUID()
        Log.e("test", "Aquiii ${uuid}")
        uuid?.let {
            val workRequest = OneTimeWorkRequestBuilder<CardWorker>()
                .setId(uuid)
                .setInitialDelay(Duration.ofSeconds(5))
                .setBackoffCriteria(
                    backoffPolicy = BackoffPolicy.LINEAR,
                    duration = Duration.ofSeconds(60 * 5)
                ).build()
            WorkManager.getInstance(context).enqueue(workRequest)
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

object TestObject {


    private var uuid: UUID? = null

    fun getUUID(): UUID? {
        if (uuid == null) {
            uuid = UUID.randomUUID()
        }
        return uuid
    }
}
