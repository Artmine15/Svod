package com.artmine15.svod.repositories.remote.interfaces

interface RoomHandler {
    fun joinRoomAsUser(
        roomId: String,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )

    fun createRoomAsAdmin(
        adminUserId: String,
        onSuccess: (roomId: String) -> Unit,
        onFailure: () -> Unit
    )
}