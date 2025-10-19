package com.artmine15.svod.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.artmine15.svod.LogTags
import com.artmine15.svod.ui.composables.DateNavigation
import com.artmine15.svod.ui.composables.HomeworkTable
import com.artmine15.svod.viewmodels.DateNavigationViewModel
import com.artmine15.svod.viewmodels.HomeworkViewModel
import com.artmine15.svod.viewmodels.InitializationViewModel
import com.artmine15.svod.viewmodels.LocalUserDataViewModel
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CurrentRoomScreen(){
    val initializationViewModel: InitializationViewModel = hiltViewModel()
    val localUserDataViewModel: LocalUserDataViewModel = hiltViewModel()
    val homeworkViewModel: HomeworkViewModel = hiltViewModel()
    val dateNavigationViewModel: DateNavigationViewModel = hiltViewModel()

    val roomIdState by localUserDataViewModel.roomIdFlow.collectAsState("")

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AnimatedContent(
            targetState = initializationViewModel.currentUserStates.isInitialized
        ) { it ->
            if(it){
                var currentDate by remember { mutableStateOf(dateNavigationViewModel.currentDate) }
                val documentSnapshot by homeworkViewModel.documentSnapshotFlow.collectAsState()

                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DateNavigation(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    LaunchedEffect(dateNavigationViewModel.currentDate) {


                        homeworkViewModel.tryInitializeHomework(
                            date = dateNavigationViewModel.currentDate,
                            onHomeworkExists = {
                                currentDate = dateNavigationViewModel.currentDate
                            },
                            onSuccess = {
                                currentDate = dateNavigationViewModel.currentDate
                            },
                            onFailure = {}
                        )
                    }


                    if(documentSnapshot?.exists() == true &&
                        documentSnapshot?.id == currentDate.toString()
                        ){
                        Log.d(LogTags.svod, "${documentSnapshot?.id} != $currentDate")
                        HomeworkTable(
                            modifier = Modifier.padding(16.dp),
                            date = currentDate,
                            documentSnapshot = documentSnapshot
                        )
                    }

                    Text(
                        text = roomIdState
                    )
                }
            }
            else{
                LoadingIndicator(modifier = Modifier)
            }
        }
    }
}