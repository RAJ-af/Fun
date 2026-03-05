package com.itsraj.funkytalk.data.model

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val age: Int = 0,
    val gender: String = "",
    val country: String = "",
    val nativeLanguage: String = "",
    val learningLanguage: String = "",
    val interests: List<String> = emptyList(),
    val bio: String = "",
    val streak: Int = 0,
    val isOnline: Boolean = false,
    val lastActive: Long = 0L
)
