package com.itsraj.funkytalk.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserLanguage(
    val id: Int? = null,
    val user_id: String,
    val language: String,
    val type: String // "native" or "learning"
)
