package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itsraj.funkytalk.ui.components.PremiumCard
import com.itsraj.funkytalk.ui.theme.BackgroundGradient
import com.itsraj.funkytalk.ui.theme.GradientPurpleBlue

@Composable
fun MomentsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(BackgroundGradient))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp)
        ) {
            item {
                Text(
                    "Moments",
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White,
                    modifier = Modifier.padding(top = 24.dp)
                )
                Text(
                    "Share your world and learn together.",
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            items(5) { index ->
                PremiumCard(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("User $index", fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Native: French • 2h ago", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                            }
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Outlined.MoreHoriz, null, tint = Color.White.copy(alpha = 0.5f))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "I am learning English today. It is very fun to meet new people and practice together. How is your day going?",
                        color = Color.White,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    PremiumCard(
                        modifier = Modifier.fillMaxWidth(),
                        gradient = GradientPurpleBlue
                    ) {
                        Text("Suggested Correction", fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("“Learning English today. It’s fun to meet new people...”", color = Color.Black.copy(alpha = 0.7f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.ThumbUp, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("24 Likes", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Language, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Suggest Correction", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp)) // Bottom nav padding
            }
        }
    }
}
