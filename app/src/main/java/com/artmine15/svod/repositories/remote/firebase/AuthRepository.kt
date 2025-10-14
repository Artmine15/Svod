package com.artmine15.svod.repositories.remote.firebase

import android.util.Log
import com.artmine15.svod.repositories.remote.interfaces.AuthHandler
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject

class AuthRepository @Inject constructor() : AuthHandler {
    val db = FirebaseFirestore.getInstance()

    override fun createUser(userName: String, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        val userMap = hashMapOf(
            "name" to userName
        )

        db.collection("users")
             .add(userMap)
             .addOnSuccessListener { documentReference ->
                 Log.d("App", "Success with id ${documentReference.id}")
                 onSuccess.invoke(documentReference.id)
             }
             .addOnFailureListener { exception ->
                 Log.d("App", "User creation failed. ${exception.toString()}")
                 onFailure.invoke()
             }
    }
}