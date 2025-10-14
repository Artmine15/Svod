package com.artmine15.svod.repositories.remote.interfaces

interface AuthHandler {
    fun createUser(userName: String, onSuccess: ((roomId: String) -> Unit))
}