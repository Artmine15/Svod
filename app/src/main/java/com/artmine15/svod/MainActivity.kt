package com.artmine15.svod

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.artmine15.svod.ui.screens.AuthenticationScreen
import com.artmine15.svod.ui.theme.SvodTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SvodTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val backStack = remember { mutableStateListOf<NavKey>(AuthenticationScreenKey) }
                    NavDisplay(
                        modifier = Modifier.padding(innerPadding),
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        entryProvider = entryProvider{
                            entry(AuthenticationScreenKey) {
                                AuthenticationScreen()
                            }
                        }
                    )
                }
            }
        }
    }
}
