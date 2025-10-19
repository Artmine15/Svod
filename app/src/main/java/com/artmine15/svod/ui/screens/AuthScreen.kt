package com.artmine15.svod.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.artmine15.svod.CurrentRoomScreenKey
import com.artmine15.svod.viewmodels.AuthViewModel
import com.artmine15.svod.viewmodels.NavigationViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AuthScreen(){
    val authViewModel = hiltViewModel<AuthViewModel>()
    val navigationViewModel = hiltViewModel<NavigationViewModel>()

    var firstNameTextFieldValue by remember { mutableStateOf("") }
    var secondNameTextFieldValue by remember { mutableStateOf("") }

    var isRegistering by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                text = "Как тебя зовут?",
                style = MaterialTheme.typography.displaySmall,
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = firstNameTextFieldValue,
                onValueChange = {
                    if(it.length < 20)
                        firstNameTextFieldValue = it
                },
                label = { Text("Имя") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = secondNameTextFieldValue,
                onValueChange = {
                    if(it.length < 20)
                        secondNameTextFieldValue = it
                },
                label = { Text("Фамилия") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedContent(
                targetState = isRegistering,
                transitionSpec = { (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut()) },
                contentAlignment = Alignment.Center
            ) { state ->
                if(state){
                    LoadingIndicator()
                }
                else{
                    Button(
                        modifier = Modifier,
                        enabled = firstNameTextFieldValue != "" && secondNameTextFieldValue != "" && !isRegistering,
                        onClick = {
                            isRegistering = true
                            authViewModel.createNewUser(
                                name = "${firstNameTextFieldValue.trim()} ${secondNameTextFieldValue.trim()}",
                                onSuccess = {
                                    navigationViewModel.replaceTo(CurrentRoomScreenKey, 1000)
                                },
                                onFailure = { isRegistering = false }
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
    }
}