package com.artmine15.svod.viewmodels

import androidx.lifecycle.ViewModel
import com.artmine15.svod.repositories.firebase.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(val authRepository: AuthRepository) : ViewModel() {
    fun getName() : String {
        return authRepository.authenticateUser(" ")
    }
}