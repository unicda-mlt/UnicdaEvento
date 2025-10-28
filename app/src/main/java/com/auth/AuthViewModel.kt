package com.auth

import android.app.Activity
import android.util.Patterns
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val credManager: CredentialManager,
    private val googleIdOption: GetGoogleIdOption,
    private val signInWithGoogle: SignInWithGoogleUseCase,
    private val signInWithEmailPassword: SignInWithEmailPasswordUseCase,
    private val signUpWithEmailPassword: SignUpWithEmailPasswordUseCase,
    private val repo: AuthRepository
) : ViewModel() {

    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data object EmailResetPasswordSent : UiState
        data class SignedIn(val user: FirebaseUser) : UiState
        data class Error(val message: String) : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state

    private fun String.isValidEmail(): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(this.trim()).matches()

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

    fun currentUserOrNull(): FirebaseUser? = repo.currentUser

    fun signInGoogle(activity: Activity) {
        viewModelScope.launch {
            try {
                _state.value = UiState.Loading

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
                _state.value = UiState.SignedIn(user)

            } catch (t: Throwable) {
                _state.value = UiState.Error(t.message ?: "Sign-in failed")
            }
        }
    }

    fun signInEmail(email: String, password: String) {
        viewModelScope.launch {
            if (email.isEmpty() || !email.isValidEmail() || password.isEmpty()) {
                _state.value = UiState.Error("El correo y la contraseña son obligatorios")
                return@launch
            }

            try {
                _state.value = UiState.Loading
                val user = signInWithEmailPassword(email, password)
                _state.value = UiState.SignedIn(user)
            } catch (e: Exception) {
                val msg = handleEmailCredentialErrorMessage(e, "Ocurrió un error al iniciar sesión")
                _state.value = UiState.Error(msg)
            }
        }
    }

    fun signUpEmail(email: String, password: String) {
        viewModelScope.launch {
            if (email.isEmpty() || !email.isValidEmail() || password.isEmpty()) {
                _state.value = UiState.Error("El correo y la contraseña son obligatorios")
                return@launch
            }

            try {
                _state.value = UiState.Loading
                val user = signUpWithEmailPassword(email, password)
                _state.value = UiState.SignedIn(user)
            } catch (e: Throwable) {
                val msg = handleEmailCredentialErrorMessage(
                    e,
                    "Se ha producido un error al intentar registrar el usuario"
                )
                _state.value = UiState.Error(msg)
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            if (!email.isValidEmail()) {
                _state.value = UiState.Error("El correo eléctronico es obligatorio")
                return@launch
            }

            try {
                _state.value = UiState.Loading
                repo.sendPasswordReset(email)
                _state.value = UiState.EmailResetPasswordSent
            } catch (e: Exception) {
                val msg = handleEmailCredentialErrorMessage(
                    e,
                    "No se pudo enviar el correo de restablecer contraseña"
                )
                _state.value = UiState.Error(msg)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            repo.signOut()
            delay(500)
            _state.value = UiState.Idle
        }
    }
}
