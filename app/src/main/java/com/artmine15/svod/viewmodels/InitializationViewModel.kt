package com.artmine15.svod.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        onUserNotAuth: () -> Unit,
        onNoLocalCurrentRoom: () -> Unit,
        onNoUserInCurrentRoom: () -> Unit,
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
                    onUserNotAuth.invoke()
                    return@launch
                }
                Log.d("tryInitializeAuth()", "Local UserId: ${currentUserData.userId}")

                currentUserData = currentUserData.copy(
                    roomId = localUserDataRepository.getValue(
                        LocalUserDataKeys.CURRENT_ROOM_ID,
                        ""
                    )
                )
                if (currentUserData.roomId == "") {
                    onNoLocalCurrentRoom.invoke()
                    Log.d("tryInitializeAuth()", "onNoCurrentRoom, return")
                    return@launch
                }
                Log.d("tryInitializeAuth()", "Local CurrentRoomId: ${currentUserData.roomId}")

                authRepository.isUserExists(
                    userId = currentUserData.userId,
                    onNoUser = {
                        Log.d("tryInitializeAuth()", "onNoCurrentRoom")
                        onUserNotAuth.invoke()
                    },
                    onSuccess = {
                        Log.d("tryInitializeAuth()", "User exists")
                        viewModelScope.launch {
                            roomRepository.isUserInRoom(
                                roomId = currentUserData.roomId,
                                userId = currentUserData.userId,
                                onNoRoom = {
                                    Log.d("tryInitializeAuth()", "onNoCurrentRoom")
                                    onNoLocalCurrentRoom.invoke()
                                },
                                onNoUserInRoom = {
                                    Log.d("tryInitializeAuth()", "onNoCurrentRoom")
                                    onNoUserInCurrentRoom.invoke()
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
                        Log.d("tryInitializeAuth()", exception.message.toString())
                        onFailure.invoke(exception)
                    }
                )
            }
        }
        else if(auth.currentUser?.isAnonymous == false) onFailure.invoke(Exception("No anonymous registration"))
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

    suspend fun syncLocalCurrentRoomId(){
        currentUserData = currentUserData.copy(
            userId = localUserDataRepository.getValue(
                key = LocalUserDataKeys.CURRENT_ROOM_ID,
                initial = ""
            )
        )
    }

    suspend fun syncLocalCurrentRoomId(newValue: String){
        localUserDataRepository.saveValue(LocalUserDataKeys.CURRENT_ROOM_ID, newValue)
        currentUserData = currentUserData.copy(userId = newValue)
    }
}