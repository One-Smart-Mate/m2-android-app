package com.ih.osm

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE
import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.core.workmanager.AppWorker
import com.ih.osm.core.workmanager.WorkManagerUUID
import com.ih.osm.ui.navigation.AppNavigation
import com.ih.osm.ui.pages.splash.SplashViewModel
import com.ih.osm.ui.theme.OsmAppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.Duration
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
        handleAppUpdates()
        setContent {
            OsmAppTheme {
                val state = splashViewModel.startRoute.collectAsState()
                AppNavigation(startDestination = state.value)
            }
        }
    }

    private fun observeNetworkChanges() {
        val networkRequest =
            NetworkRequest.Builder()
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
//        val uuid = WorkManagerUUID.get()
//        uuid?.let {
//            val workRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                OneTimeWorkRequestBuilder<CardWorker>()
//                    .setId(uuid)
//                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
//                    .setBackoffCriteria(
//                        backoffPolicy = BackoffPolicy.LINEAR,
//                        duration = Duration.ofSeconds(5)
//                    ).build()
//            } else {
//                OneTimeWorkRequestBuilder<CardWorker>()
//                    .setId(uuid)
//                    .setBackoffCriteria(
//                        backoffPolicy = BackoffPolicy.LINEAR,
//                        duration = Duration.ofSeconds(5)
//                    ).build()
//            }
//            WorkManager.getInstance(this@MainActivity).enqueue(workRequest)
//        }
    }

    fun enqueueSyncCardsWork() {
        val workRequest = OneTimeWorkRequestBuilder<AppWorker>().build()
        val uuid = WorkManagerUUID.get().toString()
        WorkManager.getInstance(this@MainActivity).enqueueUniqueWork(
            uuid,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
    }

    private fun checkNetworkPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE
            ),
            2
        )
    }

    private fun openPlayStore() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: Exception) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }

    fun showUpdateDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.update_title))
            .setMessage(getString(R.string.update_description))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.update_list)) { _, _ ->
                openPlayStore()
            }
            .setNegativeButton(getString(R.string.update_cancel)) { _, _ ->
                this.finish()
            }.show()
    }

    private fun handleAppUpdates() {
        val appUpdateManager = AppUpdateManagerFactory.create(this@MainActivity)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        val updateOptions = AppUpdateOptions.newBuilder(IMMEDIATE).build()
        val updateFlowResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode != RESULT_OK) {
                showUpdateDialog()
            }
        }
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    updateFlowResultLauncher,
                    updateOptions
                )
            }
        }
    }
}
