package com.itsraj.funkytalk.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.itsraj.funkytalk.data.model.ParticipantWithProfile
import com.itsraj.funkytalk.data.model.RoomMessageWithProfile
import com.itsraj.funkytalk.ui.theme.MangoYellow
import com.itsraj.funkytalk.viewmodel.VoiceRoomViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceRoomScreen(
    navController: NavController,
    viewModel: VoiceRoomViewModel,
    authViewModel: com.itsraj.funkytalk.viewmodel.AuthViewModel,
    roomId: String,
    role: String
) {
    val isMuted by viewModel.isMuted.collectAsState()
    val rooms by viewModel.rooms.collectAsState()
    val participants by viewModel.participants.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val activeSpeakers by viewModel.activeSpeakers.collectAsState()
    val unreadCount by viewModel.unreadMessageCount.collectAsState()

    val room = rooms.find { it.id == roomId }
    val userId = authViewModel.currentUser?.id ?: ""

    var showParticipantsSheet by remember { mutableStateOf(false) }
    var showChatSheet by remember { mutableStateOf(false) }
    var controlsExpanded by remember { mutableStateOf(false) }

    val speakers = participants.filter { it.role == "host" || it.role == "speaker" }
    val listenerCount = participants.filter { it.role == "listener" }.size

    LaunchedEffect(roomId) {
        viewModel.joinRoom(roomId, userId)
    }

    // Auto-collapse timer
    LaunchedEffect(controlsExpanded) {
        if (controlsExpanded) {
            delay(5000)
            controlsExpanded = false
        }
    }

    DisposableEffect(roomId) {
        onDispose {
            viewModel.leaveRoom(userId)
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 12.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFF8F8F8), CircleShape)
                        .align(Alignment.CenterStart)
                ) {
                    Icon(
                        Icons.Rounded.ChevronLeft,
                        contentDescription = "Back",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF666666)
                    )
                }

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = room?.title ?: "Voice Room",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.Black
                        )
                    )
                    Text(
                        text = "#FT-${roomId.takeLast(4).uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.LightGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }

                IconButton(
                    onClick = { /* Menu */ },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        Icons.Default.MoreHoriz,
                        contentDescription = "Menu",
                        tint = Color.Black
                    )
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            // Stage (Dynamic Speakers Grid)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                DynamicSpeakerStage(
                    speakers = speakers,
                    activeSpeakers = activeSpeakers
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Listener Indicator
                Surface(
                    onClick = { showParticipantsSheet = true },
                    color = Color(0xFFFEF9E7),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.wrapContentSize()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Groups,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFFBCA136)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$listenerCount Listening",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFFBCA136),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            // Bottom Floating UI
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.Start
            ) {
                val currentSpeakerUid = activeSpeakers.firstOrNull()
                val currentSpeaker = participants.find { it.user_id.hashCode() == currentSpeakerUid }

                // Chat Preview overlay
                ChatPreviewOverlay(messages = messages.takeLast(2))

                Spacer(modifier = Modifier.height(24.dp))

                // Collapsible Controls
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
                    ModernControlBar(
                        expanded = controlsExpanded,
                        onToggle = { controlsExpanded = !controlsExpanded },
                        isMuted = isMuted,
                        unreadCount = unreadCount,
                        onMuteToggle = { viewModel.toggleMute() },
                        onRaiseHand = { viewModel.raiseHand(userId) },
                        onChatToggle = {
                            navController.navigate(com.itsraj.funkytalk.ui.navigation.Screen.Chats.route)
                        },
                        onLeave = {
                            viewModel.leaveRoom(userId) {
                                navController.popBackStack()
                            }
                        },
                        currentSpeaker = currentSpeaker?.profiles?.username ?: ""
                    )
                }
            }
        }

        if (showParticipantsSheet) {
            ParticipantsBottomSheet(
                participants = participants,
                onDismiss = { showParticipantsSheet = false }
            )
        }

    }
}

@Composable
fun DynamicSpeakerStage(
    speakers: List<ParticipantWithProfile>,
    activeSpeakers: Set<Int>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp), // Adjusted height to accommodate badges
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = false
    ) {
        items(6) { index ->
            val speaker = speakers.getOrNull(index)
            SpeakerAvatarItem(
                speaker = speaker,
                size = 76.dp,
                isSpeaking = speaker != null && activeSpeakers.contains(speaker.user_id.hashCode())
            )
        }
    }
}

