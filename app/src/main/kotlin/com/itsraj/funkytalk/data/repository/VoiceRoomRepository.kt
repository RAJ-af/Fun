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

    suspend fun getActiveVoiceRooms(tagFilter: String? = null): List<VoiceRoomWithDetails> = withContext(Dispatchers.IO) {
        try {
            Log.d("VoiceRoomRepository", "Fetching active voice rooms (filter: $tagFilter)...")
            val rooms = supabase.postgrest["voice_rooms"]
                .select(Columns.raw("*, room_participants(*, profiles(*))")) {
                    if (tagFilter != null && tagFilter != "discover") {
                        filter {
                            val dbTag = tagFilter.lowercase()
                            eq("tag", dbTag)
                        }
                    }
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
                    country_code = room.country_code,
                    tag = room.tag,
                    room_type = room.room_type,
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

    suspend fun getRoom(roomId: String): VoiceRoom? = withContext(Dispatchers.IO) {
        try {
            supabase.postgrest["voice_rooms"].select {
                filter { eq("id", roomId) }
            }.decodeSingleOrNull<VoiceRoom>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createRoom(
        title: String,
        language: String,
        countryCode: String,
        tag: String,
        roomType: String,
        hostId: String
    ): VoiceRoom? = withContext(Dispatchers.IO) {
        try {
            val room = VoiceRoom(
                id = java.util.UUID.randomUUID().toString(),
                title = title,
                language = language,
                country_code = countryCode,
                tag = tag,
                room_type = roomType,
                host_id = hostId,
                created_at = Instant.now().toString()
            )
            val inserted = supabase.postgrest["voice_rooms"].insert(room).decodeSingle<VoiceRoom>()
            Log.d("VoiceRoomRepository", "Room created successfully: ${inserted.id}")

            // Creator must be host
            joinRoom(inserted.id, hostId, "host")

            inserted
        } catch (e: Exception) {
            Log.e("VoiceRoomRepository", "Room creation failure: ${e.message}")
            null
        }
    }

    suspend fun joinRoom(roomId: String, userId: String, role: String = "listener") = withContext(Dispatchers.IO) {
        try {
            // Check if user is already in the room
            val existing = supabase.postgrest["room_participants"].select {
                filter {
                    eq("room_id", roomId)
                    eq("user_id", userId)
                }
            }.decodeSingleOrNull<RoomParticipant>()

            if (existing == null) {
                val participant = RoomParticipant(
                    room_id = roomId,
                    user_id = userId,
                    role = role,
                    joined_at = Instant.now().toString()
                )
                supabase.postgrest["room_participants"].insert(participant)
                Log.d("VoiceRoomRepository", "User $userId joined room $roomId")
            } else {
                Log.d("VoiceRoomRepository", "User $userId is already in room $roomId")
            }
        } catch (e: Exception) {
            Log.e("VoiceRoomRepository", "Error joining room: ${e.message}")
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
            Log.d("VoiceRoomRepository", "User $userId left room $roomId")

            // Check if room is empty and delete it
            val remaining = supabase.postgrest["room_participants"].select {
                filter {
                    eq("room_id", roomId)
                }
            }.decodeList<RoomParticipant>()

            if (remaining.isEmpty()) {
                supabase.postgrest["voice_rooms"].delete {
                    filter {
                        eq("id", roomId)
                    }
                }
                Log.d("VoiceRoomRepository", "Room $roomId deleted as it's empty")
            }
        } catch (e: Exception) {
            Log.e("VoiceRoomRepository", "Error leaving/cleaning room: ${e.message}")
        }
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
