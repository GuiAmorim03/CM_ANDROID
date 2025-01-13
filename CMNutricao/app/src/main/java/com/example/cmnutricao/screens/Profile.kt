package com.example.cmnutricao.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cmnutricao.repository.UserData
import com.example.cmnutricao.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.example.cmnutricao.LoginActivity

@Composable
fun Profile(navController: NavHostController) {
    val userRepository = remember { UserRepository() }
    val context = LocalContext.current

    var userData by remember { mutableStateOf<UserData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showEditDialog by remember { mutableStateOf(false) }
    var height by remember { mutableStateOf(0.0) }
    var weight by remember { mutableStateOf(0.0) }
    var gender by remember { mutableStateOf("") }

    fun getBmi(height: Double, weight: Double): Double {
        return weight / ((height / 100f) * (height / 100f))
    }

    fun getBmiCategory(bmi: Double): String {
        return when {
            bmi < 16 -> "Severe Thinness"
            bmi in 16.0..16.9 -> "Moderate Thinness"
            bmi in 17.0..18.4 -> "Mild Thinness"
            bmi in 18.5..24.9 -> "Normal"
            bmi in 25.0..29.9 -> "Overweight"
            bmi in 30.0..34.9 -> "Obese Class I"
            bmi in 35.0..39.9 -> "Obese Class II"
            bmi >= 40 -> "Obese Class III"
            else -> "Invalid BMI"
        }
    }

    fun getAdvise(bmi: Double): String {
        return when {
            bmi < 18.5 -> "You should gain more weight"
            bmi in 18.5..24.9 -> "You are on a good way. Well done!"
            bmi >= 25 -> "You should lose more weight"
            else -> "Invalid BMI"
        }
    }

    LaunchedEffect(Unit) {
        userData = userRepository.getUserData()
        userData?.let { user ->
            height = user.height.toDouble()
            weight = user.weight.toDouble()
            gender = user.gender
        }
        isLoading = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                userData?.let { user ->
                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = user.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(gender, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Height", fontSize = 12.sp)
                            Text("${height / 100} m", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Weight", fontSize = 12.sp)
                            Text("$weight kg", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    val bmi = getBmi(height, weight)
                    BmiColorScale(bmi)
                    Spacer(modifier = Modifier.height(40.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("BMI: ${getBmiCategory(getBmi(height, weight))}", fontSize = 18.sp)
                        Text(
                            text = getAdvise(getBmi(height, weight)),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                } ?: Text(text = "Error loading data")
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { signOut(context) },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd),
                containerColor = Color(0xFFD9534F),
                contentColor = Color.White,
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
            }
        }
    }

    if (showEditDialog) {
        var newHeight by remember { mutableStateOf("") }
        var newWeight by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Height and Weight") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newHeight,
                        onValueChange = { newHeight = it },
                        label = { Text("Height (cm)") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newWeight,
                        onValueChange = { newWeight = it },
                        label = { Text("Weight (kg)") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    height = newHeight.toDoubleOrNull() ?: height
                    weight = newWeight.toDoubleOrNull() ?: weight
                    userRepository.updateUserHeightWeight(height, weight)
                    showEditDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun BmiColorScale(bmi: Double) {
    val gradientColors = listOf(
        Color.Green,
        Color.Yellow,
        Color.Red
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(20.dp)
            .background(Color.Gray, shape = RoundedCornerShape(10.dp))
    ) {
        val width = size.width

        drawRect(
            brush = Brush.horizontalGradient(gradientColors),
            size = size
        )

        val minBmi = 10.0
        val maxBmi = 40.0
        val position = ((bmi - minBmi) / (maxBmi - minBmi)).coerceIn(0.0, 1.0).toFloat() * width

        drawLine(
            color = Color.Black,
            start = Offset(position, 0f),
            end = Offset(position, size.height),
            strokeWidth = 8f
        )
    }
}

fun signOut(context: android.content.Context) {
    val auth = FirebaseAuth.getInstance()
    val googleSignInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
    )

    auth.signOut()
    googleSignInClient.revokeAccess().addOnCompleteListener {
        Toast.makeText(context, "Sessão encerrada", Toast.LENGTH_SHORT).show()

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}
