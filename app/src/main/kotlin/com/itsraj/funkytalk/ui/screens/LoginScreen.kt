package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import com.itsraj.funkytalk.ui.theme.BackgroundGradient
import com.itsraj.funkytalk.ui.theme.GradientCyanBlue
import com.itsraj.funkytalk.ui.theme.GradientPinkPurple
import com.itsraj.funkytalk.viewmodel.AuthState
import com.itsraj.funkytalk.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showEmailLogin by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (showEmailLogin) showEmailLogin = false else navController.popBackStack()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = if (!showEmailLogin) "Welcome to FunkyTalk" else "Sign In",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-1).sp
                ),
                modifier = Modifier.align(Alignment.Start).padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            if (!showEmailLogin) {
                SocialLoginCard(
                    text = "Continue with Google",
                    icon = Icons.Outlined.Email,
                    onClick = { /* enabled later */ }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SocialLoginCard(
                    text = "Continue with Facebook",
                    icon = Icons.Outlined.Email,
                    onClick = { /* enabled later */ }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SocialLoginCard(
                    text = "Continue with Email",
                    icon = Icons.Outlined.Email,
                    onClick = { showEmailLogin = true },
                    gradient = GradientCyanBlue
                )

                Spacer(modifier = Modifier.weight(1f))

                TextButton(onClick = { navController.navigate(Screen.Signup.route) }) {
                    Text("Don't have an account? Sign up", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(24.dp))
            } else {
                PremiumTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Email),
                    trailingIcon = { Icon(Icons.Outlined.Email, null, tint = Color.White.copy(alpha = 0.4f)) }
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
                            Icon(imageVector = image, contentDescription = null, tint = Color.White.copy(alpha = 0.4f))
                        }
                    }
                )

                if (authState is AuthState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                PremiumButton(
                    text = "Sign In",
                    onClick = { authViewModel.login(email, password) },
                    modifier = Modifier.fillMaxWidth(),
                    gradient = GradientPinkPurple,
                    enabled = authState !is AuthState.Loading
                )

                if (authState is AuthState.Loading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun SocialLoginCard(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    gradient: List<Color>? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (gradient == null) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else Color.Transparent
        )
    ) {
        val boxModifier = if (gradient != null) Modifier.background(Brush.linearGradient(gradient)) else Modifier

        Row(
            modifier = boxModifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        }
    }
}
