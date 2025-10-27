package com.domain.auth

import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject


class SignInWithGoogleUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(idToken: String): FirebaseUser =
        repo.signInWithGoogleIdToken(idToken)
}