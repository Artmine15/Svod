package com.artmine15.svod.repositories.remote.firebase

import android.util.Log
import com.artmine15.svod.LogTags
import com.artmine15.svod.constants.remote.RepositoryConstants
import com.artmine15.svod.enums.Lessons
import com.artmine15.svod.repositories.remote.interfaces.HomeworkHandler
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.datetime.LocalDate

class HomeworkRepository @Inject constructor() : HomeworkHandler {
    val db = FirebaseFirestore.getInstance()

    val lessonsMap = hashMapOf<String, String>().apply {
        for (i in Lessons.entries){
            put(i.name, "")
        }
    }

    val homeworkDocumentSnapshotFlow = MutableStateFlow<DocumentSnapshot?>(null)
    var listenerRegistration: ListenerRegistration? = null

    override fun updateField(
        roomId: String,
        date: LocalDate,
        lessonField: Lessons,
        fieldValue: String,
        onSuccess: () -> Unit,
        onFailure: (exception: Exception) -> Unit
    ) {
        val homeworkDocument = db.collection(RepositoryConstants.ROOMS_COLLECTION).document(roomId).collection(RepositoryConstants.HOMEWORKS_COLLECTION).document(date.toString())

        fun updateFieldData(){
            homeworkDocument
                .update(lessonField.name, fieldValue)
                .addOnSuccessListener {
                    Log.d(LogTags.svod, "updateField()/Field ${lessonField.name} has new value: $fieldValue")
                    onSuccess.invoke()
                }
                .addOnFailureListener { exception ->
                    Log.d(LogTags.svod, "updateField()/Field ${lessonField.name} updating failed. ${exception.toString()}")
                    onFailure.invoke(exception)
                }
        }

        homeworkDocument
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if(documentSnapshot != null && documentSnapshot.exists()){
                    updateFieldData()
                }
                else{
                    onFailure(Exception("updateFieldData()/No document snapshot"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    override fun tryInitializeHomework(
        roomId: String,
        date: LocalDate,
        onHomeworkExists: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (exception: Exception) -> Unit
    ){
        val homeworkDocument = db.collection(RepositoryConstants.ROOMS_COLLECTION).document(roomId).collection(RepositoryConstants.HOMEWORKS_COLLECTION).document(date.toString())


        homeworkDocument
            .get()
            .addOnSuccessListener { documentSnapshot ->

                if(documentSnapshot != null && !documentSnapshot.exists()){
                    homeworkDocument
                        .set(lessonsMap)
                        .addOnSuccessListener {
                            Log.d(LogTags.svod, "initializeHomework()/Homework initialized. Date: $date")
                            updateDocumentSnapshotFlow(
                                roomId = roomId,
                                date = date
                            )
                            onSuccess.invoke()
                        }
                        .addOnFailureListener { exception ->
                            Log.d(LogTags.svod, "initializeHomework()/Homework initialization failed. ${exception.toString()}")
                            onFailure.invoke(exception)
                        }
                }
                else{
                    Log.d(LogTags.svod, "initializeHomework()/Homework exists and already initialized. Date: $date")
                    updateDocumentSnapshotFlow(
                        roomId = roomId,
                        date = date
                    )
                    onHomeworkExists.invoke()
                }
            }
            .addOnFailureListener { exception ->
                onFailure.invoke(exception)
            }
    }

     override fun updateDocumentSnapshotFlow(
        roomId: String,
        date: LocalDate,
    ){
         val document = db.collection(RepositoryConstants.ROOMS_COLLECTION).document(roomId).collection(RepositoryConstants.HOMEWORKS_COLLECTION).document(date.toString())
         listenerRegistration?.remove()

         listenerRegistration = document
             .addSnapshotListener { documentSnapshot, error ->
                 if(error != null || documentSnapshot == null || !documentSnapshot.exists()){
                     Log.d(LogTags.svod, "getDocumentFlow()/Error: $error")
                     return@addSnapshotListener
                 }
                 homeworkDocumentSnapshotFlow.value = documentSnapshot
                 Log.d(LogTags.svod, "getDocumentFlow()/trySend $roomId ${documentSnapshot?.id}")
             }
         /*
         document
             .get()
             .addOnSuccessListener { documentSnapshot ->
                 if(documentSnapshot != null && documentSnapshot.exists()){
                     Log.d(LogTags.svod, "getDocumentFlow()/trySend ${documentSnapshot.id}")
                     trySend(documentSnapshot)
                 }
             }

          */



    }
}