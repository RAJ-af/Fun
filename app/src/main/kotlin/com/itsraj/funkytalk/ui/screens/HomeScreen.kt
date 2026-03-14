package com.itsraj.funkytalk.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Announcement
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.MicNone
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.itsraj.funkytalk.ui.components.FunkyRoomCard
import com.itsraj.funkytalk.ui.components.RoomCardLayout
import com.itsraj.funkytalk.ui.navigation.Screen
import com.itsraj.funkytalk.ui.theme.MangoYellow
import com.itsraj.funkytalk.viewmodel.AnnouncementViewModel
import com.itsraj.funkytalk.viewmodel.VoiceRoomViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    voiceRoomViewModel: VoiceRoomViewModel,
    authViewModel: com.itsraj.funkytalk.viewmodel.AuthViewModel,
    announcementViewModel: AnnouncementViewModel
) {
    val selectedTag by voiceRoomViewModel.selectedTag.collectAsState()
    val tags = listOf("Discover", "Languages", "Music", "Friends", "Games", "Study")

    val rooms by voiceRoomViewModel.rooms.collectAsState()
    val latestAnnouncement by announcementViewModel.latestAnnouncement.collectAsState()
    val userId = authViewModel.currentUser?.id ?: ""

    // Initial fetch when entering the screen
    LaunchedEffect(Unit) {
        voiceRoomViewModel.fetchRooms()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFFFBF0), Color(0xFFFFFFFF)),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 12.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Avatar Stack
                UserAvatarStack(
                    avatars = listOf(
                        "https://i.pravatar.cc/150?u=1",
                        "https://i.pravatar.cc/150?u=2",
                        "https://i.pravatar.cc/150?u=3"
                    )
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Center: TAG System
                val selectedIndex = tags.indexOf(selectedTag).coerceAtLeast(0)
                ScrollableTabRow(
                    selectedTabIndex = selectedIndex,
                    modifier = Modifier.weight(1f),
                    containerColor = Color.Transparent,
                    divider = {},
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        if (selectedIndex < tabPositions.size) {
                            val currentTab = tabPositions[selectedIndex]
                            WavyIndicator(
                                modifier = Modifier.tabIndicatorOffset(currentTab),
                                color = MangoYellow
                            )
                        }
                    }
                ) {
                    tags.forEachIndexed { index, tag ->
                        val isSelected = selectedTag == tag
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.15f else 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
                        )
                        val color by animateColorAsState(
                            targetValue = if (isSelected) MangoYellow else Color.Gray,
                            animationSpec = tween(durationMillis = 200)
                        )
                        val alpha by animateFloatAsState(
                            targetValue = if (isSelected) 1f else 0.6f,
                            animationSpec = tween(durationMillis = 200)
                        )

                        Tab(
                            selected = isSelected,
                            onClick = { voiceRoomViewModel.setSelectedTag(tag) },
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(48.dp),
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            selectedContentColor = MangoYellow,
                            unselectedContentColor = Color.Gray
                        ) {
                            Text(
                                text = tag,
                                modifier = Modifier.scale(scale).padding(horizontal = 4.dp),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = color.copy(alpha = alpha)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(32.dp))

                // Right: Action Icons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp) // Move slightly upward
                ) {
                    IconButton(
                        onClick = { navController.navigate(Screen.CreateRoom.route) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AddCircle,
                            contentDescription = "Create Room",
                            tint = MangoYellow,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp)) // Proper spacing
                    IconButton(
                        onClick = { navController.navigate(Screen.Announcements.route) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Campaign,
                            contentDescription = "Announcements",
                            tint = MangoYellow,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Announcement Banner
            latestAnnouncement?.let { announcement ->
                AnnouncementBanner(
                    title = announcement.title,
                    onClick = { navController.navigate(Screen.Announcements.route) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Room Feed
            if (rooms.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MangoYellow)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Looking for active rooms...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Button(
                            onClick = { voiceRoomViewModel.fetchRooms() },
                            modifier = Modifier.padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MangoYellow)
                        ) {
                            Text("Retry", color = Color.Black)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(rooms) { index, room ->
                        // Alternate layout types: Layout A (Large) and Layout B (Grid/Small)
                        val layoutType = if (index % 3 == 0) RoomCardLayout.LARGE else RoomCardLayout.GRID
                        FunkyRoomCard(
                            hashtag = room.title?.replace(" ", ""),
                            language = room.language,
                            countryCode = room.country_code,
                            tag = room.tag,
                            participantCount = room.participantCount,
                            avatars = room.participantAvatars,
                            onJoin = {
                                navController.navigate(Screen.VoiceRoom.createRoute(room.id, "listener"))
                            },
                            layoutType = layoutType
                        )
                    }

                    // Extra padding for bottom nav
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}

@Composable
fun AnnouncementBanner(title: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable(onClick = onClick),
        color = MangoYellow.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Announcement, contentDescription = null, tint = MangoYellow)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun UserAvatarStack(avatars: List<String>) {
    Row(
        modifier = Modifier.wrapContentSize(),
        horizontalArrangement = Arrangement.spacedBy((-14).dp)
    ) {
        avatars.forEach { avatarUrl ->
            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, Color.White, CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun WavyIndicator(modifier: Modifier, color: Color) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .padding(horizontal = 24.dp)
    ) {
        val path = Path().apply {
            moveTo(0f, size.height / 2)
            quadraticTo(
                size.width / 4, 0f,
                size.width / 2, size.height / 2
            )
            quadraticTo(
                size.width * 3 / 4, size.height,
                size.width, size.height / 2
            )
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
