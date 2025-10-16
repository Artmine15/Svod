package com.artmine15.svod.repositories.remote.firebase

import android.util.Log
import com.artmine15.svod.constants.remote.RepositoryConstants
import com.artmine15.svod.enums.Lessons
import com.artmine15.svod.repositories.remote.interfaces.HomeworkHandler
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.datetime.LocalDate

class HomeworkRepository @Inject constructor() : HomeworkHandler {
    val db = FirebaseFirestore.getInstance()

    val lessonsMap = hashMapOf<String, String>().apply {
        for (i in Lessons.entries){
            put(i.name, "")
        }
    }

    override fun initializeHomework(
        roomId: String,
        date: LocalDate,
        onSuccess: () -> Unit,
        onFailure: (exception: Exception) -> Unit
    ) {
        db.collection(RepositoryConstants.ROOMS_COLLECTION).document(roomId).collection(RepositoryConstants.HOMEWORKS_COLLECTION).document(date.toString())
            .set(lessonsMap)
            .addOnSuccessListener {
                Log.d("App", "Homework initialized. Date: $date")
                onSuccess.invoke()
            }
            .addOnFailureListener { exception ->
                Log.d("App", "Homework initialization failed. ${exception.toString()}")
                onFailure.invoke(exception)
            }
    }

    override fun updateField(
        roomId: String,
        date: LocalDate,
        lessonField: Lessons,
        fieldValue: String,
        onSuccess: () -> Unit,
        onFailure: (exception: Exception) -> Unit
    ) {
        val homeworkDocument = db.collection(RepositoryConstants.ROOMS_COLLECTION).document(roomId).collection(RepositoryConstants.HOMEWORKS_COLLECTION).document(date.toString())

        var isInitialized = false
        homeworkDocument
            .get()
            .addOnSuccessListener { documentSnapshot ->
                isInitialized = documentSnapshot != null && documentSnapshot.exists()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
        if(!isInitialized){
            initializeHomework(
                roomId = roomId,
                date = date,
                onSuccess = {
                    isInitialized = true
                },
                onFailure = {}
            )
        }
        if(!isInitialized){
            onFailure.invoke(Exception(""))
            return
        }

        homeworkDocument
            .update(lessonField.name, fieldValue)
            .addOnSuccessListener {
                Log.d("App", "Field ${lessonField.name} has new value: $fieldValue")
                onSuccess.invoke()
            }
            .addOnFailureListener { exception ->
                Log.d("App", "Field ${lessonField.name} updating failed. ${exception.toString()}")
                onFailure.invoke(exception)
            }
    }
}