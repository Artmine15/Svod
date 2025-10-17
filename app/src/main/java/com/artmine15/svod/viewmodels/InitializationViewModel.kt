package com.artmine15.svod.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artmine15.svod.LogTags
import com.artmine15.svod.datastore.LocalUserDataKeys
import com.artmine15.svod.models.CurrentUserData
import com.artmine15.svod.repositories.datastore.LocalUserDataRepository
import com.artmine15.svod.repositories.remote.firebase.AuthRepository
import com.artmine15.svod.repositories.remote.firebase.RoomRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class InitializationViewModel @Inject constructor(
    val localUserDataRepository: LocalUserDataRepository,
    val authRepository: AuthRepository,
    val roomRepository: RoomRepository,
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    var currentUserData by mutableStateOf(CurrentUserData(
        userId = "",
        roomId = "",
        isInitialized = false
    ))

    fun tryInitializeAuth(
        onNoLocalUserId: () -> Unit,
        onNoLocalRoomId: () -> Unit,
        onUserNotAuth: () -> Unit,
        onNoRoom: () -> Unit,
        onNoUserInRoom: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (exception: Exception) -> Unit
    ){
        if(currentUserData.isInitialized) return

        if(auth.currentUser?.uid == "") auth.signInAnonymously()

        if(auth.currentUser?.uid != "") {
            viewModelScope.launch {
                currentUserData = currentUserData.copy(
                    userId = localUserDataRepository.getValue(
                        LocalUserDataKeys.USER_ID,
                        ""
                    )
                )
                if (currentUserData.userId == "") {
                    onNoLocalUserId.invoke()
                    Log.d(LogTags.debug, "tryInitializeAuth()/UserId ${currentUserData.userId} is invalid. onNoLocalUserId.invoke(). return")
                    return@launch
                }
                Log.d(LogTags.debug, "tryInitializeAuth()/Local UserId: ${currentUserData.userId}")

                currentUserData = currentUserData.copy(
                    roomId = localUserDataRepository.getValue(
                        LocalUserDataKeys.ROOM_ID,
                        ""
                    )
                )
                if (currentUserData.roomId == "") {
                    onNoLocalRoomId.invoke()
                    Log.d(LogTags.debug, "tryInitializeAuth()/RoomId ${currentUserData.roomId} is invalid. onNoLocalRoomId.invoke(). return")
                    return@launch
                }
                Log.d(LogTags.debug, "tryInitializeAuth()/Local CurrentRoomId: ${currentUserData.roomId}")

                authRepository.isUserExists(
                    userId = currentUserData.userId,
                    onNoUser = {
                        onUserNotAuth.invoke()
                    },
                    onSuccess = {
                        Log.d(LogTags.debug, "tryInitializeAuth()/isUserExists()/onSuccess")
                        viewModelScope.launch {
                            roomRepository.isUserInRoom(
                                roomId = currentUserData.roomId,
                                userId = currentUserData.userId,
                                onNoRoom = {
                                    onNoRoom.invoke()
                                },
                                onNoUserInRoom = {
                                    onNoUserInRoom.invoke()
                                },
                                onSuccess = {
                                    currentUserData = currentUserData.copy(isInitialized = true)
                                    onSuccess.invoke()
                                },
                                onFailure = onFailure
                            )
                        }
                    },
                    onFailure = { exception ->
                        Log.d(LogTags.debug, exception.message.toString())
                        onFailure.invoke(exception)
                    }
                )
            }
        }
        else if(auth.currentUser?.isAnonymous == false) onFailure.invoke(Exception("tryInitializeAuth()/No anonymous registration"))
    }

    suspend fun syncLocalUserId(){
        currentUserData = currentUserData.copy(
            userId = localUserDataRepository.getValue(
                key = LocalUserDataKeys.USER_ID,
                initial = ""
            )
        )
    }

    suspend fun syncLocalUserId(newValue: String){
        localUserDataRepository.saveValue(LocalUserDataKeys.USER_ID, newValue)
        currentUserData = currentUserData.copy(userId = newValue)
    }

    suspend fun syncLocalRoomId(){
        currentUserData = currentUserData.copy(
            userId = localUserDataRepository.getValue(
                key = LocalUserDataKeys.ROOM_ID,
                initial = ""
            )
        )
    }

    suspend fun syncLocalRoomId(newValue: String){
        localUserDataRepository.saveValue(LocalUserDataKeys.ROOM_ID, newValue)
        currentUserData = currentUserData.copy(roomId = newValue)
    }
}