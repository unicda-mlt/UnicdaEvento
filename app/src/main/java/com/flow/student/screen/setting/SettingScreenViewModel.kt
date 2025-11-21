package com.flow.student.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingScreenViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {
    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
    }

    private val _uiState = MutableStateFlow<SettingScreenViewModel.UiState>(SettingScreenViewModel.UiState.Idle)
    val uiState: StateFlow<SettingScreenViewModel.UiState> = _uiState

    fun currentUserOrNull(): FirebaseUser? = repo.currentUser

    fun signOut() {
        viewModelScope.launch {
            _uiState.value = SettingScreenViewModel.UiState.Loading
            repo.signOut()
            delay(500)
            _uiState.value = SettingScreenViewModel.UiState.Idle
        }
    }
}