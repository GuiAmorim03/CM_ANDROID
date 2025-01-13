package com.example.cmnutricao.repository

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.cmnutricao.notifications.WaterReminder
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

data class MealPlannerData(
    val id: String = "",
    val userId: String = "",
    val breakfast : Int = 0,
    val snack : Int = 0,
    val lunch : Int = 0,
    val dinner : Int = 0,
    val water: Int = 0
)

class MealPlannerRepository {
    private val db = FirebaseFirestore.getInstance()
    private val mealPlannerCollection = db.collection("meal_planners")

    suspend fun getMealPlannerForUser(userId: String): MealPlannerData? {
        return try {
            val querySnapshot = mealPlannerCollection
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                document.toObject(MealPlannerData::class.java)?.copy(id = document.id)
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error searching for meal planner: ${e.message}")
            null
        }
    }

    suspend fun getFoodsForCategory(category: String): List<String> {
        val categoryToSearch = category.lowercase()
        return try {
            val result = db.collection("meals")
                .whereEqualTo("category", categoryToSearch)
                .get()
                .await()

            result.documents.mapNotNull { it.getString("name") }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createMealPlanner(planner: MealPlannerData, context: Context) {
        val userId = planner.userId
        val mealPlannersCollection = db.collection("meal_planners")
        val userDocument = mealPlannersCollection.document(userId)

        val existingPlanner = userDocument.get().await().toObject(MealPlannerData::class.java)
        Log.d("createMealPlanner", "userId: $userId")
        Log.d("createMealPlanner", "existingPlanner: $existingPlanner")

        if (existingPlanner != null) {
            userDocument.delete().await()
        }

        userDocument.set(planner).await()

        val waterFrequency = planner.water.toLong()
        scheduleWaterReminder(context, waterFrequency)
    }

    fun scheduleWaterReminder(context: Context, frequencyInMinutes: Long) {
        Log.d("NOTIFICACOES", "Notificacoes de $frequencyInMinutes minutos")
        val waterReminderRequest = PeriodicWorkRequest.Builder(WaterReminder::class.java, frequencyInMinutes, TimeUnit.MINUTES)
            .build()


        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "WaterReminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            waterReminderRequest
        )
    }

}
