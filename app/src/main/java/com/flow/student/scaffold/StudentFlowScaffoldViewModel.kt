package com.flow.student.scaffold

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class StudentFlowScaffoldViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _auth = FirebaseAuth.getInstance()

    private val _user = MutableStateFlow(authRepository.currentUser)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _user.value = firebaseAuth.currentUser
    }

    init {
        _auth.addAuthStateListener(authListener)
    }

    override fun onCleared() {
        _auth.removeAuthStateListener(authListener)
    }
}