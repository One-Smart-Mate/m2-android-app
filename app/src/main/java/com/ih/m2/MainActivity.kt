package com.ih.m2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.compose.mavericksViewModel
import com.ih.m2.ui.navigation.AppNavigation
import com.ih.m2.ui.pages.splash.SplashViewModel
import com.ih.m2.ui.theme.M2androidappTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    private val splashViewModel: SplashViewModel by viewModels()
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
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    val state = splashViewModel.startRoute.collectAsState()
                    AppNavigation(startDestination = state.value)
                }
            }
        }
    }
}
