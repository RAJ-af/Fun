package com.itsraj.funkytalk

import android.app.Application
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

class FunkyTalkApp : Application() {

    companion object {
        lateinit var supabase: SupabaseClient
    }

    override fun onCreate() {
        super.onCreate()

        supabase = createSupabaseClient(
            supabaseUrl = "https://dulpqochkxtamqztauea.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImR1bHBxb2Noa3h0YW1xenRhdWVhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI4MDA3OTUsImV4cCI6MjA4ODM3Njc5NX0.yrpyrNkNnsXf1fXvVpxPlFu3IeQn6Oz0TK6BoCrOd-o"
        ) {
            install(Auth) {
                flowType = FlowType.PKCE
                scheme = "funkytalk"
                host = "auth-callback"
            }
            install(Postgrest)
        }
    }
}
