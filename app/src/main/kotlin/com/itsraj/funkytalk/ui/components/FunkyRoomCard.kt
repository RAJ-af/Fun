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
    hashtag: String,
    language: String,
    languageCode: String,
    participantCount: Int,
    avatars: List<String>,
    onJoin: () -> Unit,
    layoutType: RoomCardLayout = RoomCardLayout.LARGE,
    modifier: Modifier = Modifier
) {
    val cardHeight = if (layoutType == RoomCardLayout.LARGE) 200.dp else 160.dp
    val avatarCount = if (layoutType == RoomCardLayout.LARGE) 4 else 3
    val avatarSize = if (layoutType == RoomCardLayout.LARGE) 40.dp else 32.dp

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
            // Subtle gradient overlay for text readability at bottom if there was an image bg,
            // but we use white bg as requested. Still adding it for "premium" feel.
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
                // Top section: Language & Participant Count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF5F5F5))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        AsyncImage(
                            model = "https://hatscripts.github.io/circle-flags/flags/${languageCode.lowercase()}.svg",
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = language,
                            color = Color.Black.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = Color.Black.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = participantCount.toString(),
                            color = Color.Black.copy(alpha = 0.3f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Middle section: Hashtag Title
                Text(
                    text = "#$hashtag",
                    color = Color.Black,
                    fontSize = if (layoutType == RoomCardLayout.LARGE) 20.sp else 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Bottom section: Avatar Collage & Join Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy((-10).dp)
                    ) {
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

                        if (avatars.size > avatarCount) {
                            Box(
                                modifier = Modifier
                                    .size(avatarSize)
                                    .clip(CircleShape)
                                    .background(MangoYellow)
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+${avatars.size - avatarCount}",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Button(
                        onClick = onJoin,
                        colors = ButtonDefaults.buttonColors(containerColor = MangoYellow),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = "Join",
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
