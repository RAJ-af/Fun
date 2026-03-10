package com.itsraj.funkytalk.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VoiceRoom(
    val id: String,
    val title: String,
    val language: String,
    val host_id: String,
    val created_at: String
)

@Serializable
data class VoiceRoomWithDetails(
    val id: String,
    val title: String,
    val language: String,
    val host_id: String,
    val created_at: String,
    val participantCount: Int,
    val participantAvatars: List<String>
)
