package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Settings
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
import com.itsraj.funkytalk.ui.theme.*

@Composable
fun ProfileScreen() {
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
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "My Profile",
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                    IconButton(onClick = {}, modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)) {
                        Icon(Icons.Outlined.Settings, null, tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Funky User", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = Color.White)
                        Text("funkyuser@gmail.com", color = Color.White.copy(alpha = 0.4f), fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        ProfileStat("12", "Streak")
                        ProfileStat("156", "Friends")
                        ProfileStat("4.9", "Rating")
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text("Learning Path", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))

                PremiumCard(modifier = Modifier.fillMaxWidth(), gradient = GradientPinkPurple) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Language, null, tint = Color.White, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("English Mastery", fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Advanced Level • 85% Complete", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                PremiumCard(modifier = Modifier.fillMaxWidth(), gradient = GradientCyanBlue) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Language, null, tint = Color.White, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Spanish Basics", fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Beginner Level • 12% Complete", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Spacer(modifier = Modifier.height(100.dp)) // Bottom nav padding
            }
        }
    }
}

@Composable
fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold), color = MaterialTheme.colorScheme.primary)
        Text(label, color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
    }
}
