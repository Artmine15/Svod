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
            if(key == backStack.last()) return@launch

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
            backStack.removeAt(backStack.size - 2)
        }
    }

    fun temporaryNavigateTo(startKey: NavKey, endKey: NavKey){
        isTemporaryScreensEnabled = true
        temporaryEndScreenKey = endKey
        navigateTo(startKey)
    }

    fun navigateBack(){
        removingJob?.cancel()
        backStack.removeLastOrNull()
    }
}