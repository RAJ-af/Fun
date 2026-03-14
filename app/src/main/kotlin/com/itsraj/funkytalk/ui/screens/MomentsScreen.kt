package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itsraj.funkytalk.ui.components.PremiumCard
import com.itsraj.funkytalk.ui.theme.*

@Composable
fun MomentsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp)
        ) {
            item {
                Text(
                    "Moments",
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
                    color = Color.Black,
                    modifier = Modifier.padding(top = 24.dp)
                )
                Text(
                    "Share your world and learn together.",
                    color = Color.Black.copy(alpha = 0.5f),
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
                            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.05f)))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("User $index", fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Native: French • 2h ago", color = Color.Black.copy(alpha = 0.5f), fontSize = 12.sp)
                            }
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Outlined.MoreHoriz, null, tint = Color.Black.copy(alpha = 0.3f))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "I am learning English today. It is very fun to meet new people and practice together. How is your day going?",
                        color = Color.Black,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MangoYellow.copy(alpha = 0.15f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Suggested Correction", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                            Text("“Learning English today. It’s fun to meet new people...”", color = Color.Black.copy(alpha = 0.7f), fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.ThumbUp, null, tint = Color.Black, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("24 Likes", color = Color.Black.copy(alpha = 0.6f), fontSize = 14.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Language, null, tint = Color.Black.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Suggest Correction", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
