package com.itsraj.funkytalk.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VoiceRoom(
    val id: String,
    val title: String,
    val language: String,
    val host_id: String,
    val participants: Int = 0,
    val created_at: String
)
