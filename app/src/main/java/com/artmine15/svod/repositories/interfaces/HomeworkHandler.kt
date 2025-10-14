package com.artmine15.svod.repositories.interfaces

import kotlinx.datetime.LocalDate

interface HomeworkHandler {
    fun initializeHomework(roomId: String, date: LocalDate)
    fun updateField(roomId: String, date: LocalDate, fieldName: String, fieldValue: String)
}