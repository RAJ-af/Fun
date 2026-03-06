package com.itsraj.funkytalk.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val photo_url: String = "",
    val age: Int = 0,
    val gender: String = "",
    val country: String = "",
    val native_language: String = "",
    val learning_language: String = "",
    val interests: List<String> = emptyList(),
    val bio: String = "",
    val streak: Int = 0,
    val is_online: Boolean = false,
    val last_active: Long = 0L,
    val created_at: String = ""
)
