package com.itsraj.funkytalk.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RoomParticipant(
    val id: String? = null,
    val room_id: String,
    val user_id: String,
    val role: String, // "host" or "listener"
    val joined_at: String
)
