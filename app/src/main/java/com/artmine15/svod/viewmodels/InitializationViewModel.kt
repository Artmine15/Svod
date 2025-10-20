package com.artmine15.svod.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artmine15.svod.LogTags
import com.artmine15.svod.datastore.LocalUserDataKeys
import com.artmine15.svod.model.CurrentUserData
import com.artmine15.svod.model.CurrentUserStates
import com.artmine15.svod.repositories.datastore.LocalUserDataRepository
import com.artmine15.svod.repositories.remote.firebase.AuthRepository
import com.artmine15.svod.repositories.remote.firebase.HomeworkRepository
import com.artmine15.svod.repositories.remote.firebase.RoomRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@HiltViewModel
class InitializationViewModel @Inject constructor(
    val localUserDataRepository: LocalUserDataRepository,
    val authRepository: AuthRepository,
    val roomRepository: RoomRepository,
    val homeworkRepository: HomeworkRepository
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private var currentUserData by mutableStateOf(CurrentUserData(
        userId = "",
        roomId = "",
    ))

    var currentUserStates by mutableStateOf(CurrentUserStates(
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
        if(auth.currentUser?.uid == "") auth.signInAnonymously()

        if(auth.currentUser?.uid != "") {
            viewModelScope.launch {
                currentUserStates = currentUserStates.copy(isInitialized = false)

                currentUserData = currentUserData.copy(
                    userId = localUserDataRepository.getValue(
                        LocalUserDataKeys.USER_ID,
                        ""
                    )
                )

                if (currentUserData.userId != "") {
                    Log.d(LogTags.svod, "tryInitializeAuth()/Local UserId: ${currentUserData.userId}")

                    currentUserData = currentUserData.copy(
                        roomId = localUserDataRepository.getValue(
                            LocalUserDataKeys.ROOM_ID,
                            ""
                        )
                    )
                    if (currentUserData.roomId != "") {
                        Log.d(LogTags.svod, "tryInitializeAuth()/Local CurrentRoomId: ${currentUserData.roomId}")

                        authRepository.isUserExists(
                            userId = currentUserData.userId,
                            onNoUser = {
                                onUserNotAuth.invoke()
                            },
                            onSuccess = {
                                Log.d(LogTags.svod, "tryInitializeAuth()/isUserExists()/onSuccess")
                                viewModelScope.launch {
                                    roomRepository.isUserInRoom(
                                        roomId = currentUserData.roomId,
                                        userId = currentUserData.userId,
                                        onNoRoom = onNoRoom,
                                        onNoUserInRoom = onNoUserInRoom,
                                        onSuccess = {
                                            currentUserStates = currentUserStates.copy(isInitialized = true)
                                            onSuccess.invoke()
                                        },
                                        onFailure = onFailure
                                    )
                                }
                            },
                            onFailure = { exception ->
                                Log.d(LogTags.svod, exception.message.toString())
                                onFailure.invoke(exception)
                            }
                        )
                    }
                    else{
                        onNoLocalRoomId.invoke()
                        Log.d(LogTags.svod, "tryInitializeAuth()/RoomId ${currentUserData.roomId} is invalid. onNoLocalRoomId.invoke(). return")
                        return@launch
                    }
                }
                else{
                    onNoLocalUserId.invoke()
                    Log.d(LogTags.svod, "tryInitializeAuth()/UserId ${currentUserData.userId} is invalid. onNoLocalUserId.invoke(). return")
                    return@launch
                }
            }
        }
        else if(auth.currentUser?.isAnonymous == false) onFailure.invoke(Exception("tryInitializeAuth()/No anonymous registration"))
    }
}