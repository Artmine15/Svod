package com.artmine15.svod.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.artmine15.svod.CurrentRoomScreenKey
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {
    val backStack = mutableStateListOf<NavKey>((CurrentRoomScreenKey))

    private var removingJob: Job? = null

    private var isTemporaryScreensEnabled = false
    private var temporaryScreensCount = 0
    private var temporaryEndScreenKey: NavKey = CurrentRoomScreenKey

    fun navigateTo(key: NavKey){
        if(key == backStack.last()) return

        if(isTemporaryScreensEnabled){
            temporaryScreensCount++
            if(backStack.last() == temporaryEndScreenKey){
                for(i in 0 until temporaryScreensCount){
                    backStack.removeLastOrNull()
                }
                isTemporaryScreensEnabled = false
            }
        }

        backStack.add(key)
    }

    fun replaceTo(key: NavKey, removeDelayMillis: Long){
        removingJob = viewModelScope.launch {
            val keyToRemove = backStack.last()
            if(key == keyToRemove) return@launch

            if(isTemporaryScreensEnabled){
                temporaryScreensCount++
                if(backStack.last() == temporaryEndScreenKey){
                    for(i in 0 until temporaryScreensCount){
                        backStack.removeLastOrNull()
                    }
                    isTemporaryScreensEnabled = false
                }
            }

            backStack.add(key)
            delay(removeDelayMillis)
            for(i in backStack.size - 1 downTo 0){
                if(backStack[i] == keyToRemove)
                    backStack.removeAt(i)
            }
        }
    }

    fun temporaryNavigateTo(startKey: NavKey, endKey: NavKey){
        isTemporaryScreensEnabled = true
        temporaryEndScreenKey = endKey
        navigateTo(startKey)
    }

    fun refreshCurrentScreen(removeDelayMillis: Long){
        removingJob = viewModelScope.launch {
            val currentScreenKey = backStack.last()
            backStack.add(currentScreenKey)
            backStack.removeAt(backStack.size - 2)

            backStack.add(currentScreenKey)
            delay(removeDelayMillis)
            backStack.remove(currentScreenKey)
        }
    }

    fun navigateBack(){
        removingJob?.cancel()
        backStack.removeLastOrNull()
    }
}