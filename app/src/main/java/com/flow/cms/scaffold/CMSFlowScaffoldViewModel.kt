package com.flow.cms.scaffold

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class CMSFlowScaffoldViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val currentUser: StateFlow<FirebaseUser?> = authRepository.currentUser
}