@Composable
fun SpeakerAvatarItem(
    speaker: ParticipantWithProfile?,
    size: androidx.compose.ui.unit.Dp,
    isSpeaking: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            // Host Ring / Speaking Indicator (Static Ring as per new requirement)
            if (speaker?.role == "host" || isSpeaking) {
                AsyncImage(
                    model = "https://itsraj555-resurses.hf.space/icons/host-ring-fixed.png",
                    contentDescription = null,
                    modifier = Modifier.size(size + 12.dp)
                )
            }

            Surface(
                modifier = Modifier.size(size),
                shape = CircleShape,
                color = Color(0xFFF5F5F5)
            ) {
                if (speaker?.profiles?.avatar_url != null) {
                    AsyncImage(
                        model = speaker.profiles.avatar_url,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = null,
                            modifier = Modifier.size(size / 2.5f),
                            tint = Color.LightGray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (speaker?.role == "host") {
            Surface(
                color = MangoYellow,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = "Host",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        } else {
            // Empty space to maintain text alignment if not host
            Spacer(modifier = Modifier.height(14.dp))
        }

        Text(
            text = speaker?.profiles?.username ?: "Empty",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = if (speaker == null) Color.Gray else Color.Black
            ),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ChatPreviewOverlay(messages: List<RoomMessageWithProfile>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        messages.forEach { message ->
            var visible by remember { mutableStateOf(true) }

            LaunchedEffect(message.id) {
                delay(5000) // Fade after 5 seconds
                visible = false
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(500)) + slideInVertically(),
                exit = fadeOut(animationSpec = tween(1000))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 1.dp)
                ) {
                    if (message.profiles.avatar_url != null) {
                        AsyncImage(
                            model = message.profiles.avatar_url,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier.size(20.dp).background(Color(0xFFF0F0F0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (message.profiles.username ?: "U").take(1).uppercase(),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${message.profiles.username ?: "User"}: ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = message.content,
                                fontSize = 11.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Light,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernControlBar(
    expanded: Boolean,
    onToggle: () -> Unit,
    isMuted: Boolean,
    unreadCount: Int,
    onMuteToggle: () -> Unit,
    onRaiseHand: () -> Unit,
    onChatToggle: () -> Unit,
    onLeave: () -> Unit,
    currentSpeaker: String
) {
    val transition = updateTransition(targetState = expanded, label = "ControlsTransition")

    val width by transition.animateDp(label = "Width") { state ->
        if (state) 280.dp else 160.dp
    }

    Surface(
        modifier = Modifier
            .width(width)
            .height(56.dp)
            .shadow(12.dp, RoundedCornerShape(28.dp))
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    if (delta < -20 && expanded) onToggle()
                }
            ),
        shape = RoundedCornerShape(28.dp),
        color = Color.White
    ) {
        if (!expanded) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onToggle() }
                    .padding(start = 20.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (currentSpeaker.isNotEmpty()) "Talking..." else "No one is talking",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onToggle() },
                    shape = RoundedCornerShape(12.dp),
                    color = MangoYellow
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Rounded.ChevronLeft,
                            contentDescription = "Expand",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ControlIconSmall(
                    icon = if (isMuted) Icons.Rounded.MicOff else Icons.Rounded.Mic,
                    onClick = onMuteToggle,
                    active = true, // Mic is always yellow as per "Mic -> yellow filled circle"
                    activeColor = MangoYellow,
                    iconColor = if (isMuted) Color.Gray else Color.Black
                )
                ControlIconSmall(
                    icon = Icons.Rounded.BackHand,
                    onClick = onRaiseHand,
                    inactiveColor = Color(0xFFFEF9E7),
                    iconColor = Color(0xFFBCA136)
                )
                ControlIconSmall(
                    icon = Icons.Rounded.Chat,
                    onClick = onChatToggle,
                    badgeCount = unreadCount,
                    inactiveColor = Color(0xFFFEF9E7),
                    iconColor = Color(0xFFBCA136)
                )
                ControlIconSmall(
                    icon = Icons.Rounded.Close,
                    onClick = onLeave,
                    iconColor = Color(0xFFE57373),
                    inactiveColor = Color(0xFFFFEBEE)
                )
            }
        }
    }
}

@Composable
fun ControlIconSmall(
    icon: ImageVector,
    onClick: () -> Unit,
    active: Boolean = false,
    iconColor: Color = Color.Black,
    activeColor: Color = MangoYellow,
    inactiveColor: Color = Color.Transparent,
    badgeCount: Int = 0
) {
    Box(contentAlignment = Alignment.TopEnd) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(44.dp)
                .background(if (active) activeColor else inactiveColor, CircleShape)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (active) Color.Black else iconColor
            )
        }

        if (badgeCount > 0) {
            Surface(
                color = Color.Red,
                shape = CircleShape,
                modifier = Modifier.size(16.dp).offset(x = 4.dp, y = (-4).dp),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = badgeCount.toString(), color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantsBottomSheet(
    participants: List<ParticipantWithProfile>,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 40.dp)) {
            Text(
                text = "Participants",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
            )
            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                val speakers = participants.filter { it.role != "listener" }
                val listeners = participants.filter { it.role == "listener" }

                item { SectionHeader("Speakers (${speakers.size})") }
                items(speakers) { ParticipantItem(it) }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                item { SectionHeader("Listeners (${listeners.size})") }
                items(listeners) { ParticipantItem(it) }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray, fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun ParticipantItem(participant: ParticipantWithProfile) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (participant.profiles.avatar_url != null) {
            AsyncImage(
                model = participant.profiles.avatar_url,
                contentDescription = null,
                modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFEEEEEE)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier.size(44.dp).background(Color(0xFFF5F5F5), CircleShape), contentAlignment = Alignment.Center) {
                Text(text = (participant.profiles.username ?: "U").take(1).uppercase(), color = Color.Black.copy(alpha = 0.3f), fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = participant.profiles.username ?: "User", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(text = participant.role.replaceFirstChar { it.uppercase() }, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
