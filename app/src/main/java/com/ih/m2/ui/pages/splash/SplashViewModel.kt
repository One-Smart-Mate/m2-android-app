package com.ih.m2.ui.pages.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ih.m2.core.FileHelper
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.usecase.user.GetUserUseCase
import com.ih.m2.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val coroutineContext: CoroutineContext,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _startRoute = MutableStateFlow(Screen.Login.route)
    val startRoute = _startRoute.asStateFlow()

    init {
        handleGetUser()
    }


    private fun handleGetUser() {
        viewModelScope.launch(coroutineContext) {
            kotlin.runCatching {
                getUserUseCase()
            }.onSuccess {
                val route = if (it != null) {
                   // Screen.Home.route
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

    private suspend fun navigateToScreen(route: String) {
        _startRoute.value = route
        delay(2000)
        _isAuthenticated.value = true
    }

}