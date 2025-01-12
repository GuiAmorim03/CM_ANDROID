package com.example.cmnutricao

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.cmnutricao.navigation.AppNavigation
import com.example.cmnutricao.ui.theme.CMNutricaoTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

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
}
