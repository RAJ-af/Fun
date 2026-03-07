package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.itsraj.funkytalk.ui.components.YeetalkRoomCard
import com.itsraj.funkytalk.viewmodel.VoiceRoomViewModel

@Composable
fun HomeScreen(navController: NavController, voiceRoomViewModel: VoiceRoomViewModel) {
    val dummyRooms = listOf(
        YeetalkRoom("Japanese Beginner Room", "Japanese", "🇯🇵", 8, listOf("https://i.pravatar.cc/150?u=jp1", "https://i.pravatar.cc/150?u=jp2", "https://i.pravatar.cc/150?u=jp3")),
        YeetalkRoom("English Practice Room", "English", "🇺🇸", 15, listOf("https://i.pravatar.cc/150?u=en1", "https://i.pravatar.cc/150?u=en2", "https://i.pravatar.cc/150?u=en3", "https://i.pravatar.cc/150?u=en4", "https://i.pravatar.cc/150?u=en5", "https://i.pravatar.cc/150?u=en6", "https://i.pravatar.cc/150?u=en7")),
        YeetalkRoom("Spanish Chat Room", "Spanish", "🇪🇸", 6, listOf("https://i.pravatar.cc/150?u=es1", "https://i.pravatar.cc/150?u=es2")),
        YeetalkRoom("German Conversation", "German", "🇩🇪", 4, listOf("https://i.pravatar.cc/150?u=de1", "https://i.pravatar.cc/150?u=de2", "https://i.pravatar.cc/150?u=de3")),
        YeetalkRoom("French Learning", "French", "🇫🇷", 12, listOf("https://i.pravatar.cc/150?u=fr1", "https://i.pravatar.cc/150?u=fr2", "https://i.pravatar.cc/150?u=fr3", "https://i.pravatar.cc/150?u=fr4")),
        YeetalkRoom("Korean Talk Room", "Korean", "🇰🇷", 9, listOf("https://i.pravatar.cc/150?u=kr1", "https://i.pravatar.cc/150?u=kr2", "https://i.pravatar.cc/150?u=kr3"))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF121212),
                        Color(0xFF1E1E1E)
                    )
                )
            )
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Voice Rooms",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(dummyRooms) { room ->
                    YeetalkRoomCard(
                        hashtag = room.title.replace(" ", ""),
                        language = room.languageName,
                        flag = room.flag,
                        participantCount = room.participantCount,
                        avatars = room.avatars
                    )
                }
            }
        }
    }
}

data class YeetalkRoom(
    val title: String,
    val languageName: String,
    val flag: String,
    val participantCount: Int,
    val avatars: List<String>
)
