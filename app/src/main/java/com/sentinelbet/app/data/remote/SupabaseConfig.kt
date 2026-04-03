package com.sentinelbet.app.data.remote

/**
 * Supabase project configuration.
 *
 * These values are safe to ship in the client — Supabase uses Row Level Security (RLS)
 * to protect data. The anon key is intentionally public.
 *
 * Replace SUPABASE_URL and SUPABASE_ANON_KEY with your project values from:
 * https://supabase.com/dashboard/project/_/settings/api
 */
object SupabaseConfig {
    const val SUPABASE_URL       = "https://rkopabmpbuzzqvktgoii.supabase.co"
    const val SUPABASE_ANON_KEY  = "sb_publishable_FuIfLzlhu3WghlzEhHdwkg_EaJ6u6tP"
    const val GOOGLE_WEB_CLIENT_ID = "242839515488-4c9a3b3486kn2k2ung1hj04mfv7dgq8d.apps.googleusercontent.com"
}
