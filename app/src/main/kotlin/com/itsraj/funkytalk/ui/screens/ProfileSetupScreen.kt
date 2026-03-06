package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.itsraj.funkytalk.data.model.UserProfile
import com.itsraj.funkytalk.ui.components.PremiumButton
import com.itsraj.funkytalk.ui.components.PremiumTextField
import com.itsraj.funkytalk.ui.navigation.Screen
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
            Spacer(modifier = Modifier.height(64.dp))

            Text(
                text = "Set Up Profile",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    letterSpacing = (-1).sp
                )
            )

            Text(
                text = "Step 1: The Basics",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            PremiumTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                trailingIcon = { Icon(Icons.Outlined.Person, null, tint = Color.Black.copy(alpha = 0.4f)) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Native Language
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nativeLanguage,
                    onValueChange = {},
                    label = { Text("Native Language") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().clickable { nativeExpanded = true },
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        IconButton(onClick = { nativeExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, null, tint = Color.Black.copy(alpha = 0.4f))
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black.copy(alpha = 0.3f),
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black.copy(alpha = 0.5f)
                    )
                )
                DropdownMenu(
                    expanded = nativeExpanded,
                    onDismissRequest = { nativeExpanded = false },
                    modifier = Modifier.background(Color.White).fillMaxWidth(0.8f)
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang, color = Color.Black) },
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
                OutlinedTextField(
                    value = learningLanguage,
                    onValueChange = {},
                    label = { Text("Learning Language") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().clickable { learningExpanded = true },
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        IconButton(onClick = { learningExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, null, tint = Color.Black.copy(alpha = 0.4f))
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black.copy(alpha = 0.3f),
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black.copy(alpha = 0.5f)
                    )
                )
                DropdownMenu(
                    expanded = learningExpanded,
                    onDismissRequest = { learningExpanded = false },
                    modifier = Modifier.background(Color.White).fillMaxWidth(0.8f)
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang, color = Color.Black) },
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
                    color = Color.Red,
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
                enabled = authState !is AuthState.Loading && name.isNotBlank() && nativeLanguage.isNotBlank() && learningLanguage.isNotBlank()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "You can add more details later in settings.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black.copy(alpha = 0.4f)
            )
        }
    }
}
