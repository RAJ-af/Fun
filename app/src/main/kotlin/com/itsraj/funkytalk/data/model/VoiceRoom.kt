package com.itsraj.funkytalk.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VoiceRoom(
    val id: String,
    val title: String? = null,
    val language: String? = null,
    val host_id: String? = null,
    val created_at: String? = null,
    val room_participants: List<ParticipantWithProfile>? = null
)

@Serializable
data class VoiceRoomWithDetails(
    val id: String,
    val title: String? = null,
    val language: String? = null,
    val host_id: String? = null,
    val created_at: String? = null,
    val participantCount: Int,
    val participantAvatars: List<String>
)
