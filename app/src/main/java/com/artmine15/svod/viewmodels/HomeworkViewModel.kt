package com.artmine15.svod.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artmine15.svod.datastore.LocalUserDataKeys
import com.artmine15.svod.enums.Lessons
import com.artmine15.svod.repositories.datastore.LocalUserDataRepository
import com.artmine15.svod.repositories.remote.firebase.HomeworkRepository
import com.artmine15.svod.repositories.remote.firebase.RoomRepository
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.datetime.LocalDate

class HomeworkViewModel @Inject constructor(
    val localUserDataRepository: LocalUserDataRepository,
    val roomRepository: RoomRepository,
    val homeworkRepository: HomeworkRepository
) : ViewModel() {
    suspend fun updateHomework(
        date: LocalDate,
        lesson: Lessons,
        newValue: String,
        onUserNotAuth: () -> Unit,
        onUserNotInRoom: () -> Unit,
        onUserNotAdmin: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ){
        val currentUserId = viewModelScope.async { return@async localUserDataRepository.getValue(LocalUserDataKeys.USER_ID, "") }.await()
        if(currentUserId == ""){
            onUserNotAuth.invoke()
            return
        }

        val currentRoomId = viewModelScope.async { return@async localUserDataRepository.getValue(LocalUserDataKeys.CURRENT_ROOM_ID, "") }.await()
        if(currentRoomId == ""){
            onUserNotInRoom.invoke()
            return
        }

        var currentAdminUserId = ""
        roomRepository.getAdminIdOfRoom(
            roomId = currentRoomId,
            onSuccess = { adminUserId ->
                currentAdminUserId = adminUserId
            },
            onFailure = {}
        )
        if(currentUserId != currentAdminUserId){
            onUserNotAdmin.invoke()
            return
        }

        homeworkRepository.updateField(
            roomId = currentRoomId,
            date = date,
            lessonField = lesson,
            fieldValue = newValue,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}