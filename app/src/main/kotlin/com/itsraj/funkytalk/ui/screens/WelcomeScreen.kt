package com.itsraj.funkytalk.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.itsraj.funkytalk.ui.components.PremiumButton
import com.itsraj.funkytalk.ui.navigation.Screen
import com.itsraj.funkytalk.ui.theme.BackgroundGradient
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WelcomeScreen(navController: NavController) {
    val quotes = listOf(
        "Every language is a new way to see the world.",
        "Speak to the world in its own language.",
        "Languages connect people.",
        "To have another language is to possess a second soul."
    )

    var currentQuoteIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            currentQuoteIndex = (currentQuoteIndex + 1) % quotes.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(BackgroundGradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top: Logo
            Text(
                text = "FunkyTalk",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White.copy(alpha = 0.8f),
                    letterSpacing = 2.sp
                ),
                modifier = Modifier.padding(top = 24.dp)
            )

            // Center: Quote
            AnimatedContent(
                targetState = quotes[currentQuoteIndex],
                transitionSpec = {
                    fadeIn(animationSpec = tween(1000)) togetherWith fadeOut(animationSpec = tween(1000))
                }
            ) { quote ->
                Text(
                    text = quote,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 52.sp,
                        letterSpacing = (-1).sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // Bottom: Continue Button
            PremiumButton(
                text = "Get Started",
                onClick = { navController.navigate(Screen.Privacy.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
        }
    }
}
