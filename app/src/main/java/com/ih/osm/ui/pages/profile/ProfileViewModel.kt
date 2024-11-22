package com.ih.osm.ui.pages.profile

import androidx.lifecycle.viewModelScope
import com.ih.osm.core.ui.LCE
import com.ih.osm.domain.model.User
import com.ih.osm.domain.usecase.user.GetUserUseCase
import com.ih.osm.ui.extensions.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : BaseViewModel<ProfileViewModel.UiState>(UiState()) {

    data class UiState(
        val state: LCE<User> = LCE.Loading
    )

    init {
        handleGetUser()
    }

    private fun handleGetUser() {
        setState { copy(state = LCE.Loading) }
        viewModelScope.launch {
            kotlin.runCatching {
                callUseCase { getUserUseCase() }
            }.onSuccess {
                it?.let {
                    setState { copy(state = LCE.Success(it)) }
                }
            }.onFailure {
                setState { copy(state = LCE.Fail(it.localizedMessage.orEmpty())) }
            }
        }
    }
}
