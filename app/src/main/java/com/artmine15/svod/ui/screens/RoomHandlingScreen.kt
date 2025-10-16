package com.artmine15.svod.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.artmine15.svod.AuthScreenKey
import com.artmine15.svod.CurrentRoomScreenKey
import com.artmine15.svod.viewmodels.InitializationViewModel
import com.artmine15.svod.viewmodels.NavigationViewModel
import com.artmine15.svod.viewmodels.RoomViewModel
import kotlinx.coroutines.launch

@Composable
fun RoomHandlingScreen(){
    val navigationViewModel: NavigationViewModel = hiltViewModel()
    val roomViewModel: RoomViewModel = hiltViewModel()
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
            Text(
                text = "Войдите в класс по ссылке, либо создайте свой:"
            )
            Button(
                onClick = {
                    scope.launch {
                        roomViewModel.createRoomAsAdmin(
                            onUserNotAuth = { navigationViewModel.navigateTo(AuthScreenKey) },
                            onSuccess = {
                                roomViewModel.joinRoomAsUser(
                                    onSuccess = { navigationViewModel.replaceTo(CurrentRoomScreenKey, 1000) },
                                    onFailure = {}
                                )
                            },
                            onFailure = {  }
                        )
                    }
                }
            ) {
                Text(
                    text = "Создать класс"
                )
            }
        }
    }
}