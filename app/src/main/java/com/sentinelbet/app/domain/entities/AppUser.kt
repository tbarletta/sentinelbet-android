package com.sentinelbet.app.domain.entities

/**
 * Domain entity representing the authenticated user.
 * Decoupled from Supabase's UserInfo model.
 */
data class AppUser(
    val id: String,
    val email: String?,
    val displayName: String?,
    val avatarUrl: String?,
    val plan: SubscriptionPlan = SubscriptionPlan.FREE,
)

enum class SubscriptionPlan {
    FREE,
    PRO;

    val isPro: Boolean get() = this == PRO
}
