package com.repository.auth

import com.domain.entities.UserRole
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow


interface AuthRepository {
    val currentUser: StateFlow<FirebaseUser?>
    val currentUserRole: StateFlow<UserRole?>
    suspend fun signInWithGoogleIdToken(idToken: String): FirebaseUser
    suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser
    suspend fun signUpWithEmailPassword(email: String, password: String): FirebaseUser
    suspend fun sendPasswordReset(email: String)
    fun signOut()
}