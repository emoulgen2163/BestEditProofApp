package com.emoulgen.besteditproofapp.utils

// Create NavGraph.kt
sealed class NavGraph(val route: String) {
    object Splash : NavGraph("splash")
    object Login : NavGraph("login")
    object SignUp : NavGraph("signup")
    object Dashboard : NavGraph("dashboard")
    object NewOrder : NavGraph("new_order")
    object Profile : NavGraph("profile")
}