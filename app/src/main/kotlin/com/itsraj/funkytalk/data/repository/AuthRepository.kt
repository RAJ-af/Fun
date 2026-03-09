package com.itsraj.funkytalk.data.repository

import com.itsraj.funkytalk.FunkyTalkApp
import com.itsraj.funkytalk.data.model.InitialProfile
import com.itsraj.funkytalk.data.model.ProfileUpdate
import com.itsraj.funkytalk.data.model.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import java.time.Instant

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
        val update = ProfileUpdate(
            username = profile.username,
            profile_name = profile.profile_name,
            avatar_url = profile.avatar_url,
            age = profile.age,
            gender = profile.gender,
            country = profile.country,
            native_languages = profile.native_languages,
            learning_languages = profile.learning_languages,
            hobbies = profile.hobbies,
            bio = profile.bio,
            last_seen = Instant.now().toString()
        )

        supabase.postgrest["profiles"].update(update) {
            filter {
                eq("auth_user_id", profile.auth_user_id)
            }
        }
    }

    suspend fun insertInitialProfile(userId: String, email: String, username: String, profileName: String) {
        val payload = InitialProfile(
            auth_user_id = userId,
            email = email,
            username = username,
            profile_name = profileName,
            created_at = Instant.now().toString()
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

    suspend fun resendConfirmationEmail(email: String) {
        auth.resendEmail(io.github.jan.supabase.auth.OtpType.Email.SIGNUP, email)
    }

    private suspend fun createInitialProfileRow(user: UserInfo) {
        try {
            val existing = getProfile(user.id)
            if (existing == null) {
                val payload = InitialProfile(
                    auth_user_id = user.id,
                    email = user.email ?: "",
                    created_at = Instant.now().toString()
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
