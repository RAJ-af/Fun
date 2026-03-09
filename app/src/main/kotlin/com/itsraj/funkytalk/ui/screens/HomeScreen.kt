package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.AddCircleOutline
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
import com.itsraj.funkytalk.ui.components.FunkyRoomCard
import com.itsraj.funkytalk.ui.components.RoomCardLayout
import com.itsraj.funkytalk.ui.theme.MangoYellow
import com.itsraj.funkytalk.viewmodel.VoiceRoomViewModel

@Composable
fun HomeScreen(navController: NavController, voiceRoomViewModel: VoiceRoomViewModel) {
    var selectedTag by remember { mutableStateOf("Recommended") }
    val tags = listOf("Recommended", "Language", "CN", "EN", "B.Indo", "Music", "Make friends")

    val dummyRooms = listOf(
        YeetalkRoom("English Practice Room", "English", "🇺🇸", 15, listOf("https://i.pravatar.cc/150?u=en1", "https://i.pravatar.cc/150?u=en2", "https://i.pravatar.cc/150?u=en3", "https://i.pravatar.cc/150?u=en4", "https://i.pravatar.cc/150?u=en5", "https://i.pravatar.cc/150?u=en6", "https://i.pravatar.cc/150?u=en7")),
        YeetalkRoom("Japanese Beginner Room", "Japanese", "🇯🇵", 8, listOf("https://i.pravatar.cc/150?u=jp1", "https://i.pravatar.cc/150?u=jp2", "https://i.pravatar.cc/150?u=jp3")),
        YeetalkRoom("Spanish Chat Room", "Spanish", "🇪🇸", 6, listOf("https://i.pravatar.cc/150?u=es1", "https://i.pravatar.cc/150?u=es2")),
        YeetalkRoom("German Conversation", "German", "🇩🇪", 4, listOf("https://i.pravatar.cc/150?u=de1", "https://i.pravatar.cc/150?u=de2", "https://i.pravatar.cc/150?u=de3")),
        YeetalkRoom("French Learning", "French", "🇫🇷", 12, listOf("https://i.pravatar.cc/150?u=fr1", "https://i.pravatar.cc/150?u=fr2", "https://i.pravatar.cc/150?u=fr3", "https://i.pravatar.cc/150?u=fr4")),
        YeetalkRoom("Korean Talk Room", "Korean", "🇰🇷", 9, listOf("https://i.pravatar.cc/150?u=kr1", "https://i.pravatar.cc/150?u=kr2", "https://i.pravatar.cc/150?u=kr3"))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // TAG System (Scrollable Row)
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(tags) { tag ->
                        val isSelected = tag == selectedTag
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { selectedTag = tag },
                            color = if (isSelected) MangoYellow else Color.White,
                            shape = RoundedCornerShape(20.dp),
                            tonalElevation = if (isSelected) 0.dp else 2.dp,
                            shadowElevation = if (isSelected) 4.dp else 1.dp
                        ) {
                            Text(
                                text = tag,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.Black else Color.Black.copy(alpha = 0.5f),
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }

                // Action Icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { /* Create Room */ }) {
                        Icon(
                            imageVector = Icons.Outlined.AddCircleOutline,
                            contentDescription = "Create Room",
                            tint = Color.Black.copy(alpha = 0.7f),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    IconButton(onClick = { /* Announcements */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Campaign,
                            contentDescription = "Announcements",
                            tint = Color.Black.copy(alpha = 0.7f),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            // Room Feed
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(dummyRooms) { index, room ->
                    // Alternate layout types: Layout A (Large) and Layout B (Grid/Small)
                    val layoutType = if (index % 3 == 0) RoomCardLayout.LARGE else RoomCardLayout.GRID
                    FunkyRoomCard(
                        hashtag = room.title.replace(" ", ""),
                        language = room.languageName,
                        flag = room.flag,
                        participantCount = room.participantCount,
                        avatars = room.avatars,
                        layoutType = layoutType
                    )
                }

                // Extra padding for bottom nav
                item { Spacer(modifier = Modifier.height(100.dp)) }
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
