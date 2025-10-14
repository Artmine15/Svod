package com.artmine15.svod.repositories.remote.firebase

import android.util.Log
import com.artmine15.svod.repositories.remote.interfaces.RoomHandler
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlin.collections.listOf

class RoomRepository @Inject constructor() : RoomHandler {
    val db = FirebaseFirestore.getInstance()

    override fun joinRoomAsUser(roomId: String, userId: String, onSuccess: () -> Unit) {
        val roomDocument = db.collection("rooms").document(roomId)

        roomDocument.update("users", FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
                Log.d("App", "User $userId successfully added")
                onSuccess.invoke()
            }
            .addOnFailureListener { exception ->
                Log.d("App", "User $userId not added. ${exception.toString()}")
            }
    }

    override fun createRoomAsAdmin(adminUserId: String, onSuccess: (roomId: String) -> Unit) {
        val usersMap = hashMapOf(
            "users" to listOf(adminUserId)
        )

        db.collection("rooms").add(usersMap)
            .addOnSuccessListener { documentReference ->
                onSuccess.invoke(documentReference.id)
                Log.d("App", "Room creation success, id: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.d("App", "Room creation failed. ${exception.toString()}")
            }
    }
}