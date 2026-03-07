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
            supabase.postgrest["voice_rooms"]
                .select {
                    order("created_at", order = Order.DESCENDING)
                }
                .decodeList<VoiceRoom>()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
