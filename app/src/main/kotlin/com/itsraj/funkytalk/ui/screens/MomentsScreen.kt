package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.BorderStroke

data class Moment(
    val id: String,
    val authorName: String,
    val authorPhoto: String,
    val content: String,
    val image: String? = null,
    val likes: Int = 0,
    val comments: Int = 0,
    val time: String,
    val originalText: String? = null,
    val correctedText: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MomentsScreen() {
    val moments = listOf(
        Moment(
            id = "1",
            authorName = "Sakura",
            authorPhoto = "https://i.pravatar.cc/150?u=1",
            content = "I went to Mount Fuji today! It was beautiful. 🗻",
            image = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?q=80&w=400&auto=format&fit=crop",
            likes = 124,
            comments = 15,
            time = "2h ago"
        ),
        Moment(
            id = "2",
            authorName = "Hans",
            authorPhoto = "https://i.pravatar.cc/150?u=3",
            content = "Yesterday I play football with my friend.",
            originalText = "Yesterday I play football with my friend.",
            correctedText = "Yesterday I played football with my friends.",
            likes = 45,
            comments = 3,
            time = "5h ago"
        ),
        Moment(
            id = "3",
            authorName = "Mateo",
            authorPhoto = "https://i.pravatar.cc/150?u=2",
            content = "Starting a 30-day language challenge! Who's with me?",
            likes = 89,
            comments = 24,
            time = "8h ago"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moments", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* New Post */ }) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* New Moment */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.PostAdd, contentDescription = null)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(moments) { moment ->
                MomentCard(moment)
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
            }
        }
    }
}

@Composable
fun MomentCard(moment: Moment) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Author Row
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = moment.authorPhoto,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(moment.authorName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(moment.time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content
        Text(moment.content, style = MaterialTheme.typography.bodyLarge)

        // Correction UI
        if (moment.correctedText != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Correction Suggested", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(moment.correctedText, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Image
        if (moment.image != null) {
            Spacer(modifier = Modifier.height(12.dp))
            AsyncImage(
                model = moment.image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* Like */ }) {
                    Icon(Icons.Outlined.FavoriteBorder, contentDescription = null)
                }
                Text(moment.likes.toString(), fontSize = 14.sp)

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(onClick = { /* Comment */ }) {
                    Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null)
                }
                Text(moment.comments.toString(), fontSize = 14.sp)
            }

            IconButton(onClick = { /* Correction Tool */ }) {
                Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}
