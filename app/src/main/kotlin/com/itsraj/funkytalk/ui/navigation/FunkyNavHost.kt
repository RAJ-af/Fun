package com.itsraj.funkytalk.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itsraj.funkytalk.FunkyTalkApp
import com.itsraj.funkytalk.data.repository.AnnouncementRepository
import com.itsraj.funkytalk.data.repository.VoiceRoomRepository
import com.itsraj.funkytalk.viewmodel.*

@Composable
fun MainAppScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val authViewModel: AuthViewModel = viewModel()

    val context = LocalContext.current
    val voiceRoomRepository = remember { VoiceRoomRepository(FunkyTalkApp.supabase) }
    val voiceRoomViewModel: VoiceRoomViewModel = viewModel(
        factory = VoiceRoomViewModelFactory(context.applicationContext as android.app.Application, voiceRoomRepository)
    )

    val announcementRepository = remember { AnnouncementRepository(FunkyTalkApp.supabase) }
    val announcementViewModel: AnnouncementViewModel = viewModel(
        factory = AnnouncementViewModelFactory(announcementRepository)
    )

    val bottomBarScreens = listOf(
        Screen.Home.route,
        Screen.Moments.route,
        Screen.Discover.route,
        Screen.Chats.route,
        Screen.Profile.route,
        Screen.VoiceRoom.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarScreens) {
                FunkyBottomNavigation(navController = navController)
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        FunkyNavHost(
            navController = navController,
            authViewModel = authViewModel,
            voiceRoomViewModel = voiceRoomViewModel,
            announcementViewModel = announcementViewModel,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        )
    }
}

@Composable
fun FunkyNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    voiceRoomViewModel: VoiceRoomViewModel,
    announcementViewModel: AnnouncementViewModel,
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
        composable(Screen.Auth.route) {
            AuthScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Screen.EmailConfirmation.route) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmailConfirmationScreen(
                navController = navController,
                authViewModel = authViewModel,
                email = email
            )
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(Screen.Permissions.route) { PlaceholderScreen("Permissions") }

        // Main Tabs
        composable(Screen.Home.route) {
            HomeScreen(navController, voiceRoomViewModel, authViewModel, announcementViewModel)
        }
        composable(Screen.Moments.route) { MomentsScreen() }
        composable(Screen.Discover.route) { DiscoverScreen() }
        composable(Screen.Chats.route) { ChatsScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen() }
        composable(
            route = Screen.VoiceRoom.route,
            arguments = listOf(
                androidx.navigation.navArgument("roomId") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("role") {
                    type = androidx.navigation.NavType.StringType
                    defaultValue = "listener"
                }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            val role = backStackEntry.arguments?.getString("role") ?: "listener"
            VoiceRoomScreen(navController, voiceRoomViewModel, authViewModel, roomId, role)
        }
        composable(Screen.CreateRoom.route) { CreateRoomScreen(navController, voiceRoomViewModel, authViewModel) }
        composable(Screen.Announcements.route) { AnnouncementScreen(navController, announcementViewModel) }

        // Details
        composable(Screen.ChatDetail.route) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "User"
            val photoUrl = backStackEntry.arguments?.getString("photoUrl") ?: ""
            IndividualChatScreen(navController, userName, photoUrl)
        }
    }
}
