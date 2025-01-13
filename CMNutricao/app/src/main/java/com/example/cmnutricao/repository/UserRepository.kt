package com.example.cmnutricao.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

data class UserData(
    val name: String = "",
    val height: Double = 0.0,
    val weight: Double = 0.0,
    val gender: String = ""
)

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getUserData(): UserData {
        val userId = auth.currentUser?.uid ?: return UserData()

        return try {
            val document = db.collection("users").document(userId).get().await()
            document.toObject(UserData::class.java) ?: UserData()
        } catch (e: Exception) {
            UserData()
        }
    }

    fun updateUserHeightWeight(newHeight: Double, newWeight: Double) {
        val userId = auth.currentUser?.uid ?: ""
        val userRef = db.collection("users").document(userId)

        userRef.update("height", newHeight)
            .addOnSuccessListener {
                println("User height updated successfully")
            }
            .addOnFailureListener { e ->
                println("Error updating user height: ${e.message}")
            }

        userRef.update("weight", newWeight)
            .addOnSuccessListener {
                println("User weight updated successfully")
            }
            .addOnFailureListener { e ->
                println("Error updating user weight: ${e.message}")
            }
    }

}
