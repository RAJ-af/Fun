package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.itsraj.funkytalk.ui.theme.MangoYellow

data class Message(
    val id: String,
    val text: String,
    val senderId: String,
    val timestamp: Long,
    val isCorrection: Boolean = false,
    val originalText: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualChatScreen(navController: NavController, userName: String, photoUrl: String) {
    var messageText by remember { mutableStateOf("") }

    val messages = listOf(
        Message("1", "Hi! How is your Spanish going?", "other", 1625000000),
        Message("2", "It is good! I practice every day.", "me", 1625000100),
        Message("3", "It is good! I practice every day.", "me", 1625000200, true, "It is good! I practice every day."),
        Message("4", "Great! Do you want to try a voice call later?", "other", 1625000300)
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.05f)))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(userName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Call */ }) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 0.dp,
                modifier = Modifier.navigationBarsPadding().imePadding(),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Voice Message */ }) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = Color.Black)
                    }
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...") },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.1f),
                            focusedContainerColor = Color.Black.copy(alpha = 0.05f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.05f)
                        ),
                        trailingIcon = {
                            IconButton(onClick = { /* Translate */ }) {
                                Icon(Icons.Default.Translate, contentDescription = null, tint = Color.Black.copy(alpha = 0.4f))
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = { /* Send */ },
                        modifier = Modifier.size(48.dp).background(MangoYellow, CircleShape)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message)
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val isMe = message.senderId == "me"
    val alignment = if (isMe) Alignment.End else Alignment.Start
    val color = if (isMe) MangoYellow else Color.Black.copy(alpha = 0.05f)
    val textColor = Color.Black

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Surface(
            color = color,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 2.dp,
                bottomEnd = if (isMe) 2.dp else 16.dp
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (message.isCorrection) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = Color.Black.copy(alpha = 0.5f), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Correction", fontSize = 10.sp, color = Color.Black.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
                    }
                    Text(message.originalText ?: "", fontSize = 12.sp, color = textColor.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(message.text, color = textColor, fontWeight = if (isMe) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}
