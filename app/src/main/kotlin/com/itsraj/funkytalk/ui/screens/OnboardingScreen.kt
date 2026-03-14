package com.itsraj.funkytalk.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import kotlin.math.absoluteValue
import androidx.compose.ui.draw.scale
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
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.itsraj.funkytalk.data.model.Language
import com.itsraj.funkytalk.data.model.allLanguages
import com.itsraj.funkytalk.data.model.allCountries
import com.itsraj.funkytalk.ui.components.GenderIconButton
import com.itsraj.funkytalk.ui.components.LanguageBadge
import com.itsraj.funkytalk.ui.components.ModernLanguageChip
import com.itsraj.funkytalk.ui.components.PremiumButton
import com.itsraj.funkytalk.ui.components.PremiumTextField
import com.itsraj.funkytalk.ui.components.VerticalWheelPicker
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
    val usernameAvailable by viewModel.usernameAvailable.collectAsState()

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
            onValueChange = {
                username = it
                viewModel.onUsernameChange(it)
            },
            label = "Username",
            trailingIcon = {
                if (username.length >= 3) {
                    when (usernameAvailable) {
                        true -> Icon(Icons.Default.Check, "Available", tint = Color.Green)
                        false -> Icon(Icons.Default.Close, "Taken", tint = Color.Red)
                        null -> CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MangoYellow)
                    }
                }
            }
        )

        if (username.length >= 3 && usernameAvailable == false) {
            Text(
                "Username already taken. Try another one.",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.Start).padding(start = 16.dp, top = 4.dp)
            )
        }

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
            enabled = username.isNotBlank() && name.isNotBlank() && usernameAvailable == true && authState !is AuthState.Loading,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )
    }
}

@Composable
fun Step2Age(navController: NavController, viewModel: AuthViewModel) {
    val minAge = 13
    val maxAge = 80
    val ageList = (minAge..maxAge).toList()
    var selectedIndex by remember { mutableStateOf(24 - minAge) }
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

        Spacer(modifier = Modifier.height(64.dp))

        VerticalWheelPicker(
            count = ageList.size,
            initialIndex = selectedIndex,
            onIndexChanged = { selectedIndex = it },
            itemHeight = 80.dp
        ) { index, isSelected ->
            Text(
                text = ageList[index].toString(),
                style = if (isSelected) MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black)
                        else MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) MangoYellow else Color.Black.copy(alpha = 0.2f),
                modifier = Modifier.scale(if (isSelected) 1f else 0.8f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        PremiumButton(
            text = "Next Step",
            onClick = { viewModel.saveAge(ageList[selectedIndex]) },
            enabled = authState !is AuthState.Loading,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )
    }
}

