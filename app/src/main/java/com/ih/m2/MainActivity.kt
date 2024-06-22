package com.ih.m2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import com.ih.m2.core.workmanager.CardWorker
import com.ih.m2.ui.navigation.AppNavigation
import com.ih.m2.ui.pages.splash.SplashViewModel
import com.ih.m2.ui.theme.M2androidappTheme
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            workRequest(applicationContext)
        }
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            splashViewModel.isAuthenticated.value.not()
        }

        setContent {
            M2androidappTheme {
                val state = splashViewModel.startRoute.collectAsState()
                AppNavigation(startDestination = state.value)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun workRequest(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<CardWorker>()
            .setInitialDelay(Duration.ofSeconds(5))
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.LINEAR,
                duration = Duration.ofSeconds(15)
            ).build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
    }
}
