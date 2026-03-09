package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.itsraj.funkytalk.ui.components.PremiumButton
import com.itsraj.funkytalk.ui.components.PremiumTextField
import com.itsraj.funkytalk.viewmodel.AuthViewModel
import com.itsraj.funkytalk.viewmodel.VoiceRoomViewModel

@Composable
fun CreateRoomScreen(
    navController: NavController,
    voiceRoomViewModel: VoiceRoomViewModel,
    authViewModel: AuthViewModel
) {
    var title by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("English") }
    var topic by remember { mutableStateOf("") }

    val userId = authViewModel.currentUser?.id ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Create Room",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        PremiumTextField(
            value = title,
            onValueChange = { title = it },
            label = "Room Title"
        )

        Spacer(modifier = Modifier.height(16.dp))

        PremiumTextField(
            value = language,
            onValueChange = { language = it },
            label = "Language"
        )

        Spacer(modifier = Modifier.height(16.dp))

        PremiumTextField(
            value = topic,
            onValueChange = { topic = it },
            label = "Topic (Optional)"
        )

        Spacer(modifier = Modifier.weight(1f))

        PremiumButton(
            text = "Create & Join",
            onClick = {
                if (title.isNotBlank()) {
                    voiceRoomViewModel.createRoom(title, language, userId) {
                        navController.navigate("voice_room")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        )
    }
}
