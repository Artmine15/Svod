package com.artmine15.svod.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.artmine15.svod.AuthScreenKey
import com.artmine15.svod.CurrentRoomScreenKey
import com.artmine15.svod.RoomHandlingScreenKey
import com.artmine15.svod.viewmodels.InitializationViewModel
import com.artmine15.svod.viewmodels.NavigationViewModel
import kotlinx.coroutines.launch

@Composable
fun CurrentRoomScreen(){
    val navigationViewModel: NavigationViewModel = hiltViewModel()
    val initializationViewModel: InitializationViewModel = hiltViewModel()

    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if(initializationViewModel.currentUserData.isInitialized){
                Text(
                    text = initializationViewModel.currentUserData.roomId + " ${navigationViewModel.backStack.size}"
                )
            }
            else{
                Text(
                    text = "Загрузка... наверное"
                )
            }
        }
    }
}