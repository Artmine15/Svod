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

    fun navigateTo(key: NavKey){
        backStack.add(key)
    }

    fun navigateBack(){
        backStack.removeLastOrNull()
    }
}