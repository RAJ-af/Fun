package com.itsraj.funkytalk.data.repository

import com.itsraj.funkytalk.data.model.ParticipantWithProfile
import com.itsraj.funkytalk.data.model.RoomParticipant
import android.util.Log
import com.itsraj.funkytalk.data.model.VoiceRoom
import com.itsraj.funkytalk.data.model.VoiceRoomWithDetails
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.Instant

class VoiceRoomRepository(private val supabase: SupabaseClient) {

    suspend fun getActiveVoiceRooms(): List<VoiceRoomWithDetails> = withContext(Dispatchers.IO) {
        try {
            Log.d("VoiceRoomRepository", "Fetching active voice rooms with participants...")
            // Fetch rooms and their participants in a single query to avoid N+1
            val rooms = supabase.postgrest["voice_rooms"]
                .select(Columns.raw("*, room_participants(*, profiles(*))")) {
                    order("created_at", order = Order.DESCENDING)
                    limit(50)
                }
                .decodeList<VoiceRoom>()

            Log.d("VoiceRoomRepository", "Fetched ${rooms.size} rooms from database")

            if (rooms.isEmpty()) {
                Log.d("VoiceRoomRepository", "No rooms found in database")
                return@withContext emptyList<VoiceRoomWithDetails>()
            }

            rooms.map { room ->
                val participants = room.room_participants ?: emptyList()
                Log.d("VoiceRoomRepository", "Room ${room.id} (${room.title}) has ${participants.size} participants")

                VoiceRoomWithDetails(
                    id = room.id,
                    title = room.title,
                    language = room.language,
                    host_id = room.host_id,
                    created_at = room.created_at,
                    participantCount = participants.size,
                    participantAvatars = participants.take(4).mapNotNull { it.profiles.avatar_url }
                )
            }
        } catch (e: Exception) {
            Log.e("VoiceRoomRepository", "FATAL error in getActiveVoiceRooms: ${e.message}", e)
            throw e
        }
    }

    suspend fun createRoom(title: String, language: String, hostId: String): VoiceRoom? = withContext(Dispatchers.IO) {
        try {
            val room = VoiceRoom(
                id = java.util.UUID.randomUUID().toString(),
                title = title,
                language = language,
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

    suspend fun getParticipants(roomId: String): List<ParticipantWithProfile> = withContext(Dispatchers.IO) {
        try {
            val result = supabase.postgrest["room_participants"]
                .select(Columns.raw("*, profiles(*)")) {
                    filter {
                        eq("room_id", roomId)
                    }
                }
                .decodeList<ParticipantWithProfile>()
            Log.d("VoiceRoomRepository", "Fetched ${result.size} participants for room $roomId")
            result
        } catch (e: Exception) {
            Log.e("VoiceRoomRepository", "Error fetching participants for room $roomId: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun updateParticipantRole(roomId: String, userId: String, role: String) = withContext(Dispatchers.IO) {
        try {
            supabase.postgrest["room_participants"].update(mapOf("role" to role)) {
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

    fun observeRoomChanges(): Flow<PostgresAction> {
        val channel = supabase.channel("voice_rooms_changes")
        val flow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "voice_rooms"
        }
        return flow
    }
}
