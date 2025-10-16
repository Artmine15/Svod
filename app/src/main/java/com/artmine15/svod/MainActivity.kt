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
import com.artmine15.svod.viewmodels.NavigationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SvodTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navigationViewModel = hiltViewModel<NavigationViewModel>()
                    NavDisplay(
                        modifier = Modifier.padding(innerPadding),
                        backStack = navigationViewModel.backStack,
                        onBack = { navigationViewModel.navigateBack() },
                        entryProvider = entryProvider{
                            entry(CurrentRoomScreenKey) {
                                val initializationViewModel = hiltViewModel<InitializationViewModel>()

                                LaunchedEffect(Unit) {
                                    initializationViewModel.tryInitializeAuth(
                                        onUserNotAuth = { navigationViewModel.replaceTo(AuthScreenKey, 1000) },
                                        onNoCurrentRoom = { navigationViewModel.replaceTo(RoomHandlingScreenKey, 1000) },
                                        onNoUserInCurrentRoom = { navigationViewModel.replaceTo(RoomHandlingScreenKey, 1000)  },
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
                            text = "${navigationViewModel.backStack.map { it }}\n${navigationViewModel.backStack.size}",
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
