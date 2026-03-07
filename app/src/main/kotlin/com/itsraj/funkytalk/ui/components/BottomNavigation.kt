package com.itsraj.funkytalk.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.foundation.interaction.MutableInteractionSource
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
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(40.dp),
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                ),
            shape = RoundedCornerShape(40.dp),
            color = Color(0xFF222222).copy(alpha = 0.95f) // Dark glass effect
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.screen.route
                    val scale by animateFloatAsState(if (isSelected) 1.2f else 1f)
                    val iconColor by animateColorAsState(if (isSelected) MangoYellow else Color.White.copy(alpha = 0.4f))

                    if (item == BottomNavItem.Discover) {
                        // Highlighted center button
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MangoYellow)
                                .clickable { navigateTo(navController, item.screen.route) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = Color.Black,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .scale(scale)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { navigateTo(navController, item.screen.route) }
                                )
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = iconColor,
                                modifier = Modifier.size(26.dp)
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
