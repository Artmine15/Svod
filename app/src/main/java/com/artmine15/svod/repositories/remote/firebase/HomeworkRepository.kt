package com.artmine15.svod.repositories.remote.firebase

import android.util.Log
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

    override fun initializeHomework(roomId: String, date: LocalDate) {
        db.collection("rooms").document(roomId).collection("homeworks").document(date.toString())
            .set(lessonsMap)
            .addOnSuccessListener {
                Log.d("App", "Homework initialized. Date: $date")
            }
            .addOnFailureListener { exception ->
                Log.d("App", "Homework initialization failed. ${exception.toString()}")
            }
    }

    override fun updateField(
        roomId: String,
        date: LocalDate,
        lessonField: Lessons,
        fieldValue: String,
    ) {
        db.collection("rooms").document(roomId).collection("homeworks").document(date.toString())
            .update(lessonField.name, fieldValue)
            .addOnSuccessListener {
                Log.d("App", "Field ${lessonField.name} has new value: $fieldValue")
            }
            .addOnFailureListener { exception ->
                Log.d("App", "Field ${lessonField.name} updating failed. ${exception.toString()}")
            }
    }
}