package com.domain.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resumeWithException


class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun signInWithGoogleIdToken(idToken: String): FirebaseUser =
        suspendCancellableCoroutine { cont ->
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnSuccessListener { cont.resume(requireNotNull(auth.currentUser)) { cause, _, _ -> } }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }

    override suspend fun signInWithEmailPassword(
        email: String,
        password: String
    ): FirebaseUser = suspendCancellableCoroutine { cont ->
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { cont.resume(requireNotNull(auth.currentUser)) { cause, _, _ -> } }
            .addOnFailureListener { cont.resumeWithException(it) }
    }

    override suspend fun signUpWithEmailPassword(
        email: String,
        password: String
    ): FirebaseUser = suspendCancellableCoroutine { cont ->
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { cont.resume(requireNotNull(auth.currentUser)) { cause, _, _ -> } }
            .addOnFailureListener { cont.resumeWithException(it) }
    }

    override suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override fun signOut() {
        auth.signOut()
    }
}
