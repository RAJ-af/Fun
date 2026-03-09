package com.itsraj.funkytalk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsraj.funkytalk.data.model.UserProfile
import com.itsraj.funkytalk.data.repository.AuthRepository
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.exception.AuthRestException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    object ProfileIncomplete : AuthState()
    object Unauthenticated : AuthState()
    data class EmailVerificationPending(val email: String) : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile = _userProfile.asStateFlow()

    private val _usernameAvailable = MutableStateFlow<Boolean?>(null)
    val usernameAvailable = _usernameAvailable.asStateFlow()

    private val _usernameQuery = MutableStateFlow("")

    val currentUser get() = repository.currentUser

    init {
        observeSession()
        observeUsernameQuery()
    }

    @OptIn(FlowPreview::class)
    private fun observeUsernameQuery() {
        viewModelScope.launch {
            _usernameQuery
                .debounce(500)
                .distinctUntilChanged()
                .filter { it.length >= 3 }
                .collect { query ->
                    checkUsername(query)
                }
        }
    }

    fun onUsernameChange(username: String) {
        _usernameQuery.value = username
        if (username.length < 3) {
            _usernameAvailable.value = null
        }
    }

    private fun checkUsername(username: String) {
        viewModelScope.launch {
            try {
                val isUnique = repository.isUsernameUnique(username)
                _usernameAvailable.value = isUnique
            } catch (e: Exception) {
                _usernameAvailable.value = null
            }
        }
    }

    private fun observeSession() {
        viewModelScope.launch {
            repository.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        checkUserStatus()
                    }
                    is SessionStatus.NotAuthenticated -> {
                        // Only set Unauthenticated if we're not currently in a Success/Error state
                        val current = _authState.value
                        if (current !is AuthState.Success && current !is AuthState.Error) {
                            _authState.value = AuthState.Unauthenticated
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun checkUserStatus(refresh: Boolean = false) {
        viewModelScope.launch {
            val user = if (refresh) repository.refreshUser() else repository.currentUser
            if (user == null) {
                _authState.value = AuthState.Unauthenticated
            } else {
                // Fix verification flow: check if email is verified
                if (user.emailConfirmedAt == null) {
                    _authState.value = AuthState.EmailVerificationPending(user.email ?: "")
                    return@launch
                }

                val profile = repository.getProfile(user.id)
                // Use presence of hobbies (last onboarding step) to determine completion
                val isCompleted = profile != null && !profile.hobbies.isNullOrEmpty()
                if (profile == null || !isCompleted) {
                    _userProfile.value = profile
                    _authState.value = AuthState.ProfileIncomplete
                } else {
                    _userProfile.value = profile
                    _authState.value = AuthState.Authenticated
                }
            }
        }
    }

    private fun mapError(error: Throwable, default: String): String {
        val message = error.message ?: return default
        return when {
            message.contains("profiles_username_key", ignoreCase = true) -> "Username already taken. Try another one."
            message.contains("User already registered", ignoreCase = true) -> "User already registered."
            message.contains("Invalid login credentials", ignoreCase = true) -> "Invalid email or password."
            message.contains("Email not confirmed", ignoreCase = true) -> "Please confirm your email address."
            message.contains("network", ignoreCase = true) -> "Network error. Please check your connection."
            message.contains("Database error saving new user", ignoreCase = true) -> "Error creating your profile. Please try again."
            else -> default
        }
    }

    fun continueWithEmail(email: String, pass: String) {
        if (pass.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Strategy: Signup first. If user exists, it throws.
                repository.signup(email, pass)
                _authState.value = AuthState.EmailVerificationPending(email)
            } catch (signUpError: Exception) {
                val signUpMessage = signUpError.message ?: ""
                if (signUpMessage.contains("User already registered", ignoreCase = true)) {
                    // Try Login
                    try {
                        repository.login(email, pass)
                        checkUserStatus()
                    } catch (loginError: Exception) {
                        _authState.value = AuthState.Error(mapError(loginError, "Login failed"))
                    }
                } else {
                    _authState.value = AuthState.Error(mapError(signUpError, "Signup failed"))
                }
            }
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            try {
                repository.loginWithGoogle()
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Google login failed")
            }
        }
    }

    // Onboarding methods
    fun saveBasicProfile(username: String, profileName: String, avatarBytes: ByteArray?) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = repository.currentUser ?: return@launch

                if (_usernameAvailable.value == false) {
                    _authState.value = AuthState.Error("Username already taken. Try another one.")
                    return@launch
                }

                var avatarUrl: String? = null
                if (avatarBytes != null) {
                    avatarUrl = repository.uploadAvatar(user.id, avatarBytes)
                }

                if (!repository.isUsernameUnique(username)) {
                    _authState.value = AuthState.Error("Username already taken. Try another one.")
                    return@launch
                }

                val currentProfile = _userProfile.value ?: UserProfile(auth_user_id = user.id, email = user.email ?: "")
                val updatedProfile = currentProfile.copy(
                    username = username,
                    profile_name = profileName,
                    avatar_url = avatarUrl
                )
                repository.updateProfile(updatedProfile)
                _userProfile.value = updatedProfile
                _authState.value = AuthState.Success("Step 1 complete")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(mapError(e, "Failed to save profile"))
            }
        }
    }

    fun saveAge(age: Int) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val profile = _userProfile.value ?: return@launch
                val updated = profile.copy(age = age)
                repository.updateProfile(updated)
                _userProfile.value = updated
                _authState.value = AuthState.Success("Step 2 complete")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(mapError(e, "Failed to save age"))
            }
        }
    }

    fun saveGender(gender: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val profile = _userProfile.value ?: return@launch
                val updated = profile.copy(gender = gender)
                repository.updateProfile(updated)
                _userProfile.value = updated
                _authState.value = AuthState.Success("Step 3 complete")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(mapError(e, "Failed to save gender"))
            }
        }
    }

    fun saveNativeLanguages(languages: List<String>) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val profile = _userProfile.value ?: return@launch
                val updated = profile.copy(native_languages = languages)
                repository.updateProfile(updated)
                _userProfile.value = updated
                _authState.value = AuthState.Success("Step 4 complete")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(mapError(e, "Failed to save languages"))
            }
        }
    }

    fun saveLearningLanguagesAndCountry(languages: List<String>, country: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val profile = _userProfile.value ?: return@launch
                val updated = profile.copy(
                    learning_languages = languages,
                    country = country
                )
                repository.updateProfile(updated)
                _userProfile.value = updated
                _authState.value = AuthState.Success("Step 5 complete")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(mapError(e, "Failed to save data"))
            }
        }
    }

    fun saveHobbies(hobbies: List<String>) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val profile = _userProfile.value ?: return@launch
                val finalProfile = profile.copy(
                    hobbies = hobbies,
                    is_profile_completed = true
                )
                repository.updateProfile(finalProfile)
                _userProfile.value = finalProfile
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(mapError(e, "Failed to complete onboarding"))
            }
        }
    }

    fun resendConfirmationEmail(email: String) {
        viewModelScope.launch {
            try {
                repository.resendConfirmationEmail(email)
                _authState.value = AuthState.Success("Confirmation email sent again")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(mapError(e, "Failed to resend email"))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _authState.value = AuthState.Unauthenticated
            _userProfile.value = null
        }
    }
}
