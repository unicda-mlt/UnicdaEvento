package com.auth

import com.google.firebase.auth.FirebaseUser


interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun signInWithGoogleIdToken(idToken: String): FirebaseUser
    suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser
    suspend fun signUpWithEmailPassword(email: String, password: String): FirebaseUser
    suspend fun sendPasswordReset(email: String)
    fun signOut()
}