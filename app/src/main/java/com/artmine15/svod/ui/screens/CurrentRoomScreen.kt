package com.artmine15.svod.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.artmine15.svod.ui.composables.HomeworkTable
import com.artmine15.svod.viewmodels.InitializationViewModel
import com.artmine15.svod.viewmodels.LocalUserDataViewModel
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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

            AnimatedContent(
                targetState = initializationViewModel.currentUserStates.isInitialized
            ) { it ->
                if(it){
                    HomeworkTable(
                        modifier = Modifier.padding(16.dp),
                        date = LocalDate.parse("2025-04-25")
                    )
                    Text(
                        text = roomIdState
                    )
                }
                else{
                    LoadingIndicator(modifier = Modifier)
                }
            }
        }
    }
}