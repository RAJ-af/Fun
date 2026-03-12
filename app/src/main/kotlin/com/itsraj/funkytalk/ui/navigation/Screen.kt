package com.itsraj.funkytalk.ui.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    // Auth & Onboarding
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Auth : Screen("auth")
    object EmailConfirmation : Screen("email_confirmation/{email}") {
        fun createRoute(email: String) = "email_confirmation/$email"
    }
    object Onboarding : Screen("onboarding")
    object Permissions : Screen("permissions")

    // Main Tabs
    object Home : Screen("home")
    object Moments : Screen("moments")
    object Discover : Screen("discover")
    object Chats : Screen("chats")
    object Profile : Screen("profile")
    object VoiceRoom : Screen("voice_room/{roomId}?role={role}") {
        fun createRoute(roomId: String, role: String = "listener") = "voice_room/$roomId?role=$role"
    }
    object CreateRoom : Screen("create_room")
    object Announcements : Screen("announcements")

    // Detailed
    object ChatDetail : Screen("chat_detail/{userName}/{photoUrl}") {
        fun createRoute(userName: String, photoUrl: String): String {
            val encodedUrl = URLEncoder.encode(photoUrl, StandardCharsets.UTF_8.toString())
            return "chat_detail/$userName/$encodedUrl"
        }
    }
}
