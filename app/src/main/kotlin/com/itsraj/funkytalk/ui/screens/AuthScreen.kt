package com.itsraj.funkytalk.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    var isLoginMode by remember { mutableStateOf(false) }

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

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isLoginMode) "Welcome back," else "Create an account",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        letterSpacing = (-1.5).sp
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )

                Text(
                    text = if (isLoginMode) "We happy to see you here again. Enter your email address and password"
                          else "Create your account, it takes less than a minute. Enter your email and password",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black.copy(alpha = 0.5f),
                        lineHeight = 22.sp
                    ),
                    modifier = Modifier.align(Alignment.Start).padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

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

                if (authState is AuthState.Error) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = Color.Red,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                PremiumButton(
                    text = if (isLoginMode) "Log In" else "Create an Account",
                    onClick = {
                        if (isLoginMode) authViewModel.login(email, password)
                        else authViewModel.signup(email, password)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = authState !is AuthState.Loading && email.isNotBlank() && password.length >= 6
                )

                if (isLoginMode) {
                    TextButton(onClick = { /* Forgot Password */ }) {
                        Text("Forgot password?", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Black.copy(alpha = 0.1f))
                    Text(
                        "or continue with",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Black.copy(alpha = 0.3f))
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Black.copy(alpha = 0.1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                AuthMethodCard(
                    text = "Continue with Google",
                    icon = Icons.Default.Language, // Slightly better than Email
                    onClick = { authViewModel.loginWithGoogle() }
                )

                Spacer(modifier = Modifier.height(12.dp))

                AuthMethodCard(
                    text = if (isLoginMode) "Create an Account" else "Continue with Email",
                    icon = Icons.Outlined.Email,
                    onClick = { isLoginMode = !isLoginMode }
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (!isLoginMode) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Already have an account? ", color = Color.Black.copy(alpha = 0.5f), fontSize = 14.sp)
                        Text(
                            "Log In",
                            color = MangoYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { isLoginMode = true }
                        )
                    }
                }

                if (authState is AuthState.Loading) {
                    Spacer(modifier = Modifier.height(16.dp))
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

@Composable
fun AuthMethodCard(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        color = Color.Black
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
        }
    }
}
