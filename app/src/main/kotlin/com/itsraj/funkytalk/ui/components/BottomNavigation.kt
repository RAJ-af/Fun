package com.itsraj.funkytalk.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.itsraj.funkytalk.ui.navigation.Screen
import com.itsraj.funkytalk.ui.theme.MangoYellow

sealed class BottomNavItem(val title: String, val icon: ImageVector, val screen: Screen) {
    object Home : BottomNavItem("Home", Icons.Outlined.Home, Screen.Home)
    object Moments : BottomNavItem("Moments", Icons.Outlined.Language, Screen.Moments)
    object Discover : BottomNavItem("Discover", Icons.Outlined.Explore, Screen.Discover)
    object Chats : BottomNavItem("Chats", Icons.Outlined.ChatBubbleOutline, Screen.Chats)
    object Profile : BottomNavItem("Profile", Icons.Outlined.PersonOutline, Screen.Profile)
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(36.dp),
                    ambientColor = Color.Black.copy(alpha = 0.2f),
                    spotColor = Color.Black.copy(alpha = 0.2f)
                ),
            shape = RoundedCornerShape(36.dp),
            color = Color.White
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.screen.route
                    val scale by animateFloatAsState(if (isSelected) 1.2f else 1f)

                    if (item == BottomNavItem.Discover) {
                        // Highlighted center button with Mango Yellow
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MangoYellow else Color.Black)
                                .clickable { navigateTo(navController, item.screen.route) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) Color.Black else Color.White
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { navigateTo(navController, item.screen.route) },
                            modifier = Modifier.scale(scale)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) MangoYellow else Color.Black.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route) {
        navController.graph.startDestinationRoute?.let { startRoute ->
            popUpTo(startRoute) {
                saveState = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }
}
