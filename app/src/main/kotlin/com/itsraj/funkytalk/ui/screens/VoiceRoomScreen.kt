package com.itsraj.funkytalk.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "SPEAKERS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Gray.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                )
                Spacer(modifier = Modifier.height(32.dp))

                DynamicSpeakerGrid(speakers = speakers, activeSpeakers = activeSpeakers)

                Spacer(modifier = Modifier.height(24.dp))

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
                    onMuteToggle = { viewModel.toggleMute() },
                    onRaiseHand = { viewModel.raiseHand(userId) },
                    onChatToggle = { showChatSheet = true },
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
            count == 0 -> {
                // Should not happen as host is present
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    SpeakerPlaceholder(100.dp)
                    SpeakerPlaceholder(100.dp)
                }
            }
            count == 1 -> {
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp), verticalAlignment = Alignment.CenterVertically) {
                    SpeakerAvatar(speaker = speakers[0], size = 110.dp, isSpeaking = activeSpeakers.isNotEmpty())
                    SpeakerPlaceholder(110.dp)
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    modifier = Modifier.wrapContentHeight()
                ) {
                    items(speakers) { speaker ->
                        SpeakerAvatar(speaker = speaker, size = 100.dp)
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
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFFF9F9F9), CircleShape)
            .border(1.dp, Color.LightGray.copy(alpha = 0.3f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        MyIcon(Icons.Rounded.Add, contentDescription = null, size = (size.value / 3).dp, tint = Color.LightGray)
    }
}

@Composable
fun SpeakerAvatar(speaker: ParticipantWithProfile, size: androidx.compose.ui.unit.Dp = 100.dp, isSpeaking: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val ringScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
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
            if (isSpeaking) {
                Box(
                    modifier = Modifier
                        .size(size)
                        .scale(ringScale)
                        .border(3.dp, MangoYellow.copy(alpha = ringAlpha), CircleShape)
                )
            }

            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape)
                        .background(Color(0xFFF0F0F0))
                        .border(
                            width = if (isSpeaking) 3.dp else 1.dp,
                            color = if (isSpeaking) MangoYellow else Color(0xFFEEEEEE),
                            shape = CircleShape
                        )
                ) {
                    if (speaker.profiles.avatar_url != null) {
                        AsyncImage(
                            model = speaker.profiles.avatar_url,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = (speaker.profiles.username ?: "U").take(1).uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = (size.value / 2.5).sp,
                                color = Color.White
                            )
                        }
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
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = speaker.profiles.username ?: "User",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color.Black
        )
    }
}

@Composable
fun ListenersCard(count: Int, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp,
        modifier = Modifier.wrapContentWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MyIcon(Icons.Rounded.Groups, contentDescription = null, size = 18.dp, tint = MangoYellow)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "$count Listening",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            androidx.compose.material3.Icon(
                imageVector = Icons.Rounded.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
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
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        messages.forEach { message ->
            Row(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${message.profiles.username}: ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.Black.copy(alpha = 0.6f)
                )
                Text(
                    text = message.content,
                    fontSize = 11.sp,
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
            active = !isMuted
        )
        ControlCircleButton(
            icon = Icons.Rounded.PanTool,
            onClick = onRaiseHand
        )
        ControlCircleButton(
            icon = Icons.Rounded.Chat,
            onClick = onChatToggle
        )
        ControlCircleButton(
            icon = Icons.Rounded.CallEnd,
            onClick = onLeave,
            containerColor = Color.Red.copy(alpha = 0.1f),
            iconColor = Color.Red
        )
    }
}

@Composable
fun ControlCircleButton(
    icon: ImageVector,
    onClick: () -> Unit,
    active: Boolean = false,
    containerColor: Color = Color(0xFFF7F7F7),
    iconColor: Color = Color.Black
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(if (active) MangoYellow else containerColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        MyIcon(icon, contentDescription = null, size = 26.dp, tint = if (active) Color.Black else iconColor)
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
            Box(modifier = Modifier.size(44.dp).background(Color(0xFFE0E0E0), CircleShape), contentAlignment = Alignment.Center) {
                Text(text = (participant.profiles.username ?: "U").take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = participant.profiles.username ?: "User", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(text = participant.role.replaceFirstChar { it.uppercase() }, fontSize = 12.sp, color = Color.Gray)
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
                                    modifier = Modifier.size(34.dp).clip(CircleShape).background(Color(0xFFEEEEEE)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(modifier = Modifier.size(34.dp).background(Color(0xFFE0E0E0), CircleShape), contentAlignment = Alignment.Center) {
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
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.Black)
                }
            }
        }
    }
}
