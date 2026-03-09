package com.itsraj.funkytalk.data.repository

import com.itsraj.funkytalk.data.model.VoiceRoom
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VoiceRoomRepository(private val supabase: SupabaseClient) {

    suspend fun getActiveVoiceRooms(): List<VoiceRoom> = withContext(Dispatchers.IO) {
        try {
            // Note: In a real app, this would use pagination.
            supabase.postgrest["voice_rooms"]
                .select {
                    order("created_at", order = Order.DESCENDING)
                    limit(50)
                }
                .decodeList<VoiceRoom>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun joinRoom(roomId: String, userId: String) = withContext(Dispatchers.IO) {
        try {
            // Logic to register user as participant in voice_room_participants table
            val data = mapOf(
                "room_id" to roomId,
                "user_id" to userId,
                "joined_at" to java.time.Instant.now().toString()
            )
            supabase.postgrest["voice_room_participants"].insert(data)
        } catch (e: Exception) {
            // Fail silently or handle error
        }
    }
}
