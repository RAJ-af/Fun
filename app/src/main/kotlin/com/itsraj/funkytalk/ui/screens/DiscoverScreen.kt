package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.itsraj.funkytalk.data.model.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen() {
    var searchQuery by remember { mutableStateOf("") }

    val users = listOf(
        UserProfile(uid = "1", name = "Sakura", nativeLanguage = "Japanese", learningLanguage = "English", country = "Japan", photoUrl = "https://i.pravatar.cc/150?u=1", bio = "Hi, I love sushi and learning English!"),
        UserProfile(uid = "2", name = "Mateo", nativeLanguage = "Spanish", learningLanguage = "English", country = "Spain", photoUrl = "https://i.pravatar.cc/150?u=2", bio = "Football is my life."),
        UserProfile(uid = "3", name = "Hans", nativeLanguage = "German", learningLanguage = "Spanish", country = "Germany", photoUrl = "https://i.pravatar.cc/150?u=3", bio = "Let's practice German!"),
        UserProfile(uid = "4", name = "Elena", nativeLanguage = "Russian", learningLanguage = "English", country = "Russia", photoUrl = "https://i.pravatar.cc/150?u=4", bio = "Learning to travel more."),
        UserProfile(uid = "5", name = "Li Wei", nativeLanguage = "Mandarin", learningLanguage = "French", country = "China", photoUrl = "https://i.pravatar.cc/150?u=5", bio = "Excited to meet new friends.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                placeholder = { Text("Search users...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { /* Filter */ },
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            ) {
                Icon(Icons.Default.FilterList, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Language Pills
        val languages = listOf("English", "Spanish", "French", "German", "Japanese", "Mandarin")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(languages) { lang ->
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = { /* Select Language */ }
                ) {
                    Text(
                        text = lang,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // User List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(users) { user ->
                UserRow(user)
            }
        }
    }
}

@Composable
fun UserRow(user: UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.photoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "Native: ${user.nativeLanguage} | Learning: ${user.learningLanguage}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    user.bio,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Button(
                onClick = { /* Chat */ },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Chat", fontSize = 12.sp)
            }
        }
    }
}
