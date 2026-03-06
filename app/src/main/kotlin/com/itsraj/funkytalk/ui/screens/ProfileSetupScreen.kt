package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.itsraj.funkytalk.data.model.UserProfile
import com.itsraj.funkytalk.ui.components.PremiumButton
import com.itsraj.funkytalk.ui.components.PremiumTextField
import com.itsraj.funkytalk.ui.navigation.Screen
import com.itsraj.funkytalk.ui.theme.BackgroundGradient
import com.itsraj.funkytalk.ui.theme.GradientCyanBlue
import com.itsraj.funkytalk.ui.theme.GradientPinkPurple
import com.itsraj.funkytalk.viewmodel.AuthState
import com.itsraj.funkytalk.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(navController: NavController, authViewModel: AuthViewModel) {
    var name by remember { mutableStateOf("") }
    var nativeLanguage by remember { mutableStateOf("") }
    var learningLanguage by remember { mutableStateOf("") }

    val languages = listOf(
        "English", "Spanish", "French", "German", "Chinese", "Japanese", "Korean",
        "Russian", "Portuguese", "Italian", "Arabic", "Hindi", "Bengali", "Turkish",
        "Vietnamese", "Polish", "Dutch", "Thai", "Indonesian", "Greek", "Hebrew",
        "Swedish", "Norwegian", "Danish", "Finnish", "Czech", "Hungarian", "Romanian",
        "Slovak", "Ukrainian"
    )

    var nativeExpanded by remember { mutableStateOf(false) }
    var learningExpanded by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.ProfileSetup.route) { inclusive = true }
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Text(
                text = "Set Up Profile",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            )

            Text(
                text = "Step 1: The Basics",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            PremiumTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                trailingIcon = { Icon(Icons.Outlined.Person, null, tint = Color.White.copy(alpha = 0.4f)) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Native Language
            Box(modifier = Modifier.fillMaxWidth()) {
                PremiumTextField(
                    value = nativeLanguage,
                    onValueChange = {},
                    label = "Native Language",
                    modifier = Modifier.clickable { nativeExpanded = true },
                    trailingIcon = {
                        IconButton(onClick = { nativeExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, null, tint = Color.White.copy(alpha = 0.4f))
                        }
                    }
                )
                DropdownMenu(
                    expanded = nativeExpanded,
                    onDismissRequest = { nativeExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang, color = Color.White) },
                            onClick = {
                                nativeLanguage = lang
                                nativeExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Learning Language
            Box(modifier = Modifier.fillMaxWidth()) {
                PremiumTextField(
                    value = learningLanguage,
                    onValueChange = {},
                    label = "Learning Language",
                    modifier = Modifier.clickable { learningExpanded = true },
                    trailingIcon = {
                        IconButton(onClick = { learningExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, null, tint = Color.White.copy(alpha = 0.4f))
                        }
                    }
                )
                DropdownMenu(
                    expanded = learningExpanded,
                    onDismissRequest = { learningExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang, color = Color.White) },
                            onClick = {
                                learningLanguage = lang
                                learningExpanded = false
                            }
                        )
                    }
                }
            }

            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            PremiumButton(
                text = "Complete Step 1",
                onClick = {
                    val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        val profile = UserProfile(
                            uid = user.uid,
                            name = name,
                            email = user.email ?: "",
                            nativeLanguage = nativeLanguage,
                            learningLanguage = learningLanguage,
                            createdAt = System.currentTimeMillis()
                        )
                        authViewModel.saveProfile(profile)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                gradient = GradientCyanBlue,
                enabled = authState !is AuthState.Loading && name.isNotBlank() && nativeLanguage.isNotBlank() && learningLanguage.isNotBlank()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "You can add more details later in settings.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.4f)
            )
        }
    }
}
