package com.artmine15.svod.repositories.remote.firebase

import android.util.Log
import com.artmine15.svod.LogTags
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
                Log.d(LogTags.svod, "createRoomAsAdmin()/Room ${documentReference.id} created")
                onSuccess.invoke(documentReference.id)
            }
            .addOnFailureListener { exception ->
                Log.d(LogTags.svod, "createRoomAsAdmin()/Room creation failed. ${exception.toString()}")
                onFailure.invoke(exception)
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
                Log.d(LogTags.svod, "joinRoomAsUser()/User $userId successfully added to room $roomId")
                onSuccess.invoke()
            }
            .addOnFailureListener { exception ->
                Log.d(LogTags.svod, "joinRoomAsUser()/Fail to add user $userId to room $roomId. ${exception.toString()}")
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
                    if (userIds.contains(userId)){
                        Log.d(LogTags.svod, "isUserInRoom()/User $userId listed in room $roomId")
                        onSuccess.invoke()
                    }
                    else{
                        Log.d(LogTags.svod, "isUserInRoom()/No user $userId listed in room $roomId")
                        onNoUserInRoom.invoke()
                    }

                }
                else{
                    Log.d(LogTags.svod, "isUserInRoom()/No room $roomId")
                    onNoRoom.invoke()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(LogTags.svod, exception.toString())
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
                if(documentSnapshot != null && documentSnapshot.exists()){
                    val adminUserId = documentSnapshot.get("adminUserId") as String
                    Log.d(LogTags.svod, "getAdminIdOfRoom()/AdminUserId of the room: $adminUserId")

                    onSuccess.invoke(adminUserId)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(LogTags.svod, "getAdminIdOfRoom()/Fail to get adminUserId: ${exception.toString()}")
                onFailure(exception)
            }
    }

    override fun isUserAdminOfRoom(
        userId: String,
        roomId: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        getAdminIdOfRoom(
            roomId = roomId,
            onSuccess = { adminUserId ->
                onSuccess(userId == adminUserId)
            },
            onFailure = onFailure
        )
    }
}