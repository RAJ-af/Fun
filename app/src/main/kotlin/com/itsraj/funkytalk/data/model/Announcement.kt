package com.itsraj.funkytalk.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Announcement(
    val id: String,
    val title: String,
    val message: String,
    val date: String,
    val created_at: String? = null
)
