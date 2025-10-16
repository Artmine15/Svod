package com.artmine15.svod.repositories.remote.interfaces

interface AuthHandler {
    fun createUser(
        userName: String,
        onSuccess: ((roomId: String) -> Unit),
        onFailure: (exception: Exception) -> Unit
    )

    suspend fun isUserExists(
        userId: String,
        onNoUser: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (exception: Exception) -> Unit
    )
}