package com.artmine15.svod.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artmine15.svod.datastore.LocalUserDataKeys
import com.artmine15.svod.repositories.datastore.LocalUserDataRepository
import com.artmine15.svod.repositories.remote.firebase.RoomRepository
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class RoomViewModel @Inject constructor(
    val localUserDataRepository: LocalUserDataRepository,
    val roomRepository: RoomRepository
) : ViewModel() {
    fun createRoomAsAdmin(adminUserId: String){
        roomRepository.createRoomAsAdmin(
            adminUserId = adminUserId,
            onSuccess = { roomId ->
                viewModelScope.launch {
                    localUserDataRepository.saveValue(LocalUserDataKeys.CURRENT_ROOM_ID, roomId)
                }
            }
        )
    }

    fun joinRoomAsUser(roomId: String, userId: String, ){
        roomRepository.joinRoomAsUser(
            roomId = roomId,
            userId = userId,
            onSuccess = { }
        )
    }
}