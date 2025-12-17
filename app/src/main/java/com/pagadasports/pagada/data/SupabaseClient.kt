package com.pagadasports.pagada.data

import com.pagadasports.pagada.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Singleton object that provides a configured Supabase client instance.
 *
 * Credentials are securely loaded from BuildConfig, which reads from local.properties.
 * Never hardcode credentials in this file.
 *
 * To configure:
 * 1. Add to local.properties:
 *    supabase.url=YOUR_SUPABASE_URL
 *    supabase.anon.key=YOUR_SUPABASE_ANON_KEY
 * 2. Sync Gradle
 */
object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Auth) {
            // SECURITY: Secure authentication settings
            autoLoadFromStorage = true
            autoSaveToStorage = true
        }
        install(Postgrest)
        // Add more plugins as needed:
        // install(Storage)
        // install(Realtime)
    }
}

