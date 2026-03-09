package com.itsraj.funkytalk

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

class FunkyTalkApp : Application(), ImageLoaderFactory {

    companion object {
        lateinit var supabase: SupabaseClient
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
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
            install(Storage)
        }
    }
}
