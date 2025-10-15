package com.artmine15.svod.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavKey
import com.artmine15.svod.AuthScreenKey
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {
    val backStack = mutableStateListOf<NavKey>((AuthScreenKey))

    var isTemporaryScreensEnabled = false
    var temporaryScreensCount = 0
    var temporaryEndScreenKey: NavKey = AuthScreenKey

    fun navigateTo(key: NavKey){
        backStack.add(key)

        if(isTemporaryScreensEnabled){
            temporaryScreensCount++
            if(backStack.last() == temporaryEndScreenKey){
                for(i in 0 until temporaryScreensCount){
                    backStack.removeLastOrNull()
                }
                isTemporaryScreensEnabled = false
            }
        }
    }

    fun temporaryNavigateTo(startKey: NavKey, endKey: NavKey){
        isTemporaryScreensEnabled = true
        temporaryEndScreenKey = endKey
        navigateTo(startKey)
    }

    fun navigateBack(){
        backStack.removeLastOrNull()
    }
}