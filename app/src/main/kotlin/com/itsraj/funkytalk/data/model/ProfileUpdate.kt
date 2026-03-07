package com.itsraj.funkytalk.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileUpdate(
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
    val last_seen: String? = null
)

@Serializable
data class InitialProfile(
    val auth_user_id: String,
    val email: String,
    val username: String? = null,
    val profile_name: String? = null,
    val created_at: String? = null
)
