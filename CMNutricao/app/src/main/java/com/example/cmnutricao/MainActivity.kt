package com.example.cmnutricao

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.cmnutricao.navigation.AppNavigation
import com.example.cmnutricao.ui.theme.CMNutricaoTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import android.Manifest.permission.POST_NOTIFICATIONS
import androidx.activity.result.ActivityResultLauncher

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        if (auth.currentUser == null) {
            abrirLoginActivity()
            return
        }

        val requestNotificationPermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
//                Toast.makeText(this, "Permission Allowed", Toast.LENGTH_SHORT).show()
            } else {
//                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

        pedirPermissaoNotificacao(requestNotificationPermission)

        setContent {
            CMNutricaoTheme {
                val navController = rememberNavController()
                AppNavigation(navController, auth, googleSignInClient, this)
            }
        }
    }

    private fun abrirLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun pedirPermissaoNotificacao(requestNotificationPermission: ActivityResultLauncher<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission.launch(POST_NOTIFICATIONS)
            }
        }
    }
}
