package com.ih.m2.core.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Utilize [savedStateHandle] to:
 * 1. Automatically retrieve Fragment arguments or Activity Intent extras.
 * 2. Store ViewModel state that have strong guarantees against process death equivalent to
 * Fragment arguments and Activity Intent extras.
 */
abstract class BaseViewModel<STATE : Any, ACTION : Any>(
    initialState: STATE,
) : ViewModel() {
    private val uiState = MutableStateFlow(initialState)
    private lateinit var savedStateHandle: SavedStateHandle
    private val savedStateHandleSoftLock = CompletableDeferred<Unit>()
    private val internalViewModelLogger: Timber.Tree
        get() = Timber.tag(this::class.java.simpleName)

    fun getStateFlow(): StateFlow<STATE> = uiState.asStateFlow()

    fun getStateValue(): STATE = getStateFlow().value

    protected fun setState(updater: STATE.() -> STATE) {
        uiState.update { currState ->
            updater.invoke(currState).also { newState ->
                internalViewModelLogger.i("New state: $newState")
            }
        }
    }

    fun process(action: ACTION) {
        internalViewModelLogger.i("Received action $action")
        processImpl(action)
    }

    protected abstract fun processImpl(action: ACTION)

    fun setSavedState(savedStateHandle: SavedStateHandle) {
        this.savedStateHandle = savedStateHandle
        savedStateHandleSoftLock.complete(Unit)
    }

    protected suspend fun awaitSavedStateHandle(): SavedStateHandle {
        savedStateHandleSoftLock.await()
        return savedStateHandle
    }

    protected fun acquireSavedStateHandle(savedStateHandleBlock: SavedStateHandle.() -> Unit) {
        viewModelScope.launch {
            savedStateHandleSoftLock.await()
            savedStateHandleBlock(savedStateHandle)
        }
    }
}
