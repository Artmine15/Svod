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
        onFailure: (exception: Exception) -> Unit
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
                    localUserDataRepository.saveValue(LocalUserDataKeys.ROOM_ID, roomId)
                    onSuccess.invoke()
                }
            },
            onFailure = onFailure
        )
    }

    fun joinRoomAsUser(
        onSuccess: () -> Unit,
        onFailure: (exception: Exception) -> Unit
    ){
        viewModelScope.launch {
            val userId = viewModelScope.async {
                return@async localUserDataRepository.getValue(LocalUserDataKeys.USER_ID, "")
            }.await()
            if(userId == "") {
                onFailure.invoke(Exception("UserId is empty"))
                return@launch
            }

            val roomId = viewModelScope.async {
                return@async localUserDataRepository.getValue(LocalUserDataKeys.ROOM_ID, "")
            }.await()
            if(roomId == "") {
                onFailure.invoke(Exception("RoomId is empty"))
                return@launch
            }

            roomRepository.joinRoomAsUser(
                userId = userId,
                roomId = roomId,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }

    fun joinRoomAsUser(
        roomId: String,
        onSuccess: () -> Unit,
        onFailure: (exception: Exception) -> Unit
    ){
        if(roomId == "") {
            onFailure.invoke(Exception("RoomId is empty"))
            return
        }
        viewModelScope.launch {
            val userId = viewModelScope.async {
                return@async localUserDataRepository.getValue(LocalUserDataKeys.USER_ID, "")
            }.await()
            if(userId == "") {
                onFailure.invoke(Exception("UserId is empty"))
                return@launch
            }

            localUserDataRepository.saveValue(LocalUserDataKeys.ROOM_ID, roomId)

            roomRepository.joinRoomAsUser(
                userId = userId,
                roomId = roomId,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }
}