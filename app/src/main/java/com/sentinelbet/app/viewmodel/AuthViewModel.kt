package com.sentinelbet.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelbet.app.data.repository.AuthRepository
import com.sentinelbet.app.data.repository.SubscriptionRepository
import com.sentinelbet.app.domain.entities.AppUser
import com.sentinelbet.app.domain.entities.SubscriptionPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    data object Loading       : AuthState()
    data object Unauthenticated : AuthState()
    data class  Authenticated(val user: AppUser) : AuthState()
    data class  Error(val message: String)        : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val subscriptionRepository: SubscriptionRepository,
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Observe session status changes from Supabase
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _authState.value = if (user != null) {
                    AuthState.Authenticated(user)
                } else {
                    AuthState.Unauthenticated
                }
            }
        }
    }

    /** Returns true if the current user has access to the given feature. */
    fun canAccess(feature: SubscriptionRepository.Feature): Boolean {
        val state = _authState.value
        val plan  = (state as? AuthState.Authenticated)?.user?.plan ?: SubscriptionPlan.FREE
        return subscriptionRepository.canAccess(plan, feature)
    }

    val currentUser: AppUser?
        get() = (_authState.value as? AuthState.Authenticated)?.user

    val currentPlan: SubscriptionPlan
        get() = currentUser?.plan ?: SubscriptionPlan.FREE

    // ── Actions ─────────────────────────────────────────────────────────────

    /**
     * Signs in with Google using the One Tap Credential Manager.
     * Requires the calling Activity's context (not application context).
     */
    fun signInWithGoogle(activityContext: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = authRepository.signInWithGoogle(activityContext)
                _authState.value = AuthState.Authenticated(user)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "Falha ao entrar com Google. Tente novamente."
                )
            }
        }
    }

    /**
     * Signs in with email + password.
     */
    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = authRepository.signInWithEmail(email, password)
                _authState.value = AuthState.Authenticated(user)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    when {
                        e.message?.contains("Invalid login credentials") == true ->
                            "Email ou senha incorretos."
                        e.message?.contains("Email not confirmed") == true ->
                            "Confirme seu email antes de entrar."
                        else -> e.message ?: "Falha ao entrar. Tente novamente."
                    }
                )
            }
        }
    }

    /**
     * Creates a new account with email + password.
     * Supabase sends a confirmation email automatically.
     */
    fun signUpWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                authRepository.signUpWithEmail(email, password)
                // After sign-up, user needs to confirm email — show message
                _authState.value = AuthState.Error(
                    "Conta criada! Verifique seu email para confirmar o cadastro."
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    when {
                        e.message?.contains("already registered") == true ->
                            "Este email já está cadastrado. Tente entrar."
                        else -> e.message ?: "Falha ao criar conta. Tente novamente."
                    }
                )
            }
        }
    }

    /** Signs out and clears the session. */
    fun signOut() {
        viewModelScope.launch {
            try {
                authRepository.signOut()
            } catch (_: Exception) { /* session already cleared */ }
            _authState.value = AuthState.Unauthenticated
        }
    }

    /** Clears any error message and goes back to the appropriate state. */
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = if (authRepository.isAuthenticated()) {
                val user = authRepository.currentUserSync()
                if (user != null) AuthState.Authenticated(user)
                else AuthState.Unauthenticated
            } else {
                AuthState.Unauthenticated
            }
        }
    }
}
