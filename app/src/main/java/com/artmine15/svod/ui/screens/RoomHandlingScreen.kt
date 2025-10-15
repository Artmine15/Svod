package com.artmine15.svod.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.artmine15.svod.RoomCreationScreenKey
import com.artmine15.svod.viewmodels.NavigationViewModel

@Composable
fun RoomHandlingScreen(){
    val navigationViewModel = hiltViewModel<NavigationViewModel>()
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
                onClick = { navigationViewModel.navigateTo(RoomCreationScreenKey) }
            ) {
                Text(
                    text = "Создать класс"
                )
            }
        }
    }
}