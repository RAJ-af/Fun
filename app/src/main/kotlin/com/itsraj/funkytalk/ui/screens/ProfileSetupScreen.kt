package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.itsraj.funkytalk.data.model.UserProfile
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Up Your Profile", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Step 1: The Basics",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Native Language Dropdown
            ExposedDropdownMenuBox(
                expanded = nativeExpanded,
                onExpandedChange = { nativeExpanded = !nativeExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = nativeLanguage,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Native Language") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = nativeExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = nativeExpanded,
                    onDismissRequest = { nativeExpanded = false }
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang) },
                            onClick = {
                                nativeLanguage = lang
                                nativeExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Learning Language Dropdown
            ExposedDropdownMenuBox(
                expanded = learningExpanded,
                onExpandedChange = { learningExpanded = !learningExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = learningLanguage,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Learning Language") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = learningExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = learningExpanded,
                    onDismissRequest = { learningExpanded = false }
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang) },
                            onClick = {
                                learningLanguage = lang
                                learningExpanded = false
                            }
                        )
                    }
                }
            }

            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = authState !is AuthState.Loading && name.isNotBlank() && nativeLanguage.isNotBlank() && learningLanguage.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Complete Step 1", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "You can add more details later in settings.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}
