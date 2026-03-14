package com.itsraj.funkytalk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.itsraj.funkytalk.data.model.allCountries
import com.itsraj.funkytalk.data.model.allLanguages
import com.itsraj.funkytalk.ui.components.CircularFlag
import com.itsraj.funkytalk.ui.components.PremiumButton
import com.itsraj.funkytalk.ui.components.PremiumTextField
import com.itsraj.funkytalk.ui.navigation.Screen
import com.itsraj.funkytalk.ui.theme.MangoYellow
import com.itsraj.funkytalk.viewmodel.AuthViewModel
import com.itsraj.funkytalk.viewmodel.VoiceRoomNavigationEvent
import com.itsraj.funkytalk.viewmodel.VoiceRoomViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateRoomScreen(
    navController: NavController,
    voiceRoomViewModel: VoiceRoomViewModel,
    authViewModel: AuthViewModel
) {
    var title by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf(allLanguages.first()) }
    var selectedCountry by remember { mutableStateOf(allCountries.first { it.code == "us" }) }
    var selectedTag by remember { mutableStateOf("Discover") }
    var roomType by remember { mutableStateOf("public") }
    var isCreating by remember { mutableStateOf(false) }

    val allTags = listOf("Discover", "Languages", "Music", "Friends", "Games", "Study")
    val userId = authViewModel.currentUser?.id ?: ""

    LaunchedEffect(Unit) {
        voiceRoomViewModel.navigationEvents.collect { event ->
            if (event is VoiceRoomNavigationEvent.NavigateToRoom) {
                navController.navigate(Screen.VoiceRoom.createRoute(event.roomId, event.role)) {
                    popUpTo(Screen.CreateRoom.route) { inclusive = true }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Create Room",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                color = Color.Black
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Room Details",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))

            PremiumTextField(
                value = title,
                onValueChange = { title = it },
                label = "Room Title"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Language Selector
            Text(text = "Language", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(allLanguages.take(10)) { lang ->
                    val isSelected = selectedLanguage.code == lang.code
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedLanguage = lang },
                        label = { Text(lang.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MangoYellow,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Country Selector
            Text(text = "Country", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(allCountries) { country ->
                    val isSelected = selectedCountry.code == country.code
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) MangoYellow.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { selectedCountry = country }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularFlag(code = country.code, size = 32.dp)
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = MangoYellow,
                                modifier = Modifier.size(16.dp).align(Alignment.BottomEnd).background(Color.White, CircleShape)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tag Selector
            Text(text = "Tag", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allTags.forEach { tag ->
                    val isSelected = selectedTag == tag
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTag = tag },
                        label = { Text(tag) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MangoYellow,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Room Type
            Text(text = "Room Type", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf("public", "private").forEach { type ->
                    val isSelected = roomType == type
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clickable { roomType = type },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MangoYellow else Color.White,
                        tonalElevation = 2.dp,
                        shadowElevation = if (isSelected) 4.dp else 1.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = type.replaceFirstChar { it.uppercase() },
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // Create Button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            PremiumButton(
                text = if (isCreating) "Creating..." else "Create Room",
                onClick = {
                    if (title.isNotBlank() && !isCreating) {
                        isCreating = true
                        voiceRoomViewModel.createRoom(
                            title = title,
                            language = selectedLanguage.name,
                            countryCode = selectedCountry.code,
                            tag = selectedTag,
                            roomType = roomType,
                            hostId = userId
                        ) { roomId ->
                            isCreating = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp)
            )
        }
    }
}
