package com.flow.cms.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun signOut() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            authRepository.signOut()
            _uiState.value = UiState.Idle
        }
    }
}
