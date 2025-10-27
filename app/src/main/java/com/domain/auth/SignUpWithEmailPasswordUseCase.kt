package com.domain.auth

import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject


class SignUpWithEmailPasswordUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): FirebaseUser =
        repo.signUpWithEmailPassword(email, password)
}