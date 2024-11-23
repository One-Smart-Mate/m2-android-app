package com.ih.osm.ui.extensions

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

abstract class BaseViewModel<S : Any>(
    initialState: S
) : ViewModel() {

    // StateFlow to hold the state of the UI
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state

    // The setState method that updates the state
    protected fun setState(update: S.() -> S) {
        _state.value = update(_state.value)
    }

    suspend fun <T> callUseCase(call: suspend () -> T): T {
        return withContext(Dispatchers.IO) { call() }
    }

    fun getState() = _state.value
}