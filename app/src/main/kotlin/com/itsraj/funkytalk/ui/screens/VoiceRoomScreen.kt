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
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
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
            // Stage (Dynamic Speakers Grid)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                DynamicSpeakerStage(speakers = speakers)

                Spacer(modifier = Modifier.height(32.dp))

                // Listener Indicator
                Surface(
                    onClick = { showParticipantsSheet = true },
                    color = Color.Transparent,
                    modifier = Modifier.wrapContentSize()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "👥 $listenerCount Listening",
                            style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Bottom Floating UI
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Chat Preview overlay
                ChatPreviewOverlay(messages = messages.takeLast(2))

                Spacer(modifier = Modifier.height(24.dp))

                // Floating Controls
                FloatingRoomControls(
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
                onSendMessage = { content: String -> viewModel.sendMessage(userId, content) },
                onDismiss = { showChatSheet = false },
                currentUserId = userId
            )
        }
    }
}

@Composable
fun DynamicSpeakerStage(speakers: List<ParticipantWithProfile>) {
    val count = speakers.size

    Box(modifier = Modifier.fillMaxWidth().wrapContentHeight(), contentAlignment = Alignment.Center) {
        when {
            count == 1 -> {
                SpeakerAvatarItem(speaker = speakers[0], size = 160.dp)
            }
            count == 2 -> {
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    speakers.forEach { SpeakerAvatarItem(speaker = it, size = 110.dp) }
                }
            }
            count in 3..4 -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.width(260.dp).wrapContentHeight()
                ) {
                    items(speakers) { speaker ->
                        SpeakerAvatarItem(speaker = speaker, size = 100.dp)
                    }
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                ) {
                    items(speakers.take(6)) { speaker ->
                        SpeakerAvatarItem(speaker = speaker, size = 85.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun SpeakerAvatarItem(speaker: ParticipantWithProfile, size: androidx.compose.ui.unit.Dp) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier.size(size),
                shape = CircleShape,
                color = Color(0xFFF5F5F5)
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
                            color = Color.Black.copy(alpha = 0.2f)
                        )
                    }
                }
            }

            if (speaker.role == "host") {
                Surface(
                    color = MangoYellow,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.offset(y = 2.dp)
                ) {
                    Text(
                        text = "HOST",
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
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
fun ChatPreviewOverlay(messages: List<RoomMessageWithProfile>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        messages.forEach { message ->
            var visible by remember { mutableStateOf(true) }

            LaunchedEffect(message.id) {
                delay(5000)
                visible = false
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${message.profiles.username}: ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.Black.copy(alpha = 0.5f)
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
    }
}

@Composable
fun FloatingRoomControls(
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
        ControlIconCircle(
            icon = if (isMuted) Icons.Rounded.MicOff else Icons.Rounded.Mic,
            onClick = onMuteToggle,
            active = !isMuted,
            activeColor = MangoYellow
        )
        ControlIconCircle(
            icon = Icons.Rounded.PanTool,
            onClick = onRaiseHand
        )
        ControlIconCircle(
            icon = Icons.Rounded.Chat,
            onClick = onChatToggle,
            badgeCount = unreadCount
        )
        ControlIconCircle(
            icon = Icons.Rounded.CallEnd,
            onClick = onLeave,
            containerColor = Color.White,
            iconColor = Color.Red,
            isLeave = true
        )
    }
}

@Composable
fun ControlIconCircle(
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
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = if (active) activeColor else containerColor,
            shadowElevation = 2.dp,
            onClick = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = if (active && !isLeave) Color.Black else iconColor)
            }
        }

        if (badgeCount > 0) {
            Surface(
                color = Color.Red,
                shape = CircleShape,
                modifier = Modifier.size(18.dp).offset(x = 2.dp, y = (-2).dp),
                border = BorderStroke(1.5.dp, Color.White)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = badgeCount.toString(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBottomSheet(
    messages: List<RoomMessageWithProfile>,
    onSendMessage: (String) -> Unit,
    onDismiss: () -> Unit,
    currentUserId: String
) {
    var textState = remember { mutableStateOf("") }

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
                                Box(modifier = Modifier.size(36.dp).background(Color(0xFFF5F5F5), CircleShape), contentAlignment = Alignment.Center) {
                                    Text(text = (message.profiles.username ?: "U").take(1).uppercase(), color = Color.Black.copy(alpha = 0.3f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
                            if (!isMe) {
                                Text(text = message.profiles.username ?: "User", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
                            }
                            Surface(
                                color = if (isMe) MangoYellow else Color(0xFFF5F5F5),
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
                    value = textState.value,
                    onValueChange = { textState.value = it },
                    placeholder = { Text("Say something...") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = {
                        if (textState.value.isNotBlank()) {
                            onSendMessage(textState.value)
                            textState.value = ""
                        }
                    },
                    modifier = Modifier.size(52.dp).background(MangoYellow, CircleShape)
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black
                    )
                }
            }
        }
    }
}
