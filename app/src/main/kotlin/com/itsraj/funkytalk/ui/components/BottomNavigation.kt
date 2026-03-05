package com.itsraj.funkytalk.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.itsraj.funkytalk.ui.navigation.Screen

sealed class BottomNavItem(val title: String, val icon: ImageVector, val screen: Screen) {
    object Home : BottomNavItem("Home", Icons.Default.Home, Screen.Home)
    object Moments : BottomNavItem("Moments", Icons.Default.Public, Screen.Moments)
    object Discover : BottomNavItem("Discover", Icons.Default.Search, Screen.Discover)
    object Chats : BottomNavItem("Chats", Icons.Default.Chat, Screen.Chats)
    object Profile : BottomNavItem("Profile", Icons.Default.Person, Screen.Profile)
}

@Composable
fun FunkyBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Moments,
        BottomNavItem.Discover,
        BottomNavItem.Chats,
        BottomNavItem.Profile
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
