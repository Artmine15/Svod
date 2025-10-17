package com.artmine15.svod.repositories.datastore

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewModelScope
import com.artmine15.svod.datastore.LocalUserDataKeys
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
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

    fun<T> getValueFlow(key: Preferences.Key<T>) : Flow<T?> {
        return context.dataStore.data.map { preferences ->
            preferences[key]
        }
    }

    fun<T> getValueFlow(key: Preferences.Key<T>, initial: T) : Flow<T> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: initial
        }
    }

    @Composable
    fun<T> getValueState(key: Preferences.Key<T>, initial: T) : State<T> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: initial
        }.collectAsState(initial)
    }

    suspend fun<T> isValueExists(key: Preferences.Key<T>, initial: T) : Boolean {
        val value = getValue(key, initial)

        return value != initial
    }

    suspend fun<T> isValueExists(key: Preferences.Key<T>, initial: T, onValueChecked: (isExists: Boolean) -> Unit) {
        val value = getValue(key, initial)

        if(value == initial){
            onValueChecked.invoke(false)
            return
        }

        onValueChecked.invoke(true)
    }
}