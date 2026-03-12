package com.itsraj.funkytalk.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.itsraj.funkytalk.ui.theme.MangoYellow

enum class RoomCardLayout {
    LARGE, GRID
}

@Composable
fun FunkyRoomCard(
    hashtag: String?,
    language: String?,
    countryCode: String? = null,
    tag: String? = null,
    participantCount: Int,
    avatars: List<String>,
    onJoin: () -> Unit,
    layoutType: RoomCardLayout = RoomCardLayout.LARGE,
    modifier: Modifier = Modifier
) {
    val cardHeight = if (layoutType == RoomCardLayout.LARGE) 200.dp else 160.dp
    val avatarCount = if (layoutType == RoomCardLayout.LARGE) 4 else 3
    val avatarSize = if (layoutType == RoomCardLayout.LARGE) 40.dp else 32.dp

    val flagToUse = countryCode ?: when(language?.uppercase()) {
        "EN" -> "us"
        "CN" -> "cn"
        "JP" -> "jp"
        "KR" -> "kr"
        "DE" -> "de"
        "FR" -> "fr"
        "ES" -> "es"
        "BR" -> "br"
        "ID" -> "id"
        "RU" -> "ru"
        "IT" -> "it"
        "TR" -> "tr"
        "VN" -> "vn"
        "TH" -> "th"
        else -> "un" // Unknown
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.02f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top section: Flag, Language, Tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularFlag(code = flagToUse, size = 20.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = (language ?: "UN").uppercase(),
                            color = Color.Black.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Tag Chip
                    tag?.let { selectedTag ->
                        val emoji = when(selectedTag.lowercase()) {
                            "music" -> "🎵"
                            "study" -> "📚"
                            "games" -> "🎮"
                            "friends" -> "👥"
                            "languages" -> "🗣"
                            else -> "✨"
                        }
                        Surface(
                            color = MangoYellow.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "$emoji ${selectedTag.replaceFirstChar { it.uppercase() }}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Middle section: Hashtag Title
                Text(
                    text = "#${hashtag ?: "Untitled"}",
                    color = Color.Black,
                    fontSize = if (layoutType == RoomCardLayout.LARGE) 22.sp else 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // Bottom section: Avatars & Participant Count & Join Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(horizontalArrangement = Arrangement.spacedBy((-12).dp)) {
                            avatars.take(avatarCount).forEach { avatarUrl ->
                                Surface(
                                    modifier = Modifier
                                        .size(avatarSize)
                                        .border(2.dp, Color.White, CircleShape),
                                    shape = CircleShape,
                                    color = Color(0xFFEEEEEE)
                                ) {
                                    AsyncImage(
                                        model = avatarUrl,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = Color.Black.copy(alpha = 0.3f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = participantCount.toString(),
                                color = Color.Black.copy(alpha = 0.3f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = onJoin,
                        colors = ButtonDefaults.buttonColors(containerColor = MangoYellow),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = "Join",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}
