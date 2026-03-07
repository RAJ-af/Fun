package com.itsraj.funkytalk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsraj.funkytalk.data.model.UserProfile
import com.itsraj.funkytalk.data.repository.AuthRepository
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.exception.AuthRestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    object ProfileIncomplete : AuthState()
    object Unauthenticated : AuthState()
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

    init {
        observeSession()
    }

    private fun observeSession() {
        viewModelScope.launch {
            repository.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        checkUserStatus()
                    }
                    is SessionStatus.NotAuthenticated -> {
                        _authState.value = AuthState.Unauthenticated
                    }
                    else -> {}
                }
            }
        }
    }

    fun checkUserStatus() {
        viewModelScope.launch {
            val user = repository.currentUser
            if (user == null) {
                _authState.value = AuthState.Unauthenticated
            } else {
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
                _authState.value = AuthState.Success("Account created successfully")
                checkUserStatus()
            } catch (signUpError: Exception) {
                val signUpMessage = signUpError.message ?: ""
                if (signUpMessage.contains("User already registered", ignoreCase = true)) {
                    // Try Login
                    try {
                        repository.login(email, pass)
                        checkUserStatus()
                    } catch (loginError: Exception) {
                        _authState.value = AuthState.Error(loginError.message ?: "Login failed")
                    }
                } else {
                    _authState.value = AuthState.Error(signUpError.message ?: "Signup failed")
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
                var avatarUrl: String? = null
                if (avatarBytes != null) {
                    avatarUrl = repository.uploadAvatar(user.id, avatarBytes)
                }

                if (!repository.isUsernameUnique(username)) {
                    _authState.value = AuthState.Error("Username already taken")
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
                _authState.value = AuthState.Error(e.message ?: "Failed to save profile")
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
                _authState.value = AuthState.Error(e.message ?: "Failed to save age")
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
                _authState.value = AuthState.Error(e.message ?: "Failed to save gender")
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
                _authState.value = AuthState.Error(e.message ?: "Failed to save languages")
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
                _authState.value = AuthState.Error(e.message ?: "Failed to save data")
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
                _authState.value = AuthState.Error(e.message ?: "Failed to complete onboarding")
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
