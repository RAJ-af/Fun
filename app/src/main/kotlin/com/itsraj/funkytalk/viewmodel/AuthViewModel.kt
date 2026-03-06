package com.itsraj.funkytalk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsraj.funkytalk.data.model.UserProfile
import com.itsraj.funkytalk.data.repository.AuthRepository
import io.github.jan.supabase.exceptions.RestException
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
        checkUserStatus()
    }

    fun checkUserStatus() {
        viewModelScope.launch {
            val user = repository.currentUser
            if (user == null) {
                _authState.value = AuthState.Unauthenticated
            } else {
                val profile = repository.getProfile(user.id)
                if (profile == null) {
                    _authState.value = AuthState.ProfileIncomplete
                } else {
                    _userProfile.value = profile
                    _authState.value = AuthState.Authenticated
                }
            }
        }
    }

    fun continueWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Try Login
                val user = repository.login(email, pass)
                if (user != null) {
                    checkUserStatus()
                }
            } catch (loginError: Exception) {
                // If login fails, we attempt signup.
                // If signup fails because the user already exists, then the password was wrong.
                try {
                    val newUser = repository.signup(email, pass)
                    if (newUser != null) {
                        _authState.value = AuthState.Success("Account created successfully")
                        checkUserStatus()
                    }
                } catch (signUpError: Exception) {
                    val signUpMessage = signUpError.message ?: ""
                    if (signUpMessage.contains("User already registered", ignoreCase = true)) {
                        _authState.value = AuthState.Error("Incorrect password")
                    } else {
                        _authState.value = AuthState.Error("Could not create account. Use at least 6 characters for password.")
                    }
                }
            }
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            try {
                repository.loginWithGoogle()
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Google login failed")
            }
        }
    }

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                repository.saveProfile(profile)
                _userProfile.value = profile
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Failed to save profile")
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
