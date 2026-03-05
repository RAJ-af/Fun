package com.itsraj.funkytalk.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.itsraj.funkytalk.ui.navigation.Screen
import com.itsraj.funkytalk.viewmodel.AuthState
import com.itsraj.funkytalk.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.collectAsState()

    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    LaunchedEffect(authState) {
        delay(2000) // Minimum splash time
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
            is AuthState.ProfileIncomplete -> {
                navController.navigate(Screen.ProfileSetup.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
            is AuthState.Unauthenticated -> {
                navController.navigate(Screen.Welcome.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "FT",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontSize = 120.sp
            ),
            modifier = Modifier.scale(scale)
        )
    }
}
