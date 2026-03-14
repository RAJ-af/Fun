package com.itsraj.funkytalk.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ParticipantWithProfile(
    val id: String? = null,
    val room_id: String,
    val user_id: String,
    val role: String,
    val joined_at: String,
    val profiles: UserProfile // Supabase join structure
)
