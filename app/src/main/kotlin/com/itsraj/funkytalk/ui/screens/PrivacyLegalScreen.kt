package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.itsraj.funkytalk.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyLegalScreen(navController: NavController) {
    var isChecked by remember { mutableStateOf(false) }
    var showPolicy by remember { mutableStateOf(false) }
    var showTerms by remember { mutableStateOf(false) }

    if (showPolicy || showTerms) {
        AlertDialog(
            onDismissRequest = {
                showPolicy = false
                showTerms = false
            },
            title = { Text(if (showPolicy) "Privacy Policy" else "Terms & Conditions") },
            text = {
                Text(
                    text = "This is placeholder text for the FunkyTalk MVP ${if (showPolicy) "Privacy Policy" else "Terms & Conditions"}.\n\n" +
                            "We value your privacy and aim to provide a safe platform for language exchange. " +
                            "Your data is used to match you with partners and improve your experience.",
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showPolicy = false
                    showTerms = false
                }) {
                    Text("Close")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Privacy & Legal", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Quick Summary",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "By using FunkyTalk, you agree to our terms. We collect basic profile data and chat history to provide language exchange services. We do not sell your personal data to third parties.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = { showPolicy = true }) {
                        Text("Privacy Policy", fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { showTerms = true }) {
                        Text("Terms & Conditions", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = "I agree to Privacy Policy and Terms & Conditions",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Button(
                    onClick = { navController.navigate(Screen.Login.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = isChecked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Continue", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }
}
