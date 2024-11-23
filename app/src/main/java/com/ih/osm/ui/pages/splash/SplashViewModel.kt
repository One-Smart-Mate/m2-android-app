package com.ih.osm.ui.pages.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.ih.osm.domain.usecase.firebase.SyncFirebaseTokenUseCase
import com.ih.osm.domain.usecase.user.GetUserUseCase
import com.ih.osm.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@HiltViewModel
class SplashViewModel
@Inject
constructor(
    private val coroutineContext: CoroutineContext,
    private val getUserUseCase: GetUserUseCase,
    private val syncFirebaseTokenUseCase: SyncFirebaseTokenUseCase
) : ViewModel() {
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _startRoute = MutableStateFlow(Screen.Login.route)
    val startRoute = _startRoute.asStateFlow()

    init {
        handleSyncFirebaseToken()
        handleSyncFirebaseConfigs()
    }

    private fun handleSyncFirebaseConfigs() {
        viewModelScope.launch {
            try {
                val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
                val configSettings = remoteConfigSettings {
                    minimumFetchIntervalInSeconds = 3600
                }
                remoteConfig.setConfigSettingsAsync(configSettings)
                remoteConfig.fetchAndActivate().await()

                handleGetUser()
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                handleGetUser()
            }
        }
    }

    private fun handleGetUser() {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getUserUseCase()
            }.onSuccess {
                val route =
                    if (it != null) {
                        FirebaseCrashlytics.getInstance().setUserId(it.userId)
                        Screen.HomeV2.route
                    } else {
                        Screen.Login.route
                    }
                navigateToScreen(route)
            }.onFailure {
                navigateToScreen(Screen.Login.route)
            }
        }
    }

    private fun handleSyncFirebaseToken() {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                syncFirebaseTokenUseCase()
            }
        }
    }

    private suspend fun navigateToScreen(route: String) {
        _startRoute.value = route
        delay(1000)
        _isAuthenticated.value = true
    }
}
