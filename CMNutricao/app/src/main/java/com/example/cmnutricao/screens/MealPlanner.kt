package com.example.cmnutricao.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cmnutricao.repository.MealPlannerRepository
import com.example.cmnutricao.repository.MealPlannerData
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MealPlanner(navController: NavHostController) {
    val mealPlannerRepository = remember { MealPlannerRepository() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var mealPlanner by remember { mutableStateOf<MealPlannerData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        mealPlanner = mealPlannerRepository.getMealPlannerForUser(userId)
        isLoading = false
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            mealPlanner?.let { planner ->
                MealPlannerContent(planner, navController)
            } ?: NoMealPlannerMessage(navController)
        }
    }
}

@Composable
fun NoMealPlannerMessage(navController: NavHostController) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("No meal planner found.", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            navController.navigate("Create Meal Planner")
        }) {
            Text(text = "Create New Planner")
        }
    }
}

@Composable
fun MealPlannerContent(planner: MealPlannerData, navController: NavHostController) {
    val categories = listOf(
        "🍳 Breakfast" to planner.breakfast,
        "\uD83E\uDD58 Lunch" to planner.lunch,
        "🥐 Snack" to planner.snack,
        "\uD83C\uDF72 Dinner" to planner.dinner,
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "✔\uFE0F Meal Planner is Active",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        items(categories) { (category, calories) ->
            MealPlannerCategory(category, calories)
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💧 Drink water",
                        fontSize = 20.sp,
                    )
                    Text(
                        text = " every ${getWaterFrequency(planner.water)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(onClick = {
                        navController.navigate("Plan Daily Meal")
                    }) {
                        Text(text = "Plan Daily Meal")
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(onClick = {
                        navController.navigate("Create Meal Planner")
                    }) {
                        Text(text = "Create New Planner")
                    }
                }
            }
        }
    }
}

@Composable
fun MealPlannerCategory(name: String, calories: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
    ) {
        Text(
            text = "$name ($calories kcal)",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


fun getWaterFrequency(waterTime: Int) : String {
    val hours = waterTime / 60
    val minutes = waterTime % 60

    return "${if (hours == 0) "" else "${hours}h"}${if (minutes == 0) "" else "${minutes}min"}"

}
