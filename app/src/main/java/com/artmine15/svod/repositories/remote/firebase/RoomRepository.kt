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
        onFailure: (exception: Exception) -> Unit
    ) {
        val usersMap = hashMapOf(
            "adminUserId" to adminUserId,
            "userIds" to listOf<String>()
        )

        db.collection(RepositoryConstants.ROOMS_COLLECTION).add(usersMap)
            .addOnSuccessListener { documentReference ->
                Log.d("createRoomAsAdmin()", "Room creation success, id: ${documentReference.id}")
                onSuccess.invoke(documentReference.id)
            }
            .addOnFailureListener { exception ->
                Log.d("createRoomAsAdmin()", "Room creation failed. ${exception.toString()}")
                onFailure.invoke(exception)
            }
    }

    override fun getAdminIdOfRoom(
        roomId: String,
        onSuccess: (adminUserId: String) -> Unit,
        onFailure: (exception: Exception) -> Unit,
    ) {
        db.collection(RepositoryConstants.ROOMS_COLLECTION).document(roomId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val adminUserId = documentSnapshot.get("adminUserId") as String

                Log.d("getAdminIdOfRoom()", "Admin of the room: $adminUserId")
                if(documentSnapshot != null && documentSnapshot.exists()){
                    onSuccess.invoke(adminUserId)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("getAdminIdOfRoom()", "Admin not found: ${exception.toString()}")
                onFailure(exception)
            }
    }

    override fun joinRoomAsUser(
        userId: String,
        roomId: String,
        onSuccess: () -> Unit,
        onFailure: (exception: Exception) -> Unit
    ) {
        db.collection(RepositoryConstants.ROOMS_COLLECTION).document(roomId)
            .update("userIds", FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
                Log.d("joinRoomAsUser()", "User $userId successfully added")
                onSuccess.invoke()
            }
            .addOnFailureListener { exception ->
                Log.d("joinRoomAsUser()", "User $userId not added to room $roomId. ${exception.toString()}")
                onFailure.invoke(exception)
            }
    }

    override suspend fun isUserInRoom(
        userId: String,
        roomId: String,
        onNoRoom: () -> Unit,
        onNoUserInRoom: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (exception: Exception) -> Unit,
    ){
        db.collection(RepositoryConstants.ROOMS_COLLECTION).document(roomId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val userIds = documentSnapshot.get("userIds") as List<*>
                    if (userIds.contains(userId)) onSuccess.invoke() else onNoUserInRoom.invoke()

                    Log.d("isUserInRoom()", "$userId in room $roomId")
                }
                else{
                    onNoRoom.invoke()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("isUserInRoom()", exception.toString())
                onFailure.invoke(exception)
            }
    }
}