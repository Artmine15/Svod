package com.artmine15.svod.models

data class CurrentUserData(
    val userId: String,
    val roomId: String,
    val isInitialized: Boolean
)
