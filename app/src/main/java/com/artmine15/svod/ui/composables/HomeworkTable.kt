package com.artmine15.svod.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.artmine15.svod.enums.Lessons
import com.artmine15.svod.viewmodels.HomeworkViewModel
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.datetime.LocalDate

val lessonsMap = mapOf(
    Lessons.RUSSIAN to "Русский язык",
    Lessons.ALGEBRA to "Алгебра",
    Lessons.GEOMETRY to "Геометрия",
    Lessons.MATH to "Математика",
    Lessons.ICT to "Информатика",
    null to ""
)

@Composable
fun HomeworkTable(
    modifier: Modifier = Modifier,
    date: LocalDate
){
    val homeworkViewModel: HomeworkViewModel = hiltViewModel()
    val documentSnapshot by homeworkViewModel.documentSnapshotFlow.collectAsState(null)

    Row(
        modifier = modifier
    ) {
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight(),
            thickness = 4.dp,
            color = MaterialTheme.colorScheme.surfaceContainer
        )
        LazyColumn {
            items(Lessons.entries, key = { it.name }){ lesson ->
                HomeworkTableCell(
                    lesson = lesson,
                    date = date,
                    isAdmin = true,
                    homeworkViewModel = homeworkViewModel,
                    documentSnapshot = documentSnapshot
                )
            }
        }
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight(),
            thickness = 4.dp,
            color = MaterialTheme.colorScheme.surfaceContainer
        )
    }
}

@Composable
fun HomeworkTableCell(
    lesson: Lessons,
    date: LocalDate,
    isAdmin: Boolean,
    homeworkViewModel: HomeworkViewModel,
    documentSnapshot: DocumentSnapshot?,
){
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ){
        Text(
            text = lessonsMap[lesson] ?: ""
        )
        VerticalDivider(
            modifier = Modifier
                .height(50.dp),
            thickness = 50.dp,
            color = MaterialTheme.colorScheme.surfaceContainer
        )

        val descriptionText = documentSnapshot?.getString(lesson.name) ?: ""

        if(isAdmin){
            val textFieldState = rememberTextFieldState(descriptionText)
            TextField(
                modifier = Modifier
                    .onFocusChanged { focusState ->
                        if(!focusState.isFocused && descriptionText != textFieldState.text.toString()){
                            homeworkViewModel.updateHomework(
                                date = date,
                                lesson = lesson,
                                newValue = textFieldState.text.toString(),
                                onSuccess = {},
                                onFailure = {}
                            )
                        }
                    },
                state = textFieldState,
            )
        }
        else{
            Text(
                text = descriptionText
            )
        }
    }
}