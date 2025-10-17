package com.artmine15.svod.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

object LocalUserDataKeys {
    val USER_ID = stringPreferencesKey("user_id")
    val ROOM_ID = stringPreferencesKey("room_id")
}