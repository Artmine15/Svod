package com.artmine15.svod.repositories.remote.interfaces

interface RoomHandler {
    fun createRoomAsAdmin(
        adminUserId: String,
        onSuccess: (roomId: String) -> Unit,
        onFailure: (exception: Exception) -> Unit
    )

    fun getAdminIdOfRoom(
        roomId: String,
        onSuccess: (adminUserId: String) -> Unit,
        onFailure: (exception: Exception) -> Unit
    )

    fun joinRoomAsUser(
        userId: String,
        roomId: String,
        onSuccess: () -> Unit,
        onFailure: (exception: Exception) -> Unit
    )

    suspend fun isUserInRoom(
        userId: String,
        roomId: String,
        onNoRoom: () -> Unit,
        onNoUserInRoom: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (exception: Exception) -> Unit
    )
}