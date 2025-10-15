package com.artmine15.svod

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.artmine15.svod.ui.screens.AuthScreen
import com.artmine15.svod.ui.screens.CurrentRoomScreen
import com.artmine15.svod.ui.screens.RoomHandlingScreen
import com.artmine15.svod.ui.theme.SvodTheme
import com.artmine15.svod.viewmodels.NavigationViewModel
import dagger.hilt.android.AndroidEntryPoint

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
                            entry(AuthScreenKey) {
                                AuthScreen()
                            }
                            entry(RoomHandlingScreenKey) {
                                RoomHandlingScreen()
                            }
                            entry(CurrentRoomScreenKey) {
                                CurrentRoomScreen()
                            }
                        }
                    )
                }
            }
        }
    }
}
