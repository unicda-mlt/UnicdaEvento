package com.main.unicdaevento.screen

import android.app.Activity
import android.util.Patterns
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.entities.UserRole
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.repository.auth.AuthRepository
import com.repository.auth.SignInWithEmailPasswordUseCase
import com.repository.auth.SignInWithGoogleUseCase
import com.repository.auth.SignUpWithEmailPasswordUseCase
import com.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val credManager: CredentialManager,
    private val googleIdOption: GetGoogleIdOption,
    private val signInWithGoogle: SignInWithGoogleUseCase,
    private val signInWithEmailPassword: SignInWithEmailPasswordUseCase,
    private val signUpWithEmailPassword: SignUpWithEmailPasswordUseCase,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data object EmailResetPasswordSent : UiState
        data class SignedIn(val user: FirebaseUser) : UiState
        data class Error(val message: String) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    val currentUser: StateFlow<FirebaseUser?> = authRepository.currentUser

    val currentUserRole: StateFlow<UserRole?> = authRepository.currentUserRole

    private fun String.isValidEmail(): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(this.trim()).matches()

    fun setEmail(value: String) {
        _email.value = value
    }

    fun setPassword(value: String) {
        _password.value = value
    }

    private fun handleEmailCredentialErrorMessage(e: Throwable, defaultMessage: String): String {
        val msg = when (e) {
            is FirebaseAuthInvalidUserException -> "La cuenta no existe o ha sido deshabilitada"
            is FirebaseAuthUserCollisionException -> "El correo electrónico ya está registrado"
            is FirebaseAuthWeakPasswordException -> "La contraseña es demasiado débil"
            is FirebaseAuthEmailException -> "Error con el correo electrónico proporcionado"
            is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos"
            is FirebaseNetworkException -> "Error de conexión. Verifica tu conexión a internet"
            is FirebaseTooManyRequestsException -> "Demasiados intentos fallidos. Intenta nuevamente más tarde"
            else -> defaultMessage
        }

        return msg
    }

    fun signInGoogle(activity: Activity) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val credential = credManager.getCredential(
                    context = activity,
                    request = request
                ).credential

                val googleIdToken = GoogleIdTokenCredential
                    .createFrom(credential.data)
                    .idToken

                val user = signInWithGoogle(googleIdToken)
                _uiState.value = UiState.SignedIn(user)

            } catch (t: Throwable) {
                _uiState.value = UiState.Error(t.message ?: "Sign-in failed")
            }
        }
    }

    fun signInEmail(email: String, password: String) {
        viewModelScope.launch {
            if (email.isEmpty() || !email.isValidEmail() || password.isEmpty()) {
                _uiState.value = UiState.Error("El correo y la contraseña son obligatorios")
                return@launch
            }

            try {
                _uiState.value = UiState.Loading
                val user = signInWithEmailPassword(email, password)
                _uiState.value = UiState.SignedIn(user)
            } catch (e: Exception) {
                val msg = handleEmailCredentialErrorMessage(e, "Ocurrió un error al iniciar sesión")
                _uiState.value = UiState.Error(msg)
            }
        }
    }

    fun signUpEmail(email: String, password: String) {
        viewModelScope.launch {
            if (email.isEmpty() || !email.isValidEmail() || password.isEmpty()) {
                _uiState.value = UiState.Error("El correo y la contraseña son obligatorios")
                return@launch
            }

            try {
                _uiState.value = UiState.Loading
                val user = signUpWithEmailPassword(email, password)
                _uiState.value = UiState.SignedIn(user)
            } catch (e: Throwable) {
                val msg = handleEmailCredentialErrorMessage(
                    e,
                    "Se ha producido un error al intentar registrar el usuario"
                )
                _uiState.value = UiState.Error(msg)
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            if (!email.isValidEmail()) {
                _uiState.value = UiState.Error("El correo eléctronico es obligatorio")
                return@launch
            }

            try {
                _uiState.value = UiState.Loading
                authRepository.sendPasswordReset(email)
                _uiState.value = UiState.EmailResetPasswordSent
            } catch (e: Exception) {
                val msg = handleEmailCredentialErrorMessage(
                    e,
                    "No se pudo enviar el correo de restablecer contraseña"
                )
                _uiState.value = UiState.Error(msg)
            }
        }
    }

    suspend fun getUserRole(userId: String?): UserRole {
        if (userId != null) {
            return userRepository.getRole(userId)
        }

        return UserRole.NO_ROLE
    }
}