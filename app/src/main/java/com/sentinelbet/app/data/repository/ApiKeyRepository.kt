package com.sentinelbet.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("sentinel_prefs", Context.MODE_PRIVATE)

    private val _apiKey = MutableStateFlow(loadKey())
    val apiKey: StateFlow<String?> = _apiKey.asStateFlow()

    private fun loadKey(): String? =
        prefs.getString("anthropic_api_key", null)?.takeIf { it.isNotBlank() }

    fun getApiKey(): String? = loadKey()

    fun saveApiKey(key: String) {
        prefs.edit().putString("anthropic_api_key", key.trim()).commit()
        _apiKey.value = key.trim()
    }

    fun clearApiKey() {
        prefs.edit().remove("anthropic_api_key").commit()
        _apiKey.value = null
    }
}