@Composable
fun Step3Gender(navController: NavController, viewModel: AuthViewModel) {
    var selectedGender by remember { mutableStateOf("") }
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

        Spacer(modifier = Modifier.height(80.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            GenderIconButton(
                icon = Icons.Default.Male,
                label = "Male",
                isSelected = selectedGender == "Male",
                onClick = { selectedGender = "Male" }
            )

            GenderIconButton(
                icon = Icons.Default.Female,
                label = "Female",
                isSelected = selectedGender == "Female",
                onClick = { selectedGender = "Female" }
            )
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Step4NativeLanguages(navController: NavController, viewModel: AuthViewModel) {
    val selectedLanguages = remember { mutableStateListOf<String>() }
    var searchQuery by remember { mutableStateOf("") }
    val filteredLanguages = remember(searchQuery) {
        allLanguages.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.code.contains(searchQuery, ignoreCase = true)
        }
    }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success && (authState as AuthState.Success).message == "Step 4 complete") {
            navController.navigate("step5")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
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

        Spacer(modifier = Modifier.height(24.dp))

        PremiumTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = "Search languages..."
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                filteredLanguages.forEach { lang ->
                    val isSelected = selectedLanguages.contains(lang.name)
                    ModernLanguageChip(
                        text = lang.name,
                        flag = lang.flag,
                        code = lang.code,
                        isSelected = isSelected,
                        onClick = {
                            if (isSelected) selectedLanguages.remove(lang.name)
                            else if (selectedLanguages.size < 2) selectedLanguages.add(lang.name)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

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
    val context = LocalContext.current
    val selectedLanguages = remember { mutableStateListOf<String>() }
    var searchQuery by remember { mutableStateOf("") }
    val filteredLanguages = remember(searchQuery) {
        allLanguages.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.code.contains(searchQuery, ignoreCase = true)
        }
    }

    // Country Selection logic
    val deviceCountryCode = remember {
        context.resources.configuration.locales[0].country.lowercase()
    }

    val initialCountryIndex = remember {
        val index = allCountries.indexOfFirst { it.code == deviceCountryCode }
        if (index != -1) index else allCountries.indexOfFirst { it.code == "us" }.coerceAtLeast(0)
    }

    val pagerState = rememberPagerState(initialPage = initialCountryIndex) { allCountries.size }
    val selectedCountry = allCountries[pagerState.currentPage]

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success && (authState as AuthState.Success).message == "Step 5 complete") {
            navController.navigate("step6")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
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

        Spacer(modifier = Modifier.height(16.dp))

        PremiumTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = "Search languages..."
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(modifier = Modifier.weight(0.4f).fillMaxWidth()) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                filteredLanguages.forEach { lang ->
                    val isSelected = selectedLanguages.contains(lang.name)
                    ModernLanguageChip(
                        text = lang.name,
                        flag = lang.flag,
                        code = lang.code,
                        isSelected = isSelected,
                        onClick = {
                            if (isSelected) selectedLanguages.remove(lang.name)
                            else if (selectedLanguages.size < 3) selectedLanguages.add(lang.name)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Where are you from?",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
            color = Color.Black,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Flag Carousel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = (LocalContext.current.resources.displayMetrics.widthPixels / (3 * context.resources.displayMetrics.density)).dp),
                pageSpacing = 16.dp
            ) { page ->
                val country = allCountries[page]
                val isSelected = page == pagerState.currentPage

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .graphicsLayer {
                            val pageOffset = (
                                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                            ).absoluteValue

                            alpha = lerp(
                                start = 0.4f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )

                            scaleX = lerp(
                                start = 0.8f,
                                stop = 1.2f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                            scaleY = lerp(
                                start = 0.8f,
                                stop = 1.2f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://hatscripts.github.io/circle-flags/flags/${country.code}.svg")
                            .decoderFactory(SvgDecoder.Factory())
                            .crossfade(true)
                            .build(),
                        contentDescription = country.name,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) MangoYellow else Color.Transparent,
                                shape = CircleShape
                            )
                    )

                    if (isSelected) {
                        Text(
                            text = country.name,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(top = 8.dp),
                            color = Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PremiumButton(
            text = "Next Step",
            onClick = { viewModel.saveLearningLanguagesAndCountry(selectedLanguages.toList(), selectedCountry.name) },
            enabled = selectedLanguages.isNotEmpty() && authState !is AuthState.Loading,
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

    val suggestedHobbies = listOf(
        "gaming", "music", "movies", "football", "anime",
        "travel", "photography", "reading", "fitness",
        "coding", "basketball", "drawing"
    )

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

        Spacer(modifier = Modifier.height(32.dp))

        PremiumTextField(
            value = hobbyInput,
            onValueChange = { hobbyInput = it },
            label = "Add a hobby...",
            trailingIcon = {
                IconButton(onClick = {
                    if (hobbyInput.isNotBlank() && !hobbies.contains(hobbyInput)) {
                        hobbies.add(hobbyInput)
                        hobbyInput = ""
                    }
                }) {
                    Icon(Icons.Default.Add, null, tint = Color.Black)
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Suggested Hobbies",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.Black.copy(alpha = 0.6f),
            modifier = Modifier.align(Alignment.Start).padding(bottom = 12.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            suggestedHobbies.forEach { hobby ->
                val isSelected = hobbies.contains(hobby)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) hobbies.remove(hobby)
                        else hobbies.add(hobby)
                    },
                    label = { Text(hobby) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MangoYellow,
                        selectedLabelColor = Color.Black,
                        containerColor = Color.Black.copy(alpha = 0.05f),
                        labelColor = Color.Black.copy(alpha = 0.7f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Color.Transparent,
                        selectedBorderColor = MangoYellow,
                        borderWidth = 1.dp,
                        selectedBorderWidth = 1.dp,
                        enabled = true,
                        selected = isSelected
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        if (hobbies.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Your Hobbies",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.Start).padding(bottom = 12.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                hobbies.forEach { hobby ->
                    Surface(
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
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(32.dp))

        PremiumButton(
            text = "Complete Profile",
            onClick = { viewModel.saveHobbies(hobbies.toList()) },
            enabled = hobbies.isNotEmpty() && authState !is AuthState.Loading,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )
    }
}
