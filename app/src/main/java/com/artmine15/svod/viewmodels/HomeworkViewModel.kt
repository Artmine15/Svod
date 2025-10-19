package com.artmine15.svod.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artmine15.svod.LogTags
import com.artmine15.svod.datastore.LocalUserDataKeys
import com.artmine15.svod.enums.Lessons
import com.artmine15.svod.repositories.datastore.LocalUserDataRepository
import com.artmine15.svod.repositories.remote.firebase.HomeworkRepository
import com.artmine15.svod.repositories.remote.firebase.RoomRepository
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@HiltViewModel
class HomeworkViewModel @Inject constructor(
    val localUserDataRepository: LocalUserDataRepository,
    val homeworkRepository: HomeworkRepository
) : ViewModel() {
    val documentSnapshotFlow = homeworkRepository.homeworkDocumentSnapshotFlow

    fun updateHomework(
        date: LocalDate,
        lesson: Lessons,
        newValue: String,
        onSuccess: () -> Unit,
        onFailure: (exception: Exception) -> Unit
    ){
        viewModelScope.launch {
            val roomId = localUserDataRepository.getValue(LocalUserDataKeys.ROOM_ID, "")

            homeworkRepository.updateField(
                roomId = roomId,
                date = date,
                lessonField = lesson,
                fieldValue = newValue,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }

    fun tryInitializeHomework(
        date: LocalDate,
        onHomeworkExists: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ){
        viewModelScope.launch {
            val roomId = localUserDataRepository.getValue(LocalUserDataKeys.ROOM_ID, "")

            homeworkRepository.tryInitializeHomework(
                roomId = roomId,
                date = date,
                onHomeworkExists = onHomeworkExists,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }
}