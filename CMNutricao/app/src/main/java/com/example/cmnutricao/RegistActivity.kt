package com.example.cmnutricao

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cmnutricao.ui.theme.CMNutricaoTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class RegistActivity : ComponentActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val userId = intent.getStringExtra("userId")
            val name = intent.getStringExtra("name")
            val email = intent.getStringExtra("email")

            if (userId != null && name != null && email != null) {
                RegistrationScreen(userId, name, email)
            }
        }
    }

    @Composable
    fun RegistrationScreen(userId: String, name: String, email: String) {
        var height by remember { mutableStateOf("") }
        var weight by remember { mutableStateOf("") }
        var gender by remember { mutableStateOf("") }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(text = "Complete Your Profile", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(20.dp))

                TextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Height (cm)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = gender == "Male",
                            onClick = { gender = "Male" }
                        )
                        Text("Male")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = gender == "Female",
                            onClick = { gender = "Female" }
                        )
                        Text("Female")
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val heightValue = height.toFloatOrNull() ?: 0f
                        val weightValue = weight.toFloatOrNull() ?: 0f
                        saveUserData(userId, name, email, heightValue, weightValue, gender)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Save")
                }
            }
        }
    }

    private fun saveUserData(userId: String, name: String, email: String, height: Float, weight: Float, gender: String) {
        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val newUser = hashMapOf(
                    "id" to userId,
                    "name" to name,
                    "email" to email,
                    "height" to height,
                    "weight" to weight,
                    "gender" to gender
                )

                userRef.set(newUser)
                    .addOnSuccessListener {
                        Log.d("Firestore", "User Saved")
                        finish()
                    }
                    .addOnFailureListener { e -> Log.e("Firestore", "Error saving user", e)
                }
            }
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Error checking user", e)
        }
    }
}
