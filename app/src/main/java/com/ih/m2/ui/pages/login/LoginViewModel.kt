package com.ih.m2.ui.pages.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ih.m2.core.ui.viewmodel.BaseViewModel
import com.ih.m2.domain.usecase.LoginUseCase
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) {


//    data  class UiState(
//        val name: String = ""
//    )
//
//    sealed class Action {
//        data class Login(val email: String, val password: String): Action()
//    }
//
//    override fun processImpl(action: Action) {
//        when(action) {
//            is Action.Login -> handleLogin(action.email, action.password)
//        }
//    }
//
//    private fun handleLogin(email: String, password: String) {
//        viewModelScope.launch {
//            kotlin.runCatching {
//                loginUseCase(email, password)
//            }.onSuccess {
//                Timber.e("Test $it")
//            }.onFailure {
//                Timber.e("Test  error$it")
//            }
//        }
//    }
}

