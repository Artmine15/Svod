package com.artmine15.svod.ui.composables

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.artmine15.svod.LogTags
import com.artmine15.svod.R
import com.artmine15.svod.enums.Lessons
import com.artmine15.svod.viewmodels.HomeworkViewModel
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate

val lessonLabelsMap = mapOf(
    Lessons.RUSSIAN to "Русский язык",
    Lessons.LITERATURE to "Литература",
    Lessons.ALGEBRA to "Алгебра",
    Lessons.GEOMETRY to "Геометрия",
    Lessons.PROBABILITY_AND_STATISTICS to "Вероятность и статистика",
    Lessons.MATH to "Математика",
    Lessons.HISTORY to "История",
    Lessons.SOCIAL_SCIENCE to "Обществознание",
    Lessons.CHEMISTRY to "Химия",
    Lessons.BIOLOGY to "Биология",
    Lessons.GEOGRAPHY to "География",
    Lessons.ENGLISH to "Английский язык",
    Lessons.PHYSICS to "Физика",
    Lessons.ICT to "Информатика",
    Lessons.READING_LITERACY to "Читательская грамотность",
    null to ""
)

val noLessonsEmojiSet = setOf("🤗", "🗿", "😋", "💅", "🫡", "😌", "😇", "🥰", "🤩")

val listContainerRounding = 28.dp
val listContainerPadding = 12.dp
val itemRounding = listContainerRounding - listContainerPadding
val itemPadding = 8.dp
val itemDescriptionRounding = itemRounding - itemPadding
val itemDescriptionPadding = 8.dp

@Composable
fun HomeworkLazyColumn(
    modifier: Modifier = Modifier,
    date: LocalDate,
    isAdmin: Boolean,
    documentSnapshot: DocumentSnapshot?,
){
    val homeworkViewModel: HomeworkViewModel = hiltViewModel()

    Log.d(LogTags.svod, "HomeworkTable composed: ${documentSnapshot?.id}")

    Box(
        modifier = modifier
    ){


        val lessonDescriptionMap = linkedMapOf<Lessons, String>().apply {
            for (lesson in Lessons.entries){
                val descriptionText = documentSnapshot?.getString(lesson.name) ?: ""
                if(isAdmin){
                    put(lesson, descriptionText)
                }
                else{
                    if(descriptionText != "")
                        put(lesson, descriptionText)
                }
            }
        }

        if(lessonDescriptionMap.isNotEmpty()){
            val visibleStates = remember { mutableStateListOf<Boolean>().apply { repeat(lessonDescriptionMap.size) { add(false) } } }

            LaunchedEffect(Unit) {
                lessonDescriptionMap.entries.indices.forEach { i ->
                    delay(i * 5L)
                    if (i < visibleStates.size) {
                        visibleStates[i] = true
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.clip(RoundedCornerShape(listContainerRounding)),
                verticalArrangement = Arrangement.spacedBy(listContainerPadding / 2),
                contentPadding = PaddingValues(listContainerPadding)
            ) {
                itemsIndexed(lessonDescriptionMap.toList(), key = { _, lesson -> lesson.first.name }){ index, lesson ->
                    AnimatedVisibility(
                        visible = visibleStates[index],
                        enter = fadeIn(
                            tween(500)
                        ) + slideInVertically(
                            spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + scaleIn(
                            spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                    ) {
                        HomeworkItem(
                            lesson = lesson.first,
                            date = date,
                            isAdmin = isAdmin,
                            homeworkViewModel = homeworkViewModel,
                            lessonDescription = lesson.second
                        )
                    }
                }
            }
        }
        else{
            Text(
                modifier = Modifier
                    .padding(listContainerPadding)
                    .align(Alignment.Center),
                text = "Домашнего задания нет${noLessonsEmojiSet.random()}"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeworkItem(
    modifier: Modifier = Modifier,
    lesson: Lessons,
    date: LocalDate,
    isAdmin: Boolean,
    homeworkViewModel: HomeworkViewModel,
    lessonDescription: String,
){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(itemRounding)
            )
            .padding(itemPadding),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = lessonLabelsMap[lesson] ?: "",
            style = MaterialTheme.typography.titleLargeEmphasized,
            fontWeight = FontWeight.Bold
        )
        Spacer(
            modifier.height(4.dp)
        )

        val descriptionText = lessonDescription

        if(isAdmin){
            var textFieldValue by remember(descriptionText) { mutableStateOf(descriptionText) }
            var isFocused by remember { mutableStateOf(false)}
            val localFocusManager = LocalFocusManager.current

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                        if(!focusState.isFocused && descriptionText != textFieldValue){
                            homeworkViewModel.updateHomework(
                                date = date,
                                lesson = lesson,
                                newValue = textFieldValue,
                                onSuccess = {},
                                onFailure = {}
                            )
                        }
                    },
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = isFocused,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        IconButton(
                            onClick = { localFocusManager.clearFocus() },
                            colors = IconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                disabledContentColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.outline_check_24),
                                contentDescription = "Done"
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(itemDescriptionRounding),
                textStyle = MaterialTheme.typography.bodySmall
            )
        }
        else{
            Text(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = RoundedCornerShape(itemDescriptionRounding)
                    )
                    .padding(itemDescriptionPadding),
                text = descriptionText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
