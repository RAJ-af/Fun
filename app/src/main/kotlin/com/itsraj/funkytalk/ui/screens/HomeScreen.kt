package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.itsraj.funkytalk.data.model.UserProfile

@Composable
fun HomeScreen() {
    val scrollState = rememberScrollState()

    // Mock data for recommended partners
    val recommendedPartners = listOf(
        UserProfile(uid = "1", name = "Sakura", nativeLanguage = "Japanese", learningLanguage = "English", country = "Japan", photoUrl = "https://i.pravatar.cc/150?u=1"),
        UserProfile(uid = "2", name = "Mateo", nativeLanguage = "Spanish", learningLanguage = "English", country = "Spain", photoUrl = "https://i.pravatar.cc/150?u=2"),
        UserProfile(uid = "3", name = "Hans", nativeLanguage = "German", learningLanguage = "Spanish", country = "Germany", photoUrl = "https://i.pravatar.cc/150?u=3"),
        UserProfile(uid = "4", name = "Elena", nativeLanguage = "Russian", learningLanguage = "English", country = "Russia", photoUrl = "https://i.pravatar.cc/150?u=4")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hello, Explorer!",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Ready to practice today?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { /* Notifications */ }) {
                Icon(Icons.Default.Notifications, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Streak Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Daily Streak",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "5 Days 🔥",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Icon(
                    Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFFFF9800)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Recommended Partners Header
        Text(
            text = "Recommended for you",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(recommendedPartners) { partner ->
                PartnerCard(partner)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Active Voice Rooms Placeholder (for Future)
        Text(
            text = "Popular Voice Rooms",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Voice Rooms coming soon!", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(100.dp)) // Extra space at bottom
    }
}

@Composable
fun PartnerCard(user: UserProfile) {
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = user.photoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                "${user.nativeLanguage} ➔ ${user.learningLanguage}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(user.country, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
