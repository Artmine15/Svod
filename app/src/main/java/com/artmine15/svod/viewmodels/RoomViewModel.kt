package com.artmine15.svod.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artmine15.svod.datastore.LocalUserDataKeys
import com.artmine15.svod.repositories.datastore.LocalUserDataRepository
import com.artmine15.svod.repositories.remote.firebase.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@HiltViewModel
class RoomViewModel @Inject constructor(
    val localUserDataRepository: LocalUserDataRepository,
    val roomRepository: RoomRepository
) : ViewModel() {
    suspend fun createRoomAsAdmin(
        onUserNotAuth: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ){
        val adminUserId = viewModelScope.async {
            return@async localUserDataRepository.getValue(LocalUserDataKeys.USER_ID, "")
        }.await()

        if(adminUserId == "") {
            onUserNotAuth.invoke()
            return
        }

        roomRepository.createRoomAsAdmin(
            adminUserId = adminUserId,
            onSuccess = { roomId ->
                viewModelScope.launch {
                    localUserDataRepository.saveValue(LocalUserDataKeys.CURRENT_ROOM_ID, roomId)
                    onSuccess.invoke()
                }
            },
            onFailure = onFailure
        )
    }

    suspend fun joinRoomAsUser(
        onRoomDoNotHaveCurrentRoom: () -> Unit,
        onUserNotAuth: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ){
        val roomId = viewModelScope.async {
            return@async localUserDataRepository.getValue(LocalUserDataKeys.CURRENT_ROOM_ID, "")
        }.await()

        if(roomId == "") {
            onRoomDoNotHaveCurrentRoom.invoke()
            return
        }

        val userId = viewModelScope.async {
            return@async localUserDataRepository.getValue(LocalUserDataKeys.USER_ID, "")
        }.await()

        if(userId == "") {
            onUserNotAuth.invoke()
            return
        }

        roomRepository.joinRoomAsUser(
            roomId = roomId,
            userId = userId,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}