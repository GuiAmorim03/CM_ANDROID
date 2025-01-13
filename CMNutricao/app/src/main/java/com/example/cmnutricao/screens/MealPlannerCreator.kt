package com.example.cmnutricao.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cmnutricao.repository.MealPlannerData
import com.example.cmnutricao.repository.MealPlannerRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun MealPlannerCreator(navController: NavHostController) {
    val mealPlannerRepository = remember { MealPlannerRepository() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var mealPlanner by remember { mutableStateOf(MealPlannerData(userId = userId)) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            val existingPlanner = mealPlannerRepository.getMealPlannerForUser(userId)
            if (existingPlanner != null) {
                mealPlanner = existingPlanner
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text("Create Your Meal Planner", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(50.dp))

        fun updateMealPlannerValue(field: (Int) -> MealPlannerData, value: String) {
            mealPlanner = field(value.toIntOrNull() ?: 0)
        }

        TextField(
            value = mealPlanner.breakfast.toString(),
            onValueChange = { updateMealPlannerValue({ mealPlanner.copy(breakfast = it) }, it) },
            label = { Text("Breakfast (kcal)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = mealPlanner.lunch.toString(),
            onValueChange = { updateMealPlannerValue({ mealPlanner.copy(lunch = it) }, it) },
            label = { Text("Lunch (kcal)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = mealPlanner.snack.toString(),
            onValueChange = { updateMealPlannerValue({ mealPlanner.copy(snack = it) }, it) },
            label = { Text("Snack (kcal)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = mealPlanner.dinner.toString(),
            onValueChange = { updateMealPlannerValue({ mealPlanner.copy(dinner = it) }, it) },
            label = { Text("Dinner (kcal)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = mealPlanner.water.toString(),
            onValueChange = { updateMealPlannerValue({ mealPlanner.copy(water = it) }, it) },
            label = { Text("Water Drinking Frequency (minutes)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    isLoading = true
                    try {
                        mealPlannerRepository.createMealPlanner(mealPlanner, context)
                        Toast.makeText(context, "Meal Planner saved!", Toast.LENGTH_SHORT).show()
                        navController.navigate("Meal Planner")
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading
        ) {
            Text("Save")
        }

        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}
