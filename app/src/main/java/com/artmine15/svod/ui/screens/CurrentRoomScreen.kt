package com.artmine15.svod.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.artmine15.svod.AuthScreenKey
import com.artmine15.svod.CurrentRoomScreenKey
import com.artmine15.svod.RoomHandlingScreenKey
import com.artmine15.svod.viewmodels.InitializationViewModel
import com.artmine15.svod.viewmodels.LocalUserDataViewModel
import com.artmine15.svod.viewmodels.NavigationViewModel
import kotlinx.coroutines.launch

@Composable
fun CurrentRoomScreen(){
    val initializationViewModel: InitializationViewModel = hiltViewModel()
    val localUserDataViewModel: LocalUserDataViewModel = hiltViewModel()

    val roomIdState by localUserDataViewModel.roomIdFlow.collectAsState("")

    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if(initializationViewModel.currentUserStates.isInitialized){
                Text(
                    text = roomIdState
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