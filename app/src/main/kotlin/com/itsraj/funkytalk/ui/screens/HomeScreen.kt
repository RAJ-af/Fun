package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.itsraj.funkytalk.ui.components.PremiumButton
import com.itsraj.funkytalk.ui.components.PremiumCard
import com.itsraj.funkytalk.ui.navigation.Screen
import com.itsraj.funkytalk.ui.theme.*
import com.itsraj.funkytalk.viewmodel.VoiceRoomViewModel

@Composable
fun HomeScreen(navController: NavController, voiceRoomViewModel: VoiceRoomViewModel) {
    val rooms by voiceRoomViewModel.rooms.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp)
        ) {
            item {
                HomeHeader()
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text(
                    text = "Active Voice Rooms",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (rooms.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No active rooms", color = Color.Black.copy(alpha = 0.3f))
                    }
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(rooms) { room ->
                            PremiumCard(
                                modifier = Modifier.width(260.dp)
                            ) {
                                Text(room.title, fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 1)
                                Text(room.language, color = Color.Black.copy(alpha = 0.5f), fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.1f)))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("${room.participants} participants", color = Color.Black.copy(alpha = 0.6f), fontSize = 12.sp)
                                    }

                                    PremiumButton(
                                        text = "Join",
                                        onClick = {
                                            voiceRoomViewModel.joinRoom(room.id)
                                            navController.navigate(Screen.VoiceRoom.route)
                                        },
                                        modifier = Modifier.height(36.dp).width(80.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text("Recommended Partners", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black), color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(5) { index ->
                PremiumCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.05f)))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Language Partner $index", fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("Native: Spanish • Learning: English", color = Color.Black.copy(alpha = 0.5f), fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            shape = CircleShape,
                            color = MangoYellow,
                            modifier = Modifier.size(40.dp)
                        ) {
                            // Placeholder for chat icon
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp)) // Bottom nav padding
            }
        }
    }
}

@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Welcome back,", color = Color.Black.copy(alpha = 0.5f), fontSize = 14.sp)
            Text("Funky User 👋", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black), color = Color.Black)
        }

        Row {
            IconButton(onClick = {}, modifier = Modifier.background(Color.Black.copy(alpha = 0.05f), CircleShape)) {
                Icon(Icons.Outlined.Search, null, tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(onClick = {}, modifier = Modifier.background(Color.Black.copy(alpha = 0.05f), CircleShape)) {
                Icon(Icons.Outlined.Notifications, null, tint = Color.Black)
            }
        }
    }
}
