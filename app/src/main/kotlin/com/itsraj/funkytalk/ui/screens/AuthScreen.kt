package com.itsraj.funkytalk.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.itsraj.funkytalk.ui.components.PremiumButton
import com.itsraj.funkytalk.ui.components.PremiumTextField
import com.itsraj.funkytalk.ui.navigation.Screen
import com.itsraj.funkytalk.ui.theme.MangoYellow
import com.itsraj.funkytalk.viewmodel.AuthState
import com.itsraj.funkytalk.viewmodel.AuthViewModel

@Composable
fun AuthScreen(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Auth.route) { inclusive = true }
            }
        } else if (authState is AuthState.ProfileIncomplete) {
            navController.navigate(Screen.Onboarding.route) {
                popUpTo(Screen.Auth.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        DecorativeBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to FunkyTalk",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        letterSpacing = (-1.5).sp
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )

                Text(
                    text = "Enter your email and password to continue. If you don't have an account, we'll create one for you.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black.copy(alpha = 0.5f),
                        lineHeight = 22.sp
                    ),
                    modifier = Modifier.align(Alignment.Start).padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                PremiumTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(16.dp))

                PremiumTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = Color.Black.copy(alpha = 0.4f))
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                when (val state = authState) {
                    is AuthState.Error -> {
                        Text(
                            text = state.message,
                            color = Color.Red,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.Start).padding(start = 4.dp)
                        )
                    }
                    is AuthState.Success -> {
                        Text(
                            text = state.message,
                            color = Color(0xFF4CAF50),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.Start).padding(start = 4.dp)
                        )
                    }
                    else -> {}
                }

                Spacer(modifier = Modifier.height(32.dp))

                PremiumButton(
                    text = if (authState is AuthState.Loading) "Processing..." else "Continue",
                    onClick = { authViewModel.continueWithEmail(email, password) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = authState !is AuthState.Loading && email.isNotBlank() && password.isNotBlank()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Black.copy(alpha = 0.1f))
                    Text(
                        "or",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Black.copy(alpha = 0.3f))
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Black.copy(alpha = 0.1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Continue with Google Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable(enabled = authState !is AuthState.Loading) { authViewModel.loginWithGoogle() },
                    shape = RoundedCornerShape(28.dp),
                    color = Color.Black
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Official Google 'G' Icon Placeholder
                        Text(
                            "G",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = Color.White,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text("Continue with Google", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                    }
                }

                if (authState is AuthState.Loading) {
                    Spacer(modifier = Modifier.height(24.dp))
                    CircularProgressIndicator(color = Color.Black, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
fun DecorativeBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .size(180.dp)
                .offset(x = 280.dp, y = 80.dp)
                .rotate(20f),
            color = MangoYellow.copy(alpha = 0.08f),
            shape = RoundedCornerShape(40.dp)
        ) {}

        Surface(
            modifier = Modifier
                .size(120.dp)
                .offset(x = (-40).dp, y = 550.dp)
                .rotate(-15f),
            color = MangoYellow.copy(alpha = 0.05f),
            shape = RoundedCornerShape(32.dp)
        ) {}

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .offset(x = 40.dp, y = 100.dp)
                .rotate(10f),
            tint = MangoYellow.copy(alpha = 0.15f)
        )
    }
}
