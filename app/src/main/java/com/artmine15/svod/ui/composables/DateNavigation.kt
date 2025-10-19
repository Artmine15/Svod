package com.artmine15.svod.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.artmine15.svod.R
import com.artmine15.svod.viewmodels.DateNavigationViewModel
import com.artmine15.svod.viewmodels.HomeworkViewModel

val russianDayOfWeek = mapOf(
    "MONDAY" to "Понедельник",
    "TUESDAY" to "Вторник",
    "WEDNESDAY" to "Среда",
    "THURSDAY" to "Четверг",
    "FRIDAY" to "Пятница",
    "SATURDAY" to "Суббота",
    "SUNDAY" to "Воскресенье"
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DateNavigation(
    modifier: Modifier = Modifier,
    ){
    val dateNavigationViewModel: DateNavigationViewModel = hiltViewModel()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                dateNavigationViewModel.switchToPreviousDay()
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_arrow_back_24),
                contentDescription = "Previous date"
            )
        }
        Text(
            text = "${dateNavigationViewModel.currentDate}\n${russianDayOfWeek.getValue(dateNavigationViewModel.currentDate.dayOfWeek.name)}",
            textAlign = TextAlign.Center
        )
        IconButton(
            onClick = {
                dateNavigationViewModel.switchToNextDay()
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_arrow_forward_24),
                contentDescription = "Next date"
            )
        }
    }
}