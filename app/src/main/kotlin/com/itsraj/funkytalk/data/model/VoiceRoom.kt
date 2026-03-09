package com.itsraj.funkytalk.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VoiceRoom(
    val id: String,
    val title: String,
    val language: String,
    val country_code: String,
    val host_id: String,
    val participants_count: Int = 0,
    val participant_avatars: List<String> = emptyList(),
    val created_at: String
)
