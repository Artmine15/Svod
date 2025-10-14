package com.artmine15.svod.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artmine15.svod.datastore.LocalUserDataKeys
import com.artmine15.svod.repositories.datastore.LocalUserDataRepository
import com.artmine15.svod.repositories.remote.firebase.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    val authRepository: AuthRepository,
    val localUserDataRepository: LocalUserDataRepository
) : ViewModel() {
    fun createNewUser(
        name: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ){
        authRepository.createUser(
            name,
            onSuccess = { userId ->
                viewModelScope.launch {
                    localUserDataRepository.saveValue(LocalUserDataKeys.USER_ID, userId)
                }
                onSuccess.invoke()
            },
            onFailure = onFailure
        )
    }
}