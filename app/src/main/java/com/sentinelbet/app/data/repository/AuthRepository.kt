package com.sentinelbet.app.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.sentinelbet.app.data.remote.SupabaseConfig
import com.sentinelbet.app.domain.entities.AppUser
import com.sentinelbet.app.domain.entities.SubscriptionPlan
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val supabase: SupabaseClient,
    @ApplicationContext private val context: Context,
    private val subscriptionRepository: SubscriptionRepository,
) {

    /**
     * Flow of the current authenticated user.
     * Emits null when user is not logged in.
     */
    val currentUser: Flow<AppUser?> = supabase.auth.sessionStatus.map { status ->
        when (status) {
            is SessionStatus.Authenticated -> {
                val user = supabase.auth.currentUserOrNull() ?: return@map null
                val plan = subscriptionRepository.getPlan(user.id)
                AppUser(
                    id          = user.id,
                    email       = user.email,
                    displayName = user.userMetadata?.get("full_name")?.toString()?.trim('"'),
                    avatarUrl   = user.userMetadata?.get("avatar_url")?.toString()?.trim('"'),
                    plan        = plan,
                )
            }
            else -> null
        }
    }

    /** Returns the current user synchronously (null if not authenticated). */
    fun currentUserSync(): AppUser? {
        val user = supabase.auth.currentUserOrNull() ?: return null
        return AppUser(
            id          = user.id,
            email       = user.email,
            displayName = user.userMetadata?.get("full_name")?.toString()?.trim('"'),
            avatarUrl   = user.userMetadata?.get("avatar_url")?.toString()?.trim('"'),
        )
    }

    /**
     * Signs in with Google using the Credential Manager (One Tap).
     * Returns the authenticated user or throws on failure.
     */
    suspend fun signInWithGoogle(activityContext: Context): AppUser {
        val credentialManager = CredentialManager.create(activityContext)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(SupabaseConfig.GOOGLE_WEB_CLIENT_ID)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(
            request = request,
            context = activityContext,
        )

        val googleCredential = GoogleIdTokenCredential.createFrom(result.credential.data)

        supabase.auth.signInWith(IDToken) {
            idToken   = googleCredential.idToken
            provider  = Google
        }

        return currentUserSync() ?: error("Sign-in succeeded but user is null")
    }

    /**
     * Signs in with email + password.
     * For users who prefer not to use Google.
     */
    suspend fun signInWithEmail(email: String, password: String): AppUser {
        supabase.auth.signInWith(io.github.jan.supabase.auth.providers.builtin.Email) {
            this.email    = email
            this.password = password
        }
        return currentUserSync() ?: error("Sign-in succeeded but user is null")
    }

    /**
     * Creates a new account with email + password.
     * Supabase sends a confirmation email automatically.
     */
    suspend fun signUpWithEmail(email: String, password: String) {
        supabase.auth.signUpWith(io.github.jan.supabase.auth.providers.builtin.Email) {
            this.email    = email
            this.password = password
        }
    }

    /** Signs out the current user. */
    suspend fun signOut() {
        supabase.auth.signOut()
    }

    /** True if a session exists (user is logged in). */
    fun isAuthenticated(): Boolean =
        supabase.auth.currentUserOrNull() != null
}
