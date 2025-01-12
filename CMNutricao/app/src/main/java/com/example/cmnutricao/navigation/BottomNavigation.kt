package com.example.cmnutricao.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        "Meal Planner" to "🍎",
        "Profile" to "👤"
    )

    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { (route, icon) ->
            NavigationBarItem(
                icon = { Text(icon) },
                label = { Text(route.capitalize()) },
                selected = currentRoute == route,
                onClick = { navController.navigate(route) }
            )
        }
    }
}
