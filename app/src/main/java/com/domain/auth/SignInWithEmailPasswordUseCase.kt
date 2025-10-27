package com.domain.auth

import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject


class SignInWithEmailPasswordUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): FirebaseUser =
        repo.signInWithEmailPassword(email, password)
}