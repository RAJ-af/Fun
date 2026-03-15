package com.itsraj.funkytalk.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
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

    val speakers = participants.filter { it.role == "host" || it.role == "speaker" }
    val listenerCount = participants.filter { it.role == "listener" }.size

    LaunchedEffect(roomId) {
        viewModel.joinRoom(roomId, userId)
    }

    DisposableEffect(roomId) {
        onDispose {
            viewModel.leaveRoom(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = room?.title ?: "Voice Room",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Menu */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Stage (Centered Speakers)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "SPEAKERS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Gray.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                )
                Spacer(modifier = Modifier.height(48.dp))

                DynamicSpeakerGrid(speakers = speakers, activeSpeakers = activeSpeakers)

                Spacer(modifier = Modifier.height(48.dp))

                // Listeners Card placed closer to speakers
                ListenersCard(count = listenerCount, onClick = { showParticipantsSheet = true })
            }

            // Bottom UI Stack
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Chat Preview
                ChatPreview(messages = messages.takeLast(2))

                Spacer(modifier = Modifier.height(24.dp))

                // Floating Controls
                FloatingControlsBar(
                    isMuted = isMuted,
                    unreadCount = unreadCount,
                    onMuteToggle = { viewModel.toggleMute() },
                    onRaiseHand = { viewModel.raiseHand(userId) },
                    onChatToggle = {
                        showChatSheet = true
                        viewModel.resetUnreadCount()
                    },
                    onLeave = {
                        viewModel.leaveRoom(userId) {
                            navController.popBackStack()
                        }
                    }
                )
            }
        }

        if (showParticipantsSheet) {
            ParticipantsBottomSheet(
                participants = participants,
                onDismiss = { showParticipantsSheet = false }
            )
        }

        if (showChatSheet) {
            ChatBottomSheet(
                messages = messages,
                onSendMessage = { content -> viewModel.sendMessage(userId, content) },
                onDismiss = { showChatSheet = false },
                currentUserId = userId
            )
        }
    }
}

