package com.artmine15.svod.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

object LocalUserDataKeys {
    val USER_ID = stringPreferencesKey("user_id")
    val CURRENT_ROOM_ID = stringPreferencesKey("current_room_id")
}