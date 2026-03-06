package com.itsraj.funkytalk.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.itsraj.funkytalk.ui.navigation.Screen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.itsraj.funkytalk.ui.components.PremiumButton
import com.itsraj.funkytalk.ui.components.PremiumTextField
import com.itsraj.funkytalk.ui.theme.MangoYellow
import com.itsraj.funkytalk.viewmodel.AuthState
import com.itsraj.funkytalk.viewmodel.AuthViewModel

@Composable
fun OnboardingScreen(navController: NavController, authViewModel: AuthViewModel) {
    val onboardingNavController = rememberNavController()
    val navBackStackEntry by onboardingNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "step1"
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Onboarding.route) {
                    this.inclusive = true
                }
            }
        }
    }

    val currentStep = when(currentRoute) {
        "step1" -> 1
        "step2" -> 2
        "step3" -> 3
        "step4" -> 4
        "step5" -> 5
        "step6" -> 6
        else -> 1
    }

    Scaffold(
        topBar = {
            OnboardingTopBar(currentStep, onBack = {
                if (currentStep > 1) onboardingNavController.popBackStack()
                else navController.popBackStack()
            })
        },
        containerColor = Color.White
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(
                navController = onboardingNavController,
                startDestination = "step1"
            ) {
                composable("step1") { Step1BasicInfo(onboardingNavController, authViewModel) }
                composable("step2") { Step2Age(onboardingNavController, authViewModel) }
                composable("step3") { Step3Gender(onboardingNavController, authViewModel) }
                composable("step4") { Step4NativeLanguages(onboardingNavController, authViewModel) }
                composable("step5") { Step5LearningLanguages(onboardingNavController, authViewModel) }
                composable("step6") { Step6Hobbies(onboardingNavController, authViewModel) }
            }
        }
    }
}

@Composable
fun OnboardingTopBar(currentStep: Int, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Text(
                text = "$currentStep / 6",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Black.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.width(48.dp)) // Balance
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Segmented Progress Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 1..6) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(if (i <= currentStep) MangoYellow else Color.Black.copy(alpha = 0.05f))
                )
            }
        }
    }
}

@Composable
fun Step1BasicInfo(navController: NavController, viewModel: AuthViewModel) {
    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val authState by viewModel.authState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success && (authState as AuthState.Success).message == "Step 1 complete") {
            navController.navigate("step2")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Set up your profile",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            color = Color.Black
        )
        Text(
            text = "Tell us a bit about yourself to get started.",
            color = Color.Black.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.05f))
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Black.copy(alpha = 0.3f), modifier = Modifier.size(32.dp))
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        PremiumTextField(
            value = username,
            onValueChange = { username = it },
            label = "Username"
        )

        Spacer(modifier = Modifier.height(16.dp))

        PremiumTextField(
            value = name,
            onValueChange = { name = it },
            label = "Full Name"
        )

        if (authState is AuthState.Error) {
            Text((authState as AuthState.Error).message, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        PremiumButton(
            text = "Next Step",
            onClick = {
                val bytes = selectedImageUri?.let { uri ->
                    context.contentResolver.openInputStream(uri)?.readBytes()
                }
                viewModel.saveBasicProfile(username, name, bytes)
            },
            enabled = username.isNotBlank() && name.isNotBlank() && authState !is AuthState.Loading,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )
    }
}

@Composable
fun Step2Age(navController: NavController, viewModel: AuthViewModel) {
    var age by remember { mutableStateOf(24f) }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success && (authState as AuthState.Success).message == "Step 2 complete") {
            navController.navigate("step3")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "How old are you?",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = age.toInt().toString(),
            style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black),
            color = MangoYellow
        )

        Spacer(modifier = Modifier.height(48.dp))

        Slider(
            value = age,
            onValueChange = { age = it },
            valueRange = 13f..99f,
            colors = SliderDefaults.colors(
                thumbColor = MangoYellow,
                activeTrackColor = MangoYellow,
                inactiveTrackColor = Color.Black.copy(alpha = 0.05f)
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        PremiumButton(
            text = "Next Step",
            onClick = { viewModel.saveAge(age.toInt()) },
            enabled = authState !is AuthState.Loading,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )
    }
}

@Composable
fun Step3Gender(navController: NavController, viewModel: AuthViewModel) {
    var selectedGender by remember { mutableStateOf("") }
    val options = listOf("Male", "Female", "Other", "Prefer not to say")
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success && (authState as AuthState.Success).message == "Step 3 complete") {
            navController.navigate("step4")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select your gender",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(48.dp))

        options.forEach { option ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { selectedGender = option },
                shape = RoundedCornerShape(20.dp),
                color = if (selectedGender == option) MangoYellow else Color.Black.copy(alpha = 0.05f),
                border = if (selectedGender == option) null else null
            ) {
                Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(option, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        PremiumButton(
            text = "Next Step",
            onClick = { viewModel.saveGender(selectedGender) },
            enabled = selectedGender.isNotBlank() && authState !is AuthState.Loading,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )
    }
}

@Composable
fun Step4NativeLanguages(navController: NavController, viewModel: AuthViewModel) {
    val languages = listOf("English", "Spanish", "French", "German", "Chinese", "Japanese", "Korean", "Russian", "Arabic")
    val selectedLanguages = remember { mutableStateListOf<String>() }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success && (authState as AuthState.Success).message == "Step 4 complete") {
            navController.navigate("step5")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your native languages",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            color = Color.Black
        )
        Text(
            text = "Select up to 2 languages.",
            color = Color.Black.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            languages.forEach { lang ->
                val isSelected = selectedLanguages.contains(lang)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) selectedLanguages.remove(lang)
                        else if (selectedLanguages.size < 2) selectedLanguages.add(lang)
                    },
                    label = { Text(lang) },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MangoYellow,
                        containerColor = Color.Black.copy(alpha = 0.05f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        PremiumButton(
            text = "Next Step",
            onClick = { viewModel.saveNativeLanguages(selectedLanguages.toList()) },
            enabled = selectedLanguages.isNotEmpty() && authState !is AuthState.Loading,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Step5LearningLanguages(navController: NavController, viewModel: AuthViewModel) {
    val languages = listOf("English", "Spanish", "French", "German", "Chinese", "Japanese", "Korean", "Russian", "Arabic")
    val selectedLanguages = remember { mutableStateListOf<String>() }
    var country by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success && (authState as AuthState.Success).message == "Step 5 complete") {
            navController.navigate("step6")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What are you learning?",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            color = Color.Black
        )
        Text(
            text = "Select up to 3 languages.",
            color = Color.Black.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            languages.forEach { lang ->
                val isSelected = selectedLanguages.contains(lang)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) selectedLanguages.remove(lang)
                        else if (selectedLanguages.size < 3) selectedLanguages.add(lang)
                    },
                    label = { Text(lang) },
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MangoYellow,
                        containerColor = Color.Black.copy(alpha = 0.05f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Where are you from?",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
            color = Color.Black,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        PremiumTextField(
            value = country,
            onValueChange = { country = it },
            label = "Country"
        )

        Spacer(modifier = Modifier.weight(1f))

        PremiumButton(
            text = "Next Step",
            onClick = { viewModel.saveLearningLanguagesAndCountry(selectedLanguages.toList(), country) },
            enabled = selectedLanguages.isNotEmpty() && country.isNotBlank() && authState !is AuthState.Loading,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Step6Hobbies(navController: NavController, viewModel: AuthViewModel) {
    var hobbyInput by remember { mutableStateOf("") }
    val hobbies = remember { mutableStateListOf<String>() }
    val authState by viewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add your hobbies",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            color = Color.Black
        )
        Text(
            text = "Share what you love to do.",
            color = Color.Black.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        PremiumTextField(
            value = hobbyInput,
            onValueChange = { hobbyInput = it },
            label = "Add a hobby...",
            trailingIcon = {
                IconButton(onClick = {
                    if (hobbyInput.isNotBlank()) {
                        hobbies.add(hobbyInput)
                        hobbyInput = ""
                    }
                }) {
                    Icon(Icons.Default.Add, null, tint = Color.Black)
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            hobbies.forEach { hobby ->
                Surface(
                    modifier = Modifier.padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MangoYellow.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MangoYellow)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(hobby, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.Close,
                            null,
                            modifier = Modifier.size(16.dp).clickable { hobbies.remove(hobby) },
                            tint = Color.Black.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        PremiumButton(
            text = "Complete Profile",
            onClick = { viewModel.saveHobbies(hobbies.toList()) },
            enabled = hobbies.isNotEmpty() && authState !is AuthState.Loading,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )
    }
}