@Composable
fun DynamicSpeakerGrid(speakers: List<ParticipantWithProfile>, activeSpeakers: Set<Int>) {
    val count = speakers.size

    Box(modifier = Modifier.fillMaxWidth().height(280.dp), contentAlignment = Alignment.Center) {
        when {
            count == 1 -> {
                Row(horizontalArrangement = Arrangement.spacedBy(40.dp), verticalAlignment = Alignment.CenterVertically) {
                    SpeakerAvatar(speaker = speakers[0], size = 120.dp, isSpeaking = activeSpeakers.isNotEmpty())
                    SpeakerPlaceholder(120.dp)
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(40.dp),
                    verticalArrangement = Arrangement.spacedBy(40.dp),
                    modifier = Modifier.wrapContentHeight()
                ) {
                    items(speakers) { speaker ->
                        SpeakerAvatar(speaker = speaker, size = 100.dp, isSpeaking = false) // Actual logic for multiple speakers would check activeSpeakers mapping
                    }
                    // Fill remaining slots up to 4 for the "Stage" feel
                    if (count < 4) {
                        items(4 - count) {
                            SpeakerPlaceholder(100.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpeakerPlaceholder(size: androidx.compose.ui.unit.Dp) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(size)
                .background(Color(0xFFF9F9F9), CircleShape)
                .border(1.dp, Color.LightGray.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            MyIcon(Icons.Rounded.Add, contentDescription = null, size = (size.value / 3).dp, tint = Color.LightGray)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Invite speaker", fontSize = 10.sp, color = Color.LightGray, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SpeakerAvatar(speaker: ParticipantWithProfile, size: androidx.compose.ui.unit.Dp = 100.dp, isSpeaking: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val ringScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            // Stage Focus Glow (Soft mango-yellow)
            Box(
                modifier = Modifier
                    .size(size + 20.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(MangoYellow.copy(alpha = 0.15f), Color.Transparent)
                        ),
                        CircleShape
                    )
            )

            if (isSpeaking) {
                Box(
                    modifier = Modifier
                        .size(size)
                        .scale(ringScale)
                        .border(4.dp, MangoYellow.copy(alpha = ringAlpha), CircleShape)
                )
            }

            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5))
                        .border(
                            width = if (isSpeaking) 4.dp else 1.dp,
                            color = if (isSpeaking) MangoYellow else Color(0xFFEEEEEE),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (speaker.profiles.avatar_url != null) {
                        AsyncImage(
                            model = speaker.profiles.avatar_url,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = (speaker.profiles.username ?: "U").take(1).uppercase(),
                            fontWeight = FontWeight.Black,
                            fontSize = (size.value / 2.5).sp,
                            color = MangoYellow
                        )
                    }
                }

                if (speaker.role == "host") {
                    Surface(
                        color = MangoYellow,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.offset(y = 2.dp),
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = "HOST",
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = speaker.profiles.username ?: "User",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun ListenersCard(count: Int, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MyIcon(Icons.Rounded.Groups, contentDescription = null, size = 20.dp, tint = MangoYellow)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "$count Listening",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Tap to see participants", fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.LightGray
            )
        }
    }
}

@Composable
fun ChatPreview(messages: List<RoomMessageWithProfile>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        messages.forEach { message ->
            Row(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.04f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${message.profiles.username}: ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.6f)
                )
                Text(
                    text = message.content,
                    fontSize = 12.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun FloatingControlsBar(
    isMuted: Boolean,
    unreadCount: Int,
    onMuteToggle: () -> Unit,
    onRaiseHand: () -> Unit,
    onChatToggle: () -> Unit,
    onLeave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ControlCircleButton(
            icon = if (isMuted) Icons.Rounded.MicOff else Icons.Rounded.Mic,
            onClick = onMuteToggle,
            active = !isMuted,
            activeColor = MangoYellow
        )
        ControlCircleButton(
            icon = Icons.Rounded.PanTool,
            onClick = onRaiseHand
        )
        ControlCircleButton(
            icon = Icons.Rounded.Chat,
            onClick = onChatToggle,
            badgeCount = unreadCount
        )
        ControlCircleButton(
            icon = Icons.Rounded.CallEnd,
            onClick = onLeave,
            containerColor = Color.White,
            iconColor = Color.Red,
            activeColor = Color.Red,
            isLeave = true
        )
    }
}

@Composable
fun ControlCircleButton(
    icon: ImageVector,
    onClick: () -> Unit,
    active: Boolean = false,
    containerColor: Color = Color.White,
    iconColor: Color = Color.Black,
    activeColor: Color = Color.White,
    badgeCount: Int = 0,
    isLeave: Boolean = false
) {
    Box(contentAlignment = Alignment.TopEnd) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .shadow(if (isLeave) 8.dp else 4.dp, CircleShape)
                .clip(CircleShape)
                .background(if (active) activeColor else containerColor)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            MyIcon(icon, contentDescription = null, size = 28.dp, tint = if (active && !isLeave) Color.Black else iconColor)
        }

        if (badgeCount > 0) {
            Surface(
                color = Color.Red,
                shape = CircleShape,
                modifier = Modifier.size(20.dp).offset(x = 4.dp, y = (-4).dp),
                border = BorderStroke(2.dp, Color.White)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = badgeCount.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MyIcon(imageVector: ImageVector, contentDescription: String?, size: androidx.compose.ui.unit.Dp, tint: Color) {
    androidx.compose.material3.Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = Modifier.size(size),
        tint = tint
    )
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
                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFEEEEEE)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier.size(48.dp).background(Color(0xFFE0E0E0), CircleShape), contentAlignment = Alignment.Center) {
                Text(text = (participant.profiles.username ?: "U").take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = participant.profiles.username ?: "User", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = participant.role.replaceFirstChar { it.uppercase() }, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBottomSheet(
    messages: List<RoomMessageWithProfile>,
    onSendMessage: (String) -> Unit,
    onDismiss: () -> Unit,
    currentUserId: String
) {
    var text by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        modifier = Modifier.fillMaxHeight(0.75f)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Text(
                text = "Room Chat",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
            )

            LazyColumn(
                modifier = Modifier.weight(1f).padding(vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(messages) { message ->
                    val isMe = message.user_id == currentUserId
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                    ) {
                        if (!isMe) {
                            if (message.profiles.avatar_url != null) {
                                AsyncImage(
                                    model = message.profiles.avatar_url,
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFEEEEEE)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(modifier = Modifier.size(36.dp).background(Color(0xFFE0E0E0), CircleShape), contentAlignment = Alignment.Center) {
                                    Text(text = (message.profiles.username ?: "U").take(1).uppercase(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
                            if (!isMe) {
                                Text(text = message.profiles.username ?: "User", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
                            }
                            Surface(
                                color = if (isMe) MangoYellow else Color(0xFFF2F2F2),
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isMe) 16.dp else 4.dp,
                                    bottomEnd = if (isMe) 4.dp else 16.dp
                                )
                            ) {
                                Text(
                                    text = message.content,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    fontSize = 15.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.padding(bottom = 40.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("Say something...") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF7F7F7),
                        unfocusedContainerColor = Color(0xFFF7F7F7),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = {
                        if (text.isNotBlank()) {
                            onSendMessage(text)
                            text = ""
                        }
                    },
                    modifier = Modifier.size(52.dp).background(MangoYellow, CircleShape)
                ) {
                    MyIcon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", size = 24.dp, tint = Color.Black)
                }
            }
        }
    }
}
