package com.itsraj.funkytalk.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String = "",
    val email: String = "",
    val username: String? = null,
    val profile_name: String? = null,
    val avatar_url: String? = null,
    val age: Int? = null,
    val gender: String? = null,
    val country: String? = null,
    val is_profile_completed: Boolean = false,
    val streak: Int = 0,
    val is_online: Boolean = false,
    val last_active: Long = 0L,
    val created_at: String = ""
)
