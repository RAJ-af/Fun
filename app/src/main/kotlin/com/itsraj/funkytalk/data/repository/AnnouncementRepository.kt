package com.itsraj.funkytalk.data.repository

import com.itsraj.funkytalk.data.model.Announcement
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AnnouncementRepository(private val supabase: SupabaseClient) {

    suspend fun getAnnouncements(): List<Announcement> = withContext(Dispatchers.IO) {
        try {
            supabase.postgrest["announcements"]
                .select {
                    order("created_at", order = Order.DESCENDING)
                }
                .decodeList<Announcement>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getLatestAnnouncement(): Announcement? = withContext(Dispatchers.IO) {
        try {
            supabase.postgrest["announcements"]
                .select {
                    order("created_at", order = Order.DESCENDING)
                    limit(1)
                }
                .decodeSingleOrNull<Announcement>()
        } catch (e: Exception) {
            null
        }
    }
}
