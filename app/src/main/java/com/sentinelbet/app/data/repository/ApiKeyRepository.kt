package com.sentinelbet.app.data.repository

import com.sentinelbet.app.data.secure.SecurePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyRepository @Inject constructor(
    private val securePrefs: SecurePreferences,
) {

    private val _apiKey = MutableStateFlow(loadKey())
    val apiKey: StateFlow<String?> = _apiKey.asStateFlow()

    private fun loadKey(): String? =
        securePrefs.getString("anthropic_api_key", null)?.takeIf { it.isNotBlank() }

    fun getApiKey(): String? = loadKey()

    fun saveApiKey(key: String) {
        securePrefs.putString("anthropic_api_key", key.trim())
        _apiKey.value = key.trim()
    }

    fun clearApiKey() {
        securePrefs.remove("anthropic_api_key")
        _apiKey.value = null
    }
}
