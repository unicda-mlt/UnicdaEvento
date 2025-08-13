package com.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.database.dao.StudentDao
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginScreenUIState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class LoginScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginScreenUIState())
    val uiState: StateFlow<LoginScreenUIState> = _uiState

    private val _studentRegistrationId = MutableStateFlow("")
    val studentRegistrationId: StateFlow<String> = _studentRegistrationId

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    fun login(studentDao: StudentDao) {
        viewModelScope.launch {
            _uiState.value = LoginScreenUIState(loading = true)

            // MUST BE DELETED IN PRODUCTION //
            delay(1_000)
            // MUST BE DELETED IN PRODUCTION //

            val user = studentDao.findByRegistrationId(_studentRegistrationId.value)
            if (user == null || user.password != _password.value) {
                _uiState.value = LoginScreenUIState(error = "Invalid credentials")
            } else {
                _uiState.value = LoginScreenUIState(success = true)
            }
        }
    }

    fun setStudentRegistrationId(value: String) {
        _studentRegistrationId.value = value
    }

    fun setPassword(value: String) {
        _password.value = value
    }
}