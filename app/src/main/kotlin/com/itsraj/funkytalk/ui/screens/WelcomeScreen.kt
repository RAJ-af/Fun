package com.itsraj.funkytalk.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.itsraj.funkytalk.ui.components.PremiumButton
import com.itsraj.funkytalk.ui.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(navController: NavController) {
    val quotes = listOf(
        "Every language is a new way to see the world.",
        "To have another language is to possess a second soul.",
        "Language is the road map of a culture.",
        "Learning is a treasure that will follow its owner everywhere.",
        "Speak a new language so that the world will be a new world."
    )

    var currentQuoteIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while(true) {
            delay(4000)
            currentQuoteIndex = (currentQuoteIndex + 1) % quotes.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top: App Name
            Text(
                text = "FunkyTalk",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    letterSpacing = 1.sp,
                    fontSize = 24.sp
                ),
                modifier = Modifier.padding(top = 16.dp)
            )

            // Center: Rotating Quote
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = quotes[currentQuoteIndex],
                    transitionSpec = {
                        (fadeIn() + slideInVertically { it / 2 })
                            .togetherWith(fadeOut() + slideOutVertically { -it / 2 })
                    },
                    label = "QuoteAnimation"
                ) { quote ->
                    Text(
                        text = quote,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            lineHeight = 48.sp,
                            letterSpacing = (-1.5).sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            // Bottom: Continue Button
            PremiumButton(
                text = "Get Started",
                onClick = { navController.navigate(Screen.Auth.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(bottom = 8.dp)
            )
        }
    }
}
