package com.itsraj.funkytalk.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserHobby(
    val id: Int? = null,
    val user_id: String,
    val hobby: String
)
