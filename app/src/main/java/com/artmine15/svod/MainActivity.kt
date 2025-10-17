package com.artmine15.svod

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.artmine15.svod.ui.screens.AuthScreen
import com.artmine15.svod.ui.screens.CurrentRoomScreen
import com.artmine15.svod.ui.screens.RoomHandlingScreen
import com.artmine15.svod.ui.theme.SvodTheme
import com.artmine15.svod.viewmodels.InitializationViewModel
import com.artmine15.svod.viewmodels.LocalUserDataViewModel
import com.artmine15.svod.viewmodels.NavigationViewModel
import com.artmine15.svod.viewmodels.RoomViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var linkRoomId: String? = ""
        intent?.data?.let { uri ->
            if(uri.scheme == "https" && uri.host == "artmine15.github.io" && uri.path?.startsWith("/Svod/join") == true){
                linkRoomId = uri.getQueryParameter("roomId")
            }
        }

        enableEdgeToEdge()
        setContent {
            SvodTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navigationViewModel: NavigationViewModel = hiltViewModel()
                    val initializationViewModel: InitializationViewModel = hiltViewModel()
                    val roomViewModel: RoomViewModel = hiltViewModel()
                    val localUserDataViewModel: LocalUserDataViewModel = hiltViewModel()
                    val linkRoomIdState by remember { mutableStateOf(linkRoomId) }

                    val replacementTime: Long = 5000

                    LaunchedEffect(linkRoomIdState) {
                        if(linkRoomIdState != ""){
                            localUserDataViewModel.saveNewRoomId(linkRoomIdState ?: "")
                        }
                    }
                    NavDisplay(
                        modifier = Modifier.padding(innerPadding),
                        backStack = navigationViewModel.backStack,
                        onBack = { navigationViewModel.navigateBack() },
                        entryProvider = entryProvider{
                            entry(CurrentRoomScreenKey) {
                                LaunchedEffect( Unit) {
                                    Log.d(LogTags.debug, "NavEntry/tryInitializeAuth()")
                                    initializationViewModel.tryInitializeAuth(
                                        onNoLocalUserId = { navigationViewModel.replaceTo(AuthScreenKey, replacementTime) },
                                        onNoLocalRoomId = { navigationViewModel.replaceTo(RoomHandlingScreenKey, replacementTime) },
                                        onUserNotAuth = { navigationViewModel.replaceTo(AuthScreenKey, replacementTime) },
                                        onNoRoom = { navigationViewModel.replaceTo(RoomHandlingScreenKey, replacementTime) },
                                        onNoUserInRoom = {
                                            if(linkRoomIdState == null || linkRoomIdState == ""){
                                                Log.d(LogTags.debug, "onNoUserInRoom->Link/Invalid linkRoomId: $linkRoomIdState")
                                            }
                                            else{
                                                Log.d(LogTags.debug, "onNoUserInRoom->Link/Joining in: $linkRoomIdState")
                                                roomViewModel.joinRoomAsUser(
                                                    roomId = linkRoomIdState ?: "",
                                                    onSuccess = {
                                                        Log.d(LogTags.debug, "onNoUserInRoom->Link/Should refresh current screen")
                                                        recreate()
                                                    },
                                                    onFailure = {}
                                                )
                                            }
                                        },
                                        onSuccess = {},
                                        onFailure = {}
                                    )
                                }

                                CurrentRoomScreen()
                            }
                            entry(AuthScreenKey) { AuthScreen() }
                            entry(RoomHandlingScreenKey) { RoomHandlingScreen() }
                        }
                    )
                    Box(modifier = Modifier.padding(innerPadding)){
                        val scope = rememberCoroutineScope()

                        val userIdState by localUserDataViewModel.userIdFlow.collectAsState("")
                        val roomIdState by localUserDataViewModel.roomIdFlow.collectAsState("")

                        Column {
                            Text(
                                text = "userId: ${userIdState}\nroomId: ${roomIdState}\nroomIdLink: $linkRoomIdState\n${navigationViewModel.backStack.size}\n${navigationViewModel.backStack.map { it }}\nisInitialized: ${initializationViewModel.currentUserStates.isInitialized}",
                                fontSize = 10.sp
                            )
                            Button(
                                onClick = {
                                    scope.launch {
                                        localUserDataViewModel.resetUserLocalData()
                                    }
                                }
                            ) {
                                Text(
                                    text = "Clear"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
