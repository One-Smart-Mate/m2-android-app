package com.ih.m2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.airbnb.mvrx.Mavericks
import com.ih.m2.ui.navigation.AppNavigation
import com.ih.m2.ui.pages.splash.SplashViewModel
import com.ih.m2.ui.theme.M2androidappTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val splashViewModel: SplashViewModel by viewModels()
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Mavericks.initialize(this)
        Timber.plant(Timber.DebugTree())

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition{
            splashViewModel.isAuthenticated.value.not()
        }
        setContent {
            M2androidappTheme {
                val state = splashViewModel.startRoute.collectAsState()
                AppNavigation(startDestination = state.value)
            }
        }
    }
}
