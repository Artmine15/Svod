package com.artmine15.svod.ui.screens

import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.artmine15.svod.LogTags
import com.artmine15.svod.R
import com.artmine15.svod.ui.composables.DateNavigation
import com.artmine15.svod.ui.composables.HomeworkTable
import com.artmine15.svod.ui.composables.listContainerRounding
import com.artmine15.svod.viewmodels.DateNavigationViewModel
import com.artmine15.svod.viewmodels.HomeworkViewModel
import com.artmine15.svod.viewmodels.InitializationViewModel
import com.artmine15.svod.viewmodels.LocalUserDataViewModel
import com.artmine15.svod.viewmodels.RoomViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CurrentRoomScreen(){
    val initializationViewModel: InitializationViewModel = hiltViewModel()
    val localUserDataViewModel: LocalUserDataViewModel = hiltViewModel()
    val roomViewModel: RoomViewModel = hiltViewModel()
    val homeworkViewModel: HomeworkViewModel = hiltViewModel()
    val dateNavigationViewModel: DateNavigationViewModel = hiltViewModel()

    val roomIdFromFlow by localUserDataViewModel.roomIdFlow.collectAsState("")

    var isAdmin by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            val context = LocalContext.current

            AnimatedVisibility(
                visible = initializationViewModel.currentUserStates.isInitialized,
                enter = slideInVertically(),
                exit = slideOutVertically()
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Мой класс"
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Присоединяйся к классу в Свод!\nhttps://artmine15.github.io/Svod/join?roomId=${roomIdFromFlow}")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.outline_share_24),
                                contentDescription = "Share"
                            )
                        }
                    },
                    colors = TopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                        subtitleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(4.dp),
            targetState = initializationViewModel.currentUserStates.isInitialized,
            contentAlignment = Alignment.TopCenter
        ) { it ->
            if(it){
                val documentSnapshot by homeworkViewModel.documentSnapshotFlow.collectAsState()

                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DateNavigation(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    LaunchedEffect(dateNavigationViewModel.currentDate) {
                        homeworkViewModel.tryInitializeHomework(
                            date = dateNavigationViewModel.currentDate,
                            onHomeworkExists = {
                            },
                            onSuccess = {
                            },
                            onFailure = {}
                        )
                    }

                    val isVisible = (documentSnapshot?.exists() == true &&
                            documentSnapshot?.id.toString() == dateNavigationViewModel.currentDate.toString())
                    roomViewModel.isUserAdminOfRoom(
                        onSuccess = { isUserAdmin ->
                            isAdmin = isUserAdmin
                        },
                        onFailure = {}
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                                shape = RoundedCornerShape(listContainerRounding)
                            )
                    ){
                        AnimatedContent(
                            targetState = isVisible,
                            transitionSpec = { (fadeIn(tween(1000)))
                                .togetherWith(fadeOut(tween(10))) }
                        ) { state ->
                            if(state){
                                Log.d(LogTags.svod, "${documentSnapshot?.id.toString()} == ${dateNavigationViewModel.currentDate}")
                                HomeworkTable(
                                    date = dateNavigationViewModel.currentDate,
                                    isAdmin = isAdmin,
                                    documentSnapshot = documentSnapshot
                                )
                            }
                            else{
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    LoadingIndicator(modifier = Modifier)
                                }
                            }
                        }
                    }
                }
            }
            else{
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoadingIndicator()
                }
            }
        }
    }
}