package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
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
import com.itsraj.funkytalk.ui.components.PremiumTextField
import com.itsraj.funkytalk.ui.theme.BackgroundGradient
import com.itsraj.funkytalk.ui.theme.GradientCyanBlue
import com.itsraj.funkytalk.ui.theme.GradientPurpleBlue

@Composable
fun DiscoverScreen() {
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
                    "Discover",
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White,
                    modifier = Modifier.padding(top = 24.dp)
                )
                Text(
                    "Find your next language partner.",
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                PremiumTextField(
                    value = "",
                    onValueChange = {},
                    label = "Search by country, language...",
                    trailingIcon = { Icon(Icons.Outlined.Search, null, tint = Color.White.copy(alpha = 0.4f)) }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    FilterChip("All", true)
                    FilterChip("English", false)
                    FilterChip("Spanish", false)
                    FilterChip("German", false)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            items(8) { index ->
                PremiumCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("User Name $index", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 18.sp)
                            Text("Native: German • Learning: English", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                        }
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (index % 2 == 0) Color(0xFF00E676) else Color.Transparent)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        indexToInterests(index).forEach { interest ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(interest, fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                            }
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

@Composable
fun FilterChip(label: String, isSelected: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, fontWeight = FontWeight.Bold, color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.6f))
    }
}

private fun indexToInterests(index: Int): List<String> {
    return when(index % 3) {
        0 -> listOf("Travel", "Music", "Photography")
        1 -> listOf("Coding", "Art", "Movies")
        else -> listOf("Sports", "Books", "Gaming")
    }
}
