package com.itsraj.funkytalk.data.repository

import com.itsraj.funkytalk.data.model.RoomParticipant
import com.itsraj.funkytalk.data.model.VoiceRoom
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.Instant

class VoiceRoomRepository(private val supabase: SupabaseClient) {

    suspend fun getActiveVoiceRooms(): List<VoiceRoom> = withContext(Dispatchers.IO) {
        try {
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

    suspend fun createRoom(title: String, language: String, hostId: String): VoiceRoom? = withContext(Dispatchers.IO) {
        try {
            val room = VoiceRoom(
                id = java.util.UUID.randomUUID().toString(),
                title = title,
                language = language,
                country_code = "us", // Default
                host_id = hostId,
                created_at = Instant.now().toString()
            )
            val inserted = supabase.postgrest["voice_rooms"].insert(room).decodeSingle<VoiceRoom>()
            joinRoom(inserted.id, hostId, "host")
            inserted
        } catch (e: Exception) {
            null
        }
    }

    suspend fun joinRoom(roomId: String, userId: String, role: String = "listener") = withContext(Dispatchers.IO) {
        try {
            val participant = RoomParticipant(
                room_id = roomId,
                user_id = userId,
                role = role,
                joined_at = Instant.now().toString()
            )
            supabase.postgrest["room_participants"].insert(participant)
        } catch (e: Exception) {
            // Check for duplicates in actual app logic
        }
    }

    suspend fun leaveRoom(roomId: String, userId: String) = withContext(Dispatchers.IO) {
        try {
            supabase.postgrest["room_participants"].delete {
                filter {
                    eq("room_id", roomId)
                    eq("user_id", userId)
                }
            }
        } catch (e: Exception) { }
    }

    fun observeParticipantChanges(): Flow<PostgresAction> {
        val channel = supabase.channel("room_participants_changes")
        val flow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "room_participants"
        }
        return flow
    }
}
