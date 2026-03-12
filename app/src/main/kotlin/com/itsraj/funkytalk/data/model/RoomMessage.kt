package com.itsraj.funkytalk.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RoomMessage(
    val id: String? = null,
    val room_id: String,
    val user_id: String,
    val content: String,
    val created_at: String? = null
)

@Serializable
data class RoomMessageWithProfile(
    val id: String,
    val room_id: String,
    val user_id: String,
    val content: String,
    val created_at: String,
    val profiles: UserProfile
)
