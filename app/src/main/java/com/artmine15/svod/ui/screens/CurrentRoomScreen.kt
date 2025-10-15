package com.artmine15.svod.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.artmine15.svod.datastore.LocalUserDataKeys
import com.artmine15.svod.viewmodels.AuthViewModel
import kotlinx.coroutines.async

@Composable
fun CurrentRoomScreen(){
    val localUserDataRepository = hiltViewModel<AuthViewModel>().localUserDataRepository
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var roomId by remember { mutableStateOf("") }
            LaunchedEffect(this) {
                roomId = scope.async {
                    return@async localUserDataRepository.getValue(LocalUserDataKeys.CURRENT_ROOM_ID, "")
                }.await()
            }

            Text(
                text = roomId
            )
        }
    }
}