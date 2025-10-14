package com.artmine15.svod.repositories.firebase

import android.util.Log
import com.artmine15.svod.repositories.interfaces.AuthHandler
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject

class AuthRepository @Inject constructor() : AuthHandler {
    val db = FirebaseFirestore.getInstance()

    override fun createUser(userName: String, onSuccess: ((userId: String) -> Unit)) {
        val userMap = hashMapOf(
            "name" to userName
        )

        db.collection("users")
             .add(userMap)
             .addOnSuccessListener { documentReference ->
                 onSuccess.invoke(documentReference.id)
                 Log.d("App", "Success with id ${documentReference.id}")
             }
             .addOnFailureListener { exception ->
                 Log.d("App", "User creation failed. ${exception.toString()}")
             }
    }
}