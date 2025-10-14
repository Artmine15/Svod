package com.artmine15.svod.repositories.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LocalUserDataRepository @Inject constructor(val context: Context) {
    val Context.dataStore by preferencesDataStore("local_user_data")

    suspend fun<T> saveValue(key: Preferences.Key<T>, value: T){
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun<T> getValue(key: Preferences.Key<T>) : T? {
        return context.dataStore.data.map { preferences ->
            preferences[key]
        }.first()
    }

    suspend fun<T> getValue(key: Preferences.Key<T>, initial: T) : T {
        return context.dataStore.data.map { preferences ->
            preferences[key]
        }.first() ?: initial
    }
}