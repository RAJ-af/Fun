package com.itsraj.funkytalk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.itsraj.funkytalk.data.model.UserProfile
import com.itsraj.funkytalk.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    object ProfileIncomplete : AuthState()
    object Unauthenticated : AuthState()
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
                val profile = repository.getProfile(user.uid)
                if (profile == null) {
                    _authState.value = AuthState.ProfileIncomplete
                } else {
                    _userProfile.value = profile
                    _authState.value = AuthState.Authenticated
                }
            }
        }
    }

    fun logout() {
        repository.logout()
        _authState.value = AuthState.Unauthenticated
        _userProfile.value = null
    }
}
