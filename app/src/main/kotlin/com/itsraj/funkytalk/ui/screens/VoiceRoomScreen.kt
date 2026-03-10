package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.MoreVert
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.itsraj.funkytalk.ui.theme.MangoYellow
import com.itsraj.funkytalk.viewmodel.VoiceRoomViewModel

@Composable
fun VoiceRoomScreen(navController: NavController, viewModel: VoiceRoomViewModel, authViewModel: com.itsraj.funkytalk.viewmodel.AuthViewModel) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, can join and speak
        } else {
            // Permission denied, handle accordingly
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    val isMuted by viewModel.isMuted.collectAsState()
    val isJoined by viewModel.isJoined.collectAsState()
    val currentRoomId by viewModel.currentRoomId.collectAsState()
    val rooms by viewModel.rooms.collectAsState()
    val participants by viewModel.participants.collectAsState()

    val room = rooms.find { it.id == currentRoomId }
    val userId = authViewModel.currentUser?.id ?: ""

    val speakers = participants.filter { it.role == "host" || it.role == "speaker" }
    val listeners = participants.filter { it.role == "listener" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .statusBarsPadding()
    ) {
        // Top Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = room?.title ?: "Voice Room",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = Color.Black
                )
            }
            IconButton(onClick = { /* Menu */ }) {
                Icon(Icons.Default.MoreHoriz, contentDescription = "Menu", tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Center section: Participant Grid
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp)
        ) {
            // Speaker Area
            Text(
                text = "Speakers",
                style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.heightIn(max = 300.dp)
            ) {
                items(speakers) { participant ->
                    ParticipantAvatar(
                        name = participant.profiles.username ?: "User",
                        avatarUrl = participant.profiles.avatar_url,
                        role = participant.role,
                        size = 80.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Listener Area
            Text(
                text = "Listeners",
                style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(listeners) { participant ->
                    ParticipantAvatar(
                        name = participant.profiles.username ?: "User",
                        avatarUrl = participant.profiles.avatar_url,
                        role = participant.role,
                        size = 56.dp
                    )
                }
            }
        }

        // Bottom Control Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            tonalElevation = 8.dp,
            shadowElevation = 16.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic Toggle
                IconButton(
                    onClick = { viewModel.toggleMute() },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(if (isMuted) Color.Red.copy(alpha = 0.1f) else Color(0xFFF5F5F5))
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mic",
                        tint = if (isMuted) Color.Red else Color.Black
                    )
                }

                // Raise Hand / Speak Button
                IconButton(
                    onClick = { viewModel.raiseHand(userId) },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5))
                ) {
                    Icon(
                        imageVector = Icons.Default.PanTool,
                        contentDescription = "Raise Hand",
                        tint = Color.Black
                    )
                }

                // Leave Room Button
                Button(
                    onClick = {
                        viewModel.leaveRoom(userId)
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .height(56.dp)
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Leave",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // More Options
                IconButton(
                    onClick = { /* More */ },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5))
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "More",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun ParticipantAvatar(name: String, avatarUrl: String?, role: String, size: androidx.compose.ui.unit.Dp) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                if (avatarUrl != null) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Text(
                        text = name.take(1),
                        fontWeight = FontWeight.Black,
                        color = Color.Black.copy(alpha = 0.3f),
                        fontSize = (size.value / 2.5).sp
                    )
                }
            }

            if (role == "host") {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Host",
                    tint = MangoYellow,
                    modifier = Modifier
                        .size((size.value / 3).dp)
                        .background(Color.White, CircleShape)
                        .padding(2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            fontSize = 10.sp,
            color = Color.Black.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
