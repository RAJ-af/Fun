package com.itsraj.funkytalk.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Email
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
import com.itsraj.funkytalk.viewmodel.AuthState
import com.itsraj.funkytalk.viewmodel.AuthViewModel

@Composable
fun AuthScreen(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isEmailMode by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Auth.route) { inclusive = true }
            }
        } else if (authState is AuthState.ProfileIncomplete) {
            navController.navigate(Screen.ProfileSetup.route) {
                popUpTo(Screen.Auth.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        DecorativeBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (isEmailMode) isEmailMode = false else navController.popBackStack()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = if (!isEmailMode) "Speak to the\nworld." else "Welcome\nBack.",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    lineHeight = 52.sp,
                    letterSpacing = (-2).sp
                ),
                modifier = Modifier.align(Alignment.Start).padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            if (!isEmailMode) {
                AuthMethodCard(
                    text = "Continue with Email",
                    icon = Icons.Outlined.Email,
                    onClick = { isEmailMode = true }
                )

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    "By continuing, you agree to our Terms of Service.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black.copy(alpha = 0.4f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                PremiumTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Email),
                    trailingIcon = { Icon(Icons.Outlined.Email, null, tint = Color.Black.copy(alpha = 0.4f)) }
                )

                Spacer(modifier = Modifier.height(24.dp))

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

                if (authState is AuthState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                PremiumButton(
                    text = "Continue",
                    onClick = {
                        authViewModel.login(email, password)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = authState !is AuthState.Loading && email.isNotBlank() && password.length >= 6
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { authViewModel.signup(email, password) }) {
                    Text("Don't have an account? Sign up", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                if (authState is AuthState.Loading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(color = Color.Black)
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
                .size(200.dp)
                .offset(x = 260.dp, y = 100.dp)
                .rotate(15f),
            color = Color(0xFFFFC833).copy(alpha = 0.1f),
            shape = RoundedCornerShape(32.dp)
        ) {}

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .offset(x = 40.dp, y = 450.dp)
                .rotate(-10f),
            tint = Color(0xFFFFC833).copy(alpha = 0.2f)
        )
    }
}

@Composable
fun AuthMethodCard(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f)),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.Black, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.Black)
        }
    }
}
