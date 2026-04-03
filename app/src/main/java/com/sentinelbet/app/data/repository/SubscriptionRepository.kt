package com.sentinelbet.app.data.repository

import com.sentinelbet.app.domain.entities.SubscriptionPlan
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages user subscription state.
 *
 * Plans:
 *  - FREE  — default, no payment required
 *  - PRO   — paid, unlocks AI analysis + unlimited value bets
 *
 * The subscription record is stored in Supabase (`subscriptions` table).
 * Row Level Security ensures users can only read their own row.
 *
 * Schema (run in Supabase SQL editor):
 * ─────────────────────────────────────
 * create table if not exists public.subscriptions (
 *   user_id    uuid primary key references auth.users(id) on delete cascade,
 *   plan       text not null default 'free' check (plan in ('free', 'pro')),
 *   expires_at timestamptz,
 *   created_at timestamptz default now()
 * );
 * alter table public.subscriptions enable row level security;
 * create policy "Users read own subscription"
 *   on public.subscriptions for select
 *   using (auth.uid() = user_id);
 * ─────────────────────────────────────
 */
@Singleton
class SubscriptionRepository @Inject constructor(
    private val supabase: SupabaseClient,
) {

    /**
     * Feature gates — defines which plan each feature requires.
     */
    enum class Feature(val requiredPlan: SubscriptionPlan) {
        AI_ANALYSIS(SubscriptionPlan.PRO),
        VALUE_BETS_UNLIMITED(SubscriptionPlan.PRO),
        LIVE_PREMIUM(SubscriptionPlan.PRO),
        HISTORY_EXPORT(SubscriptionPlan.PRO),

        // Free features (always accessible)
        DASHBOARD(SubscriptionPlan.FREE),
        BANKROLL(SubscriptionPlan.FREE),
        HISTORY_BASIC(SubscriptionPlan.FREE),
        LIVE_BASIC(SubscriptionPlan.FREE),
        VALUE_BETS_LIMITED(SubscriptionPlan.FREE),
    }

    /**
     * Returns the user's current plan from Supabase.
     * Falls back to FREE on any error (fail-safe).
     */
    suspend fun getPlan(userId: String): SubscriptionPlan {
        return try {
            val result = supabase.postgrest
                .from("subscriptions")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                    limit(1)
                }
                .decodeSingleOrNull<SubscriptionRow>()

            when (result?.plan) {
                "pro" -> SubscriptionPlan.PRO
                else  -> SubscriptionPlan.FREE
            }
        } catch (e: Exception) {
            SubscriptionPlan.FREE
        }
    }

    /**
     * Checks whether the given [plan] has access to [feature].
     */
    fun canAccess(plan: SubscriptionPlan, feature: Feature): Boolean =
        plan.ordinal >= feature.requiredPlan.ordinal

    /**
     * Value-bet limit for the free plan.
     * PRO users see unlimited bets; FREE users see a preview.
     */
    fun valueBetLimit(plan: SubscriptionPlan): Int =
        if (plan.isPro) Int.MAX_VALUE else 3

    /**
     * AI analysis message limit per day for free plan.
     * PRO = unlimited.
     */
    fun aiAnalysisLimit(plan: SubscriptionPlan): Int =
        if (plan.isPro) Int.MAX_VALUE else 2

    @Serializable
    private data class SubscriptionRow(
        val user_id: String,
        val plan: String,
    )
}
