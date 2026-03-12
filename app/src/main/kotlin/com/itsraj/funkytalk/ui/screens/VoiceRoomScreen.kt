package com.itsraj.funkytalk.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.itsraj.funkytalk.data.model.ParticipantWithProfile
import com.itsraj.funkytalk.data.model.RoomMessageWithProfile
import com.itsraj.funkytalk.ui.theme.MangoYellow
import com.itsraj.funkytalk.viewmodel.VoiceRoomViewModel
import kotlinx.coroutines.launch

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

    val room = rooms.find { it.id == roomId }
    val userId = authViewModel.currentUser?.id ?: ""
    val currentProfile = authViewModel.userProfile.collectAsState().value

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${participants.size}", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { /* Menu */ }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            VoiceControlsBar(
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
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Speakers",
                style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray, fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            SpeakerGrid(speakers = speakers)

            Spacer(modifier = Modifier.weight(1f))

            ParticipantsRow(
                count = listenerCount,
                onClick = { showParticipantsSheet = true }
            )

            Spacer(modifier = Modifier.height(24.dp))
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
fun SpeakerGrid(speakers: List<ParticipantWithProfile>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(speakers) { speaker ->
            SpeakerCard(speaker = speaker)
        }
    }
}

@Composable
fun SpeakerCard(speaker: ParticipantWithProfile) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MangoYellow.copy(alpha = 0.1f))
                    .border(2.dp, MangoYellow.copy(alpha = 0.3f), CircleShape),
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
                        fontSize = 24.sp,
                        color = MangoYellow
                    )
                }
            }

            if (speaker.role == "host") {
                Surface(
                    color = MangoYellow,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.offset(y = 4.dp)
                ) {
                    Text(
                        text = "HOST",
                        color = Color.Black,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = speaker.profiles.username ?: "User",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = MangoYellow
            )
        }
    }
}

@Composable
fun ParticipantsRow(count: Int, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color(0xFFF8F8F8),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Listeners ($count)",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
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
        Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text(
                text = "Participants",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
            )
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
        AsyncImage(
            model = participant.profiles.avatar_url,
            contentDescription = null,
            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFEEEEEE)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = participant.profiles.username ?: "User", fontWeight = FontWeight.Bold)
            Text(text = participant.role.replaceFirstChar { it.uppercase() }, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun VoiceControlsBar(
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    onRaiseHand: () -> Unit,
    onChatToggle: () -> Unit,
    onLeave: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlCircleButton(
                icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                onClick = onMuteToggle,
                active = !isMuted
            )
            ControlCircleButton(
                icon = Icons.Default.PanTool,
                onClick = onRaiseHand
            )
            ControlCircleButton(
                icon = Icons.Default.Chat,
                onClick = onChatToggle
            )
            ControlCircleButton(
                icon = Icons.Default.CallEnd,
                onClick = onLeave,
                containerColor = Color.Red.copy(alpha = 0.1f),
                iconColor = Color.Red
            )
        }
    }
}

@Composable
fun ControlCircleButton(
    icon: ImageVector,
    onClick: () -> Unit,
    active: Boolean = false,
    containerColor: Color = Color(0xFFF5F5F5),
    iconColor: Color = Color.Black
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(if (active) MangoYellow else containerColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = if (active) Color.Black else iconColor)
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
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        modifier = Modifier.fillMaxHeight(0.7f)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Text(
                text = "Room Chat",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
            )

            LazyColumn(
                modifier = Modifier.weight(1f).padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { message ->
                    val isMe = message.user_id == currentUserId
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                    ) {
                        if (!isMe) {
                            AsyncImage(
                                model = message.profiles.avatar_url,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFEEEEEE)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
                            if (!isMe) {
                                Text(text = message.profiles.username ?: "User", fontSize = 10.sp, color = Color.Gray)
                            }
                            Surface(
                                color = if (isMe) MangoYellow else Color(0xFFF5F5F5),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = message.content,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("Say something...") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (text.isNotBlank()) {
                            onSendMessage(text)
                            text = ""
                        }
                    },
                    modifier = Modifier.background(MangoYellow, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.Black)
                }
            }
        }
    }
}
