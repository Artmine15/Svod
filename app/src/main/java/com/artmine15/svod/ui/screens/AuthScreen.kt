package com.artmine15.svod.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.artmine15.svod.RoomHandlingScreenKey
import com.artmine15.svod.viewmodels.AuthViewModel
import com.artmine15.svod.viewmodels.NavigationViewModel

@Preview
@Composable
fun AuthScreen(){
    val authViewModel = hiltViewModel<AuthViewModel>()
    val navigationViewModel = hiltViewModel<NavigationViewModel>()

    val textFieldState = rememberTextFieldState("")
    var textFieldSupportTextState by remember { mutableStateOf("Error") }
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                text = "Как тебя зовут?",
                style = MaterialTheme.typography.displaySmall,
            )
            TextField(
                state = textFieldState,
                supportingText = {
                    Text(
                        text = textFieldSupportTextState
                    )
                },
                lineLimits = TextFieldLineLimits.SingleLine
            )
            Button(
                modifier = Modifier,
                onClick = {
                    authViewModel.createNewUser(
                        name = textFieldState.text.toString(),
                        onSuccess = { navigationViewModel.navigateTo(RoomHandlingScreenKey) },
                        onFailure = { textFieldSupportTextState = "Errorrrr" }
                    )
                }
            ) {
                Text(
                    text = "Зарегистрироваться"
                )
            }
        }
    }
}