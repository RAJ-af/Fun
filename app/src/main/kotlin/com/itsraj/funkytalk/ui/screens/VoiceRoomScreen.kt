package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.itsraj.funkytalk.ui.theme.MangoYellow
import com.itsraj.funkytalk.viewmodel.VoiceRoomViewModel

@Composable
fun VoiceRoomScreen(navController: NavController, viewModel: VoiceRoomViewModel) {
    val isMuted by viewModel.isMuted.collectAsState()
    val isJoined by viewModel.isJoined.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Voice Room",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isJoined) "Connected" else "Connecting...",
            color = if (isJoined) Color.Green else Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Participant Grid (Mock for now, as real-time participant sync isn't in scope)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(6) { index ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "P${index + 1}",
                            fontWeight = FontWeight.Black,
                            color = Color.Black.copy(alpha = 0.3f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "User ${index + 1}",
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mute Button
            FloatingActionButton(
                onClick = { viewModel.toggleMute() },
                containerColor = if (isMuted) Color.Red else Color.Black.copy(alpha = 0.05f),
                contentColor = if (isMuted) Color.White else Color.Black,
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Mute"
                )
            }

            // Leave Button
            FloatingActionButton(
                onClick = {
                    viewModel.leaveRoom()
                    navController.popBackStack()
                },
                containerColor = Color.Red,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CallEnd,
                    contentDescription = "Leave",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
