package com.itsraj.funkytalk.data.repository

import com.itsraj.funkytalk.FunkyTalkApp
import com.itsraj.funkytalk.data.model.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import java.util.Date

class AuthRepository {
    private val supabase = FunkyTalkApp.supabase
    private val auth = supabase.auth

    val currentUser: UserInfo? get() = auth.currentUserOrNull()
    val sessionStatus: Flow<SessionStatus> = auth.sessionStatus

    suspend fun getProfile(userId: String): UserProfile? {
        return try {
            supabase.postgrest["profiles"].select {
                filter {
                    eq("auth_user_id", userId)
                }
            }.decodeSingleOrNull<UserProfile>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateProfile(profile: UserProfile) {
        // Only update allowed fields, filtering by auth_user_id
        // Using a map to ensure 'id' is not sent
        val updateMap = mutableMapOf<String, Any?>()
        profile.username?.let { updateMap["username"] = it }
        profile.profile_name?.let { updateMap["profile_name"] = it }
        profile.avatar_url?.let { updateMap["avatar_url"] = it }
        profile.age?.let { updateMap["age"] = it }
        profile.gender?.let { updateMap["gender"] = it }
        profile.native_languages?.let { updateMap["native_languages"] = it }
        profile.learning_languages?.let { updateMap["learning_languages"] = it }
        profile.country?.let { updateMap["country"] = it }
        profile.hobbies?.let { updateMap["hobbies"] = it }
        profile.bio?.let { updateMap["bio"] = it }
        updateMap["last_seen"] = Date().toString()

        supabase.postgrest["profiles"].update(updateMap) {
            filter {
                eq("auth_user_id", profile.auth_user_id)
            }
        }
    }

    suspend fun insertInitialProfile(userId: String, email: String, username: String, profileName: String) {
        val payload = mapOf(
            "auth_user_id" to userId,
            "username" to username,
            "profile_name" to profileName,
            "email" to email
        )
        supabase.postgrest["profiles"].insert(payload)
    }

    suspend fun uploadAvatar(userId: String, bytes: ByteArray): String {
        val fileName = "$userId/${System.currentTimeMillis()}.png"
        val bucket = supabase.storage.from("avatars")
        bucket.upload(fileName, bytes) {
            upsert = true
        }
        return bucket.publicUrl(fileName)
    }

    suspend fun isUsernameUnique(username: String): Boolean {
        val response = supabase.postgrest["profiles"].select {
            filter {
                eq("username", username)
            }
        }.decodeList<UserProfile>()
        return response.isEmpty()
    }

    suspend fun login(email: String, pass: String): UserInfo? {
        auth.signInWith(Email) {
            this.email = email
            this.password = pass
        }
        val user = auth.currentUserOrNull()
        if (user != null) {
            createInitialProfileRow(user)
        }
        return user
    }

    suspend fun loginWithGoogle() {
        auth.signInWith(Google)
    }

    suspend fun signup(email: String, pass: String): UserInfo? {
        auth.signUpWith(Email) {
            this.email = email
            this.password = pass
        }
        val user = auth.currentUserOrNull()
        if (user != null) {
            createInitialProfileRow(user)
        }
        return user
    }

    private suspend fun createInitialProfileRow(user: UserInfo) {
        try {
            val existing = getProfile(user.id)
            if (existing == null) {
                val payload = mapOf(
                    "auth_user_id" to user.id,
                    "email" to (user.email ?: ""),
                    "created_at" to Date().toString()
                )
                supabase.postgrest["profiles"].insert(payload)
            }
        } catch (e: Exception) {
            // Log error
        }
    }

    suspend fun logout() {
        auth.signOut()
    }
}
