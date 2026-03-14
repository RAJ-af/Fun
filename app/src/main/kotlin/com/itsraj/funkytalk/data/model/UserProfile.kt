package com.itsraj.funkytalk.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val auth_user_id: String = "",
    val username: String? = null,
    val profile_name: String? = null,
    val avatar_url: String? = null,
    val age: Int? = null,
    val gender: String? = null,
    val country: String? = null,
    val native_languages: List<String>? = null,
    val learning_languages: List<String>? = null,
    val hobbies: List<String>? = null,
    val bio: String? = null,
    val email: String? = null,
    val last_seen: String? = null,
    val created_at: String? = null,
    val is_profile_completed: Boolean = false // Helper field, not in DB but used for logic
)
