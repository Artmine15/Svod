package com.artmine15.svod.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.artmine15.svod.AuthScreenKey
import com.artmine15.svod.CurrentRoomScreenKey
import com.artmine15.svod.viewmodels.InitializationViewModel
import com.artmine15.svod.viewmodels.NavigationViewModel
import com.artmine15.svod.viewmodels.RoomViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RoomHandlingScreen(){
    val navigationViewModel: NavigationViewModel = hiltViewModel()
    val roomViewModel: RoomViewModel = hiltViewModel()

    var isCreating by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Войдите в класс по ссылке\nили",
                textAlign = TextAlign.Center
            )
            AnimatedContent(
                targetState = isCreating,
                transitionSpec = { (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut()) },
                contentAlignment = Alignment.Center
            ) { state ->
                if(state){
                    LoadingIndicator()
                }
                else{
                    Button(
                        modifier = Modifier,
                        enabled = !isCreating,
                        onClick = {
                            isCreating = true
                            roomViewModel.createRoomAsAdmin(
                                onUserNotAuth = { navigationViewModel.navigateTo(AuthScreenKey) },
                                onSuccess = {
                                    roomViewModel.joinRoomAsUser(
                                        onSuccess = { navigationViewModel.replaceTo(CurrentRoomScreenKey, 1000) },
                                        onFailure = {}
                                    )
                                },
                                onFailure = { isCreating = false }
                            )
                        }
                    ) {
                        Text(
                            text = "Создать класс"
                        )
                    }
                }
            }
        }
    }
}