package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.itsraj.funkytalk.ui.components.VoiceRoomGridCard
import com.itsraj.funkytalk.viewmodel.VoiceRoomViewModel

@Composable
fun HomeScreen(navController: NavController, voiceRoomViewModel: VoiceRoomViewModel) {
    val dummyRooms = listOf(
        DummyRoom("Japanese Beginner Room", "Japanese", "JP", 8, Color(0xFFFFF9C4), Color(0xFFFBC02D)),
        DummyRoom("English Practice Room", "English", "EN", 15, Color(0xFFE3F2FD), Color(0xFF1976D2)),
        DummyRoom("Spanish Chat Room", "Spanish", "ES", 6, Color(0xFFFFEBEE), Color(0xFFD32F2F)),
        DummyRoom("German Conversation", "German", "DE", 4, Color(0xFFF1F8E9), Color(0xFF689F38)),
        DummyRoom("French Learning", "French", "FR", 12, Color(0xFFF3E5F5), Color(0xFF7B1FA2)),
        DummyRoom("Korean Talk Room", "Korean", "KR", 9, Color(0xFFE0F7FA), Color(0xFF0097A7))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Voice Rooms",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            ),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(dummyRooms) { room ->
                VoiceRoomGridCard(
                    title = room.title,
                    languageName = room.languageName,
                    languageCode = room.languageCode,
                    participantCount = room.participantCount,
                    badgeColor = room.badgeColor,
                    badgeTextColor = room.badgeTextColor,
                    onClick = { /* No connection logic yet */ }
                )
            }
        }
    }
}

data class DummyRoom(
    val title: String,
    val languageName: String,
    val languageCode: String,
    val participantCount: Int,
    val badgeColor: Color,
    val badgeTextColor: Color
)
