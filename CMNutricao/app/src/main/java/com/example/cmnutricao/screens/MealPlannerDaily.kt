package com.example.cmnutricao.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.example.cmnutricao.repository.MealPlannerRepository
import com.example.cmnutricao.repository.MealPlannerData
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.reflect.typeOf

@Composable
fun MealPlannerDaily(navController: NavHostController) {
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
                MealPlannerDailyNavigator(planner, navController)
            }
        }
    }
}

@Composable
fun MealPlannerDailyNavigator(planner: MealPlannerData, navController: NavHostController) {
    val dayTimeCategories = mapOf(
        "breakfast" to listOf("Liquid", "Cereal"),
        "lunch" to listOf("Vegetables", "Main", "Side"),
        "snack" to listOf("Liquid", "Cereal"),
        "dinner" to listOf("Vegetables", "Main", "Side")
    )

    val dayTime = dayTimeCategories.keys.toList()
    var currentIndex by remember { mutableStateOf(0) }
    val dayTimeCalories = listOf(
        "breakfast" to planner.breakfast,
        "lunch" to planner.lunch,
        "snack" to planner.snack,
        "dinner" to planner.dinner,
    )

    val (currentMeal, calories) = dayTimeCalories.getOrNull(currentIndex) ?: ("" to 0)
    val categories = dayTimeCategories[currentMeal] ?: emptyList()

    var selectedFoodsByDay by remember { mutableStateOf(mutableMapOf<String, MutableMap<String, String>>()) }
    var currentCategoryIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📝 Plan Your Daily Meal",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))
        
        if (currentIndex < dayTime.size) {
            Text(
                text = "$currentMeal ($calories kcal)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(30.dp))

            if (currentCategoryIndex < categories.size) {
                val category = categories[currentCategoryIndex]
                MealCategorySelector(category, selectedFoodsByDay, currentMeal) { selectedOption ->
                    selectedFoodsByDay.getOrPut(currentMeal) { mutableMapOf() }[category] = selectedOption

                    Log.d("Select Food", "Updated selections: $selectedFoodsByDay")
                    Log.d("Select Food", "currentCategoryIndex: $currentCategoryIndex")
                    Log.d("Select Food", "currentIndex: $currentIndex")
                    Log.d("Select Food", "dayTime.size: $dayTime.size")
                    Log.d("Select Food", "categories.size: $categories.size")

                    if (currentCategoryIndex < categories.size - 1) {
                        currentCategoryIndex++
                    } else {
                        currentIndex++
                        currentCategoryIndex = 0
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.height(10.dp))

            selectedFoodsByDay.forEach { (meal, categories) ->
                categories["Fruit"] = "piece of fruit"

                if (meal == "lunch" || meal == "dinner") {
                    categories["Starters"] = "soup"
                }
            }

            selectedFoodsByDay.forEach { (meal, categories) ->
                val calories = dayTimeCalories.firstOrNull { it.first == meal }?.second ?: 0

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = meal.uppercase(), fontSize = 16.sp, fontWeight = FontWeight.Bold)

                    Log.d("Calories", "$calories")
                    Log.d("Calories", calories.javaClass.simpleName)
                    Text(text = "($calories kcal)", fontSize = 16.sp)
                }

                when (meal) {
                    "breakfast", "snack" -> {
                        val liquid = categories["Liquid"] ?: "Unknown"
                        Log.d("math", "${calories}/2 = ${calories/2}")
                        val liquidQuantity = if (liquid == "yoghurt" || liquid == "milk") "${calories/2}ml" else if (liquid == "chocolate milk") "${calories/1.5}ml" else ""
                        Text(text = "$liquid ($liquidQuantity)", fontSize = 16.sp)

                        val cereal = categories["Cereal"] ?: "Unknown"
                        val cerealQuantity = if (cereal == "oat" || cereal == "granola") "${calories/7}g" else if (cereal == "bread") "${calories/5}g" else "${calories/100}un"
                        Text(text = "$cereal ($cerealQuantity)", fontSize = 16.sp)
                    }

                    "lunch", "dinner" -> {
                        val starters = categories["Starters"] ?: "Unknown"
                        val startersQuantity = "${calories/3}g"
                        Text(text = "$starters ($startersQuantity)", fontSize = 16.sp)

                        val vegetables = categories["Vegetables"] ?: "Unknown"
                        val vegetablesQuantity = "${calories/6}g"
                        Text(text = "$vegetables ($vegetablesQuantity)", fontSize = 16.sp)

                        val main = categories["Main"] ?: "Unknown"
                        val mainQuantity = if (main == "fish" || main == "white meat") "${calories/5}g" else if (main == "red meat") "${calories/6}g" else if (main == "salmon") "${calories/7}g"  else if (main == "tuna") "${calories/300}un" else "1un"
                        Text(text = "$main ($mainQuantity)", fontSize = 16.sp)

                        val side = categories["Side"] ?: "Unknown"
                        val sideQuantity = if (side == "rice") "${calories/3}g" else if (side == "pasta") "${calories/4}g" else ""
                        Text(text = "$side ($sideQuantity)", fontSize = 16.sp)
                    }
                }

                val fruit = categories["Fruit"] ?: "Unknown"
                val fruitQuantity = "1un"
                Text(text = "$fruit ($fruitQuantity)", fontSize = 16.sp)


                Spacer(modifier = Modifier.height(10.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't forget to drink water",
                    fontSize = 14.sp,
                )
                Text(
                    text = " every ${getWaterFrequency(planner.water)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                val context = LocalContext.current
                val view = LocalView.current
                FloatingActionButton(
                    onClick = {
                        val uri = captureScreenAndShare(view, context)
                        uri?.let { shareImage(context, it) }
                    },
                    containerColor = Color.Green,
                    contentColor = Color.Black
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                }
            }


        }
    }
}

@Composable
fun MealCategorySelector(category: String, selectedFoodsByDay: MutableMap<String, MutableMap<String, String>>, currentMeal: String, onSelect: (String) -> Unit) {
    val mealRepository = remember { MealPlannerRepository() }
    var foodOptions by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(category) {
        foodOptions = mealRepository.getFoodsForCategory(category)
        isLoading = false
    }

    Column {
        Text(
            text = category,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(foodOptions) { food ->
                    FoodOptionItem(food) {
                        onSelect(food)
                    }
                }
            }
        }
    }
}

@Composable
fun FoodOptionItem(food: String, onClick: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = Color.Gray,
        modifier = Modifier
            .padding(4.dp)
            .size(100.dp)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = food,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


fun captureScreenAndShare(view: View, context: Context): Uri? {
    val originalBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(originalBitmap)
    view.draw(canvas)

    val x = 0
    val y = 100
    val width = view.width
    val height = 1100

    val croppedBitmap = Bitmap.createBitmap(
        originalBitmap,
        x.coerceAtLeast(0),
        y.coerceAtLeast(0),
        width.coerceAtMost(originalBitmap.width - x),
        height.coerceAtMost(originalBitmap.height - y)
    )

    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "meal_plan.png")

    return try {
        val stream = FileOutputStream(file)
        croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()

        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun shareImage(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share Meal Plan"))
}
