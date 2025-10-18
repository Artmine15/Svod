package com.artmine15.svod.repositories.remote.interfaces

import com.artmine15.svod.enums.Lessons
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface HomeworkHandler {
    fun tryInitializeHomework(
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

    fun getDocumentFlow(
        roomId: String,
        date: LocalDate,
    ) : Flow<DocumentSnapshot?>
}