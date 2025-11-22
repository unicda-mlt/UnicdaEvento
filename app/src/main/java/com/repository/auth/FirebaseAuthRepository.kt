package com.repository.auth

import com.di.ApplicationScope
import com.domain.entities.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.repository.user.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resumeWithException


class FirebaseAuthRepository(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    @ApplicationScope private val externalScope: CoroutineScope
) : AuthRepository {
    private val _currentUser = MutableStateFlow(auth.currentUser)
    override val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val _currentUserRole = MutableStateFlow<UserRole?>(null)
    override val currentUserRole: StateFlow<UserRole?> = _currentUserRole

    private val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _currentUser.value = firebaseAuth.currentUser
        val userUID = firebaseAuth.uid

        if (userUID == null) {
            _currentUserRole.value = null
        }
        else {
            externalScope.launch {
                _currentUserRole.value = userRepository.getRole(userUID)
            }
        }
    }

    init {
        auth.addAuthStateListener(listener)
    }

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
