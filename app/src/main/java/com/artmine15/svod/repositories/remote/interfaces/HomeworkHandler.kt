package com.artmine15.svod.repositories.remote.interfaces

import com.artmine15.svod.enums.Lessons
import kotlinx.datetime.LocalDate

interface HomeworkHandler {
    fun initializeHomework(
        roomId: String,
        date: LocalDate,
        onSuccess: () -> Unit,
        onFailure: (exception: Exception) -> Unit
    )
    fun updateField(
        roomId: String,
        date: LocalDate,
        lessonField: Lessons,
        fieldValue: String,
        onSuccess: () -> Unit,
        onFailure: (exception: Exception) -> Unit
    )
}