package com.artmine15.svod.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.artmine15.svod.LogTags
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toKotlinLocalDate
import java.time.ZonedDateTime

@HiltViewModel
class DateNavigationViewModel @Inject constructor() : ViewModel() {
    var currentDate: LocalDate by mutableStateOf(ZonedDateTime.now().toLocalDate().toKotlinLocalDate())

    fun switchToNextDay(){
        currentDate = currentDate.plus(1, DateTimeUnit.DAY)
        Log.d(LogTags.svod, "switchToNextDay()/New day: $currentDate")
    }

    fun switchToPreviousDay(){
        currentDate = currentDate.minus(1, DateTimeUnit.DAY)
        Log.d(LogTags.svod, "switchToPreviousDay()/New day: $currentDate")
    }
}