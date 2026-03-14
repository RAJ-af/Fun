package com.itsraj.funkytalk.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import android.content.Intent
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.itsraj.funkytalk.ui.navigation.Screen
import com.itsraj.funkytalk.ui.components.PremiumButton
import com.itsraj.funkytalk.ui.theme.MangoYellow
import com.itsraj.funkytalk.viewmodel.AuthState
import com.itsraj.funkytalk.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun EmailConfirmationScreen(navController: NavController, authViewModel: AuthViewModel, email: String) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val authState by authViewModel.authState.collectAsState()
    var animationPlayed by remember { mutableStateOf(false) }

    var resendCooldown by remember { mutableIntStateOf(0) }
    val isResendEnabled = resendCooldown == 0

    // Lifecycle observer to check verification when app resumes
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                authViewModel.checkUserStatus(refresh = true)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Timer for resend cooldown
    LaunchedEffect(resendCooldown) {
        if (resendCooldown > 0) {
            delay(1000L)
            resendCooldown -= 1
        }
    }

    val circleProgress = animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
        label = "CircleProgress"
    )

    val checkmarkScale = animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "CheckmarkScale"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
        authViewModel.checkUserStatus(refresh = true)
    }

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                if (state.message == "Confirmation email sent again") {
                    Toast.makeText(context, "Confirmation email sent again", Toast.LENGTH_SHORT).show()
                }
            }
            is AuthState.Authenticated -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Auth.route) { this.inclusive = true }
                }
            }
            is AuthState.ProfileIncomplete -> {
                navController.navigate(Screen.Onboarding.route) {
                    popUpTo(Screen.Auth.route) { this.inclusive = true }
                }
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success Animation
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = MangoYellow,
                    startAngle = -90f,
                    sweepAngle = 360f * circleProgress.value,
                    useCenter = false,
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            if (circleProgress.value > 0.9f) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .scale(checkmarkScale.value),
                    tint = MangoYellow
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Email sent",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                color = Color.Black
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "We sent a confirmation link to your email:\n$email",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Black.copy(alpha = 0.5f),
                lineHeight = 24.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(64.dp))

        PremiumButton(
            text = "Open Email",
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_EMAIL)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                if (isResendEnabled) {
                    authViewModel.resendConfirmationEmail(email)
                    resendCooldown = 30
                }
            },
            enabled = isResendEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = if (isResendEnabled) "Resend Email" else "Resend Email (${resendCooldown}s)",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isResendEnabled) Color.Black.copy(alpha = 0.6f) else Color.Gray
                )
            )
        }
    }
}
