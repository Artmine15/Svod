package com.artmine15.svod.repositories.remote.firebase

import android.util.Log
import com.artmine15.svod.constants.remote.RepositoryConstants
import com.artmine15.svod.repositories.remote.interfaces.RoomHandler
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlin.collections.listOf

class RoomRepository @Inject constructor() : RoomHandler {
    val db = FirebaseFirestore.getInstance()

    override fun createRoomAsAdmin(
        adminUserId: String,
        onSuccess: (String) -> Unit,
        onFailure: () -> Unit
    ) {
        val usersMap = hashMapOf(
            "adminUserId" to adminUserId,
            "userIds" to listOf<String>()
        )

        db.collection(RepositoryConstants.ROOMS_COLLECTION).add(usersMap)
            .addOnSuccessListener { documentReference ->
                Log.d("App", "Room creation success, id: ${documentReference.id}")
                onSuccess.invoke(documentReference.id)
            }
            .addOnFailureListener { exception ->
                Log.d("App", "Room creation failed. ${exception.toString()}")
                onFailure.invoke()
            }
    }

    override fun getAdminIdOfRoom(
        roomId: String,
        onSuccess: (adminUserId: String) -> Unit,
        onFailure: () -> Unit,
    ) {
        db.collection(RepositoryConstants.ROOMS_COLLECTION).document(roomId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val adminUserId = documentSnapshot.get("adminUserId") as String

                Log.d("App", "Admin of the room: $adminUserId")
                if(documentSnapshot != null && documentSnapshot.exists()){
                    onSuccess.invoke(adminUserId)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("App", "Admin not found: ${exception.toString()}")
            }
    }

    override fun joinRoomAsUser(
        roomId: String,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val roomDocument = db.collection(RepositoryConstants.ROOMS_COLLECTION).document(roomId)

        roomDocument.update("users", FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
                Log.d("App", "User $userId successfully added")
                onSuccess.invoke()
            }
            .addOnFailureListener { exception ->
                Log.d("App", "User $userId not added. ${exception.toString()}")
                onFailure.invoke()
            }
    }
}