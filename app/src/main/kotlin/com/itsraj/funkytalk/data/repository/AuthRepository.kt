package com.itsraj.funkytalk.data.repository

import com.itsraj.funkytalk.FunkyTalkApp
import com.itsraj.funkytalk.data.model.UserHobby
import com.itsraj.funkytalk.data.model.UserLanguage
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

    suspend fun getProfile(id: String): UserProfile? {
        return try {
            val response = supabase.postgrest["profiles"].select {
                filter {
                    eq("id", id)
                }
            }.decodeSingleOrNull<UserProfile>()
            response
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateProfile(profile: UserProfile) {
        supabase.postgrest["profiles"].update(profile) {
            filter {
                eq("id", profile.id)
            }
        }
    }

    suspend fun saveProfile(profile: UserProfile) {
        supabase.postgrest["profiles"].upsert(profile)
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

    suspend fun addUserLanguages(languages: List<UserLanguage>) {
        supabase.postgrest["user_languages"].insert(languages)
    }

    suspend fun addUserHobbies(hobbies: List<UserHobby>) {
        supabase.postgrest["user_hobbies"].insert(hobbies)
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
                val profile = UserProfile(
                    id = user.id,
                    email = user.email ?: "",
                    created_at = Date().toString()
                )
                supabase.postgrest["profiles"].upsert(profile)
            }
        } catch (e: Exception) {
            // Log error
        }
    }

    suspend fun logout() {
        auth.signOut()
    }
}
