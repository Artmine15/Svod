package com.artmine15.svod

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
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
import com.artmine15.svod.viewmodels.NavigationViewModel
import com.artmine15.svod.viewmodels.RoomViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var roomId: String? = ""
        intent?.data?.let { uri ->
            if(uri.scheme == "https" && uri.host == "artmine15.github.io" && uri.path?.startsWith("/Svod/join") == true){
                roomId = uri.getQueryParameter("roomId")
            }
        }
        enableEdgeToEdge()
        setContent {
            SvodTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navigationViewModel: NavigationViewModel = hiltViewModel()
                    val initializationViewModel: InitializationViewModel = hiltViewModel()
                    val roomViewModel: RoomViewModel = hiltViewModel()
                    NavDisplay(
                        modifier = Modifier.padding(innerPadding),
                        backStack = navigationViewModel.backStack,
                        onBack = { navigationViewModel.navigateBack() },
                        entryProvider = entryProvider{
                            entry(CurrentRoomScreenKey) {
                                LaunchedEffect(roomId) {
                                    if(roomId != "")
                                        initializationViewModel.syncLocalCurrentRoomId(roomId ?: "")
                                }
                                LaunchedEffect(Unit) {
                                    initializationViewModel.tryInitializeAuth(
                                        onUserNotAuth = { navigationViewModel.replaceTo(AuthScreenKey, 1000) },
                                        onNoLocalCurrentRoom = {
                                            navigationViewModel.replaceTo(RoomHandlingScreenKey, 1000)
                                        },
                                        onNoUserInCurrentRoom = {
                                            if(roomId == null || roomId == ""){
                                                Log.d("LINK", "FAIL ROOMID: $roomId")

                                                navigationViewModel.replaceTo(RoomHandlingScreenKey, 1000)
                                            }
                                            else{
                                                Log.d("LINK", "JOINING IN: $roomId")
                                                roomViewModel.joinRoomAsUser(
                                                    roomId = roomId,
                                                    onSuccess = { Log.d("LINK", "Successfully JOIN IN: $roomId")},
                                                    onFailure = {}
                                                )
                                            }
                                        },
                                        onSuccess = {},
                                        onFailure = {}
                                    )
                                }

                                Log.d("Main", "Entry CurrentRoomScreenKey")
                                CurrentRoomScreen()
                            }
                            entry(AuthScreenKey) { AuthScreen() }
                            entry(RoomHandlingScreenKey) { RoomHandlingScreen() }
                        }
                    )
                    Box(modifier = Modifier.padding(innerPadding)){
                        Text(
                            text = "userId: ${initializationViewModel.currentUserData.userId}\nroomId: ${initializationViewModel.currentUserData.roomId}\nroomIdLink: $roomId\n${navigationViewModel.backStack.size}\n${navigationViewModel.backStack.map { it }}",
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
