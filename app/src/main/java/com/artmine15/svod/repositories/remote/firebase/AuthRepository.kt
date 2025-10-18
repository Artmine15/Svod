package com.artmine15.svod.repositories.remote.firebase

import android.util.Log
import com.artmine15.svod.LogTags
import com.artmine15.svod.constants.remote.RepositoryConstants
import com.artmine15.svod.repositories.remote.interfaces.AuthHandler
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject

class AuthRepository @Inject constructor() : AuthHandler {
    val db = FirebaseFirestore.getInstance()

    override fun createUser(userName: String, onSuccess: (String) -> Unit, onFailure: (exception: Exception) -> Unit) {
        val userMap = hashMapOf(
            "name" to userName
        )

        db.collection(RepositoryConstants.USERS_COLLECTION)
             .add(userMap)
             .addOnSuccessListener { documentReference ->
                 Log.d("Svod/Debug", "createUser()/User ${documentReference.id} created")
                 onSuccess.invoke(documentReference.id)
             }
             .addOnFailureListener { exception ->
                 Log.d("Svod/Debug", "createUser()/User creation failed. ${exception.toString()}")
                 onFailure.invoke(exception)
             }
    }

    override suspend fun isUserExists(
        userId: String,
        onNoUser: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (exception: Exception) -> Unit,
    ) {
        db.collection(RepositoryConstants.USERS_COLLECTION).document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if(documentSnapshot != null && documentSnapshot.exists()){
                    Log.d(LogTags.svod, "isUserExists()/User $userId exists")
                    onSuccess.invoke()
                }
                else{
                    Log.d(LogTags.svod, "isUserExists()/User $userId do not exists")
                    onNoUser.invoke()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(LogTags.svod, "isUserExists()/${exception.toString()}")
                onFailure(exception)
            }
    }
}