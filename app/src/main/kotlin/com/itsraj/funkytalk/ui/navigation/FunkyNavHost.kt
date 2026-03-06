package com.itsraj.funkytalk.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.itsraj.funkytalk.ui.components.FunkyBottomNavigation
import com.itsraj.funkytalk.ui.screens.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itsraj.funkytalk.viewmodel.AuthViewModel

@Composable
fun MainAppScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val authViewModel: AuthViewModel = viewModel()

    val bottomBarScreens = listOf(
        Screen.Home.route,
        Screen.Moments.route,
        Screen.Discover.route,
        Screen.Chats.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarScreens) {
                FunkyBottomNavigation(navController = navController)
            }
        }
    ) { innerPadding ->
        FunkyNavHost(
            navController = navController,
            authViewModel = authViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun FunkyNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        // Auth flow
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController = navController)
        }
        composable(Screen.Privacy.route) {
            PrivacyLegalScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Screen.Signup.route) {
            SignupScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Screen.ProfileSetup.route) {
            ProfileSetupScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(Screen.Permissions.route) { PlaceholderScreen("Permissions") }

        // Main Tabs
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.Moments.route) { MomentsScreen() }
        composable(Screen.Discover.route) { DiscoverScreen() }
        composable(Screen.Chats.route) { ChatsScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen() }

        // Details
        composable(Screen.ChatDetail.route) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "User"
            val photoUrl = backStackEntry.arguments?.getString("photoUrl") ?: ""
            IndividualChatScreen(navController, userName, photoUrl)
        }
    }
}
