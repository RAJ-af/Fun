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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.itsraj.funkytalk.ui.components.PremiumButton
import com.itsraj.funkytalk.ui.components.PremiumCard
import com.itsraj.funkytalk.ui.navigation.Screen
import com.itsraj.funkytalk.ui.theme.BackgroundGradient
import com.itsraj.funkytalk.ui.theme.GradientPurpleBlue

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
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text(if (showPolicy) "Privacy Policy" else "Terms & Conditions", color = Color.White) },
            text = {
                Text(
                    text = "This is placeholder text for the FunkyTalk MVP ${if (showPolicy) "Privacy Policy" else "Terms & Conditions"}.\n\n" +
                            "We value your privacy and aim to provide a safe platform for language exchange. " +
                            "Your data is used to match you with partners and improve your experience.",
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showPolicy = false
                    showTerms = false
                }) {
                    Text("Close", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(BackgroundGradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = PaddingValues(24.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Privacy & Legal",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            PremiumCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Quick Summary",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "By using FunkyTalk, you agree to our terms. We collect basic profile data and chat history to provide language exchange services.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { showPolicy = true }) {
                        Text("Privacy Policy", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    }
                    TextButton(onClick = { showTerms = true }) {
                        Text("Terms & Conditions", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = Color.White.copy(alpha = 0.5f)
                        )
                    )
                    Text(
                        text = "I agree to Privacy Policy and Terms & Conditions",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                PremiumButton(
                    text = "Continue",
                    onClick = { navController.navigate(Screen.Login.route) },
                    enabled = isChecked,
                    modifier = Modifier.fillMaxWidth(),
                    gradient = GradientPurpleBlue
                )
            }
        }
    }
}
