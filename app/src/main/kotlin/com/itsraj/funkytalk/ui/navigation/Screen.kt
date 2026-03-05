package com.itsraj.funkytalk.ui.navigation

sealed class Screen(val route: String) {
    // Auth & Onboarding
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Privacy : Screen("privacy")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object ProfileSetup : Screen("profile_setup")
    object Permissions : Screen("permissions")

    // Main Tabs
    object Home : Screen("home")
    object Moments : Screen("moments")
    object Discover : Screen("discover")
    object Chats : Screen("chats")
    object Profile : Screen("profile")

    // Detailed
    object ChatDetail : Screen("chat_detail/{chatId}") {
        fun createRoute(chatId: String) = "chat_detail/$chatId"
    }
}
