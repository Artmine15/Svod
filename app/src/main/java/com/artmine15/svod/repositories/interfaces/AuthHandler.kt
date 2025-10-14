package com.artmine15.svod.repositories.interfaces

interface AuthHandler {
    fun createUser(userName: String, onSuccess: ((roomId: String) -> Unit))
}