package com.artmine15.svod.viewmodels

import androidx.lifecycle.ViewModel
import com.artmine15.svod.datastore.LocalUserDataKeys
import com.artmine15.svod.repositories.datastore.LocalUserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class LocalUserDataViewModel @Inject constructor(
    val localUserDataRepository: LocalUserDataRepository
) : ViewModel() {
    val userIdFlow = localUserDataRepository.getValueFlow(LocalUserDataKeys.USER_ID, "")
    val roomIdFlow = localUserDataRepository.getValueFlow(LocalUserDataKeys.ROOM_ID, "")

    suspend fun resetUserLocalData(){
        localUserDataRepository.saveValue(LocalUserDataKeys.USER_ID, "")
        localUserDataRepository.saveValue(LocalUserDataKeys.ROOM_ID, "")
    }

    suspend fun saveNewRoomId(newValue: String){
        localUserDataRepository.saveValue(LocalUserDataKeys.ROOM_ID, newValue)
    }
}