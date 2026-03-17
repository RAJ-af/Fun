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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 16.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFF5F5F5), CircleShape)
                    ) {
                        Icon(
                            Icons.Rounded.ChevronLeft,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = room?.title ?: "Voice Room",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "#FT-${roomId.takeLast(4)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        )
                    }

                    IconButton(onClick = { /* Menu */ }) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = "Menu")
                    }
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
                Spacer(modifier = Modifier.height(40.dp))

                DynamicSpeakerStage(
                    speakers = speakers,
                    activeSpeakers = activeSpeakers
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Listener Indicator
                Surface(
                    onClick = { showParticipantsSheet = true },
                    color = MangoYellow.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.wrapContentSize()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Groups,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF8B6B00)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$listenerCount Listening",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFF8B6B00),
                                fontWeight = FontWeight.Bold
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
                // Talking Indicator
                val currentSpeakerUid = activeSpeakers.firstOrNull()
                val currentSpeaker = participants.find { it.user_id.hashCode() == currentSpeakerUid }

                AnimatedVisibility(
                    visible = currentSpeaker != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(MangoYellow, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${currentSpeaker?.profiles?.username} is talking...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Chat Preview overlay
                ChatPreviewOverlay(messages = messages.takeLast(2))

                Spacer(modifier = Modifier.height(24.dp))

                // Collapsible Controls
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CollapsibleControlBar(
                        expanded = controlsExpanded,
                        onToggle = { controlsExpanded = !controlsExpanded },
                        isMuted = isMuted,
                        unreadCount = unreadCount,
                        onMuteToggle = { viewModel.toggleMute() },
                        onRaiseHand = { viewModel.raiseHand(userId) },
                    onChatToggle = {
                        // Navigate to global chats screen as a shortcut
                        navController.navigate(com.itsraj.funkytalk.ui.navigation.Screen.Chats.route)
                        },
                        onLeave = {
                            viewModel.leaveRoom(userId) {
                                navController.popBackStack()
                            }
                        }
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
            .height(280.dp), // Approximate height for 2 rows
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
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
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            // Pulse Effect
            if (isSpeaking) {
                Box(
                    modifier = Modifier
                        .size(size + 12.dp)
                        .scale(pulseScale)
                        .background(MangoYellow.copy(alpha = 0.2f), CircleShape)
                )
            }

            Box(contentAlignment = Alignment.BottomCenter) {
                Surface(
                    modifier = Modifier
                        .size(size)
                        .border(
                            width = if (isSpeaking || speaker?.role == "host") 2.dp else 0.dp,
                            color = if (isSpeaking || speaker?.role == "host") MangoYellow else Color.Transparent,
                            shape = CircleShape
                        ),
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

                if (speaker?.role == "host") {
                    Surface(
                        color = MangoYellow,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .offset(y = 8.dp)
                    ) {
                        Text(
                            text = "Host",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        messages.forEach { message ->
            var visible by remember { mutableStateOf(true) }

            LaunchedEffect(message.id) {
                delay(8000)
                visible = false
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    if (message.profiles.avatar_url != null) {
                        AsyncImage(
                            model = message.profiles.avatar_url,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier.size(24.dp).background(Color(0xFFEEEEEE), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (message.profiles.username ?: "U").take(1).uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = message.profiles.username ?: "User",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = message.content,
                            fontSize = 13.sp,
                            color = Color.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CollapsibleControlBar(
    expanded: Boolean,
    onToggle: () -> Unit,
    isMuted: Boolean,
    unreadCount: Int,
    onMuteToggle: () -> Unit,
    onRaiseHand: () -> Unit,
    onChatToggle: () -> Unit,
    onLeave: () -> Unit
) {
    val transition = updateTransition(targetState = expanded, label = "ControlsTransition")

    val width by transition.animateDp(label = "Width") { state ->
        if (state) 280.dp else 56.dp
    }

    Surface(
        modifier = Modifier
            .width(width)
            .height(56.dp)
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    if (delta < -20 && expanded) onToggle()
                }
            )
            .clickable(enabled = !expanded) { onToggle() },
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        if (!expanded) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = "Expand",
                    tint = Color.Gray
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ControlIconSmall(
                    icon = if (isMuted) Icons.Rounded.MicOff else Icons.Rounded.Mic,
                    onClick = onMuteToggle,
                    active = !isMuted,
                    activeColor = MangoYellow,
                    inactiveColor = Color(0xFFF5F5F5)
                )
                ControlIconSmall(
                    icon = Icons.Rounded.PanTool,
                    onClick = onRaiseHand,
                    inactiveColor = Color(0xFFF5F5F5)
                )
                ControlIconSmall(
                    icon = Icons.Rounded.Chat,
                    onClick = onChatToggle,
                    badgeCount = unreadCount,
                    inactiveColor = Color(0xFFF5F5F5)
                )
                ControlIconSmall(
                    icon = Icons.Rounded.Block, // Changed to match "leave" style in screenshot
                    onClick = onLeave,
                    iconColor = Color(0xFFE57373),
                    inactiveColor = Color(0xFFF5F5F5)
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
