package com.artmine15.svod.viewmodels

import androidx.lifecycle.ViewModel
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
    var currentDate: LocalDate = ZonedDateTime.now().toLocalDate().toKotlinLocalDate()

    fun switchToNextDay(){
        currentDate = currentDate.plus(1, DateTimeUnit.DAY)
    }

    fun switchToPreviousDay(){
        currentDate = currentDate.minus(1, DateTimeUnit.DAY)
    }
}