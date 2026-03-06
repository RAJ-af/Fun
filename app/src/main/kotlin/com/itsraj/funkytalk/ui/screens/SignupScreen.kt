package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun SignupScreen(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.ProfileIncomplete) {
            navController.navigate(Screen.ProfileSetup.route) {
                popUpTo(Screen.Signup.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
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
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Join FunkyTalk",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    letterSpacing = (-1).sp
                ),
                modifier = Modifier.align(Alignment.Start).padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

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

            Spacer(modifier = Modifier.height(24.dp))

            PremiumTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                text = "Create Account",
                onClick = {
                    if (password == confirmPassword) {
                        authViewModel.signup(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = authState !is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = { navController.navigate(Screen.Privacy.route) }) {
                Text(
                    "By signing up, you agree to our Privacy Policy",
                    fontSize = 14.sp,
                    color = Color.Black.copy(alpha = 0.5f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
                Text("Already have an account? Login", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
