package com.sentinelbet.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelbet.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/** 0 = follow system, 1 = force light, 2 = force dark */
enum class ThemeMode(val value: Int) {
    System(0), Light(1), Dark(2);
    companion object {
        fun from(v: Int) = entries.firstOrNull { it.value == v } ?: System
    }
}

data class ThemeState(
    val themeMode: ThemeMode = ThemeMode.System,
    val nightShiftEnabled: Boolean = false,
    val nightShiftIntensity: Int = 40,       // 0-100
    val nightShiftBedtime: Int = 22,         // 0-23 hour
    val nightShiftWake: Int = 7,             // 0-23 hour
    val nightShiftActive: Boolean = false,   // computed: is NS on right now?
)

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val settings: SettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ThemeState())
    val state: StateFlow<ThemeState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val themeMode    = ThemeMode.from(settings.getThemeMode())
            val nsEnabled    = settings.getNightShiftEnabled()
            val nsIntensity  = settings.getNightShiftIntensity()
            val nsBedtime    = settings.getNightShiftBedtime()
            val nsWake       = settings.getNightShiftWake()
            _state.value = ThemeState(
                themeMode          = themeMode,
                nightShiftEnabled  = nsEnabled,
                nightShiftIntensity = nsIntensity,
                nightShiftBedtime  = nsBedtime,
                nightShiftWake     = nsWake,
                nightShiftActive   = nsEnabled && isInNightShiftWindow(nsBedtime, nsWake),
            )
        }
    }

    // ── Theme Mode ──────────────────────────────────────────────────────────

    fun setThemeMode(mode: ThemeMode) {
        _state.value = _state.value.copy(themeMode = mode)
        viewModelScope.launch { settings.setThemeMode(mode.value) }
    }

    // ── Night Shift ─────────────────────────────────────────────────────────

    fun setNightShiftEnabled(enabled: Boolean) {
        val s = _state.value
        val active = enabled && isInNightShiftWindow(s.nightShiftBedtime, s.nightShiftWake)
        _state.value = s.copy(nightShiftEnabled = enabled, nightShiftActive = active)
        viewModelScope.launch { settings.setNightShiftEnabled(enabled) }
    }

    fun setNightShiftIntensity(intensity: Int) {
        _state.value = _state.value.copy(nightShiftIntensity = intensity)
        viewModelScope.launch { settings.setNightShiftIntensity(intensity) }
    }

    fun setNightShiftBedtime(hour: Int) {
        val s = _state.value
        val active = s.nightShiftEnabled && isInNightShiftWindow(hour, s.nightShiftWake)
        _state.value = s.copy(nightShiftBedtime = hour, nightShiftActive = active)
        viewModelScope.launch { settings.setNightShiftBedtime(hour) }
    }

    fun setNightShiftWake(hour: Int) {
        val s = _state.value
        val active = s.nightShiftEnabled && isInNightShiftWindow(s.nightShiftBedtime, hour)
        _state.value = s.copy(nightShiftWake = hour, nightShiftActive = active)
        viewModelScope.launch { settings.setNightShiftWake(hour) }
    }

    /** Manually force-toggle Night Shift (overrides schedule for current session) */
    fun toggleNightShiftNow() {
        val s = _state.value
        _state.value = s.copy(nightShiftActive = !s.nightShiftActive)
    }

    /** Refresh the nightShiftActive flag against the current time */
    fun refreshNightShiftActive() {
        val s = _state.value
        if (s.nightShiftEnabled) {
            _state.value = s.copy(
                nightShiftActive = isInNightShiftWindow(s.nightShiftBedtime, s.nightShiftWake)
            )
        }
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private fun isInNightShiftWindow(bedtime: Int, wake: Int): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return if (bedtime > wake) {
            // spans midnight, e.g. 22 → 7
            hour >= bedtime || hour < wake
        } else {
            // same-day window, e.g. 14 → 16
            hour in bedtime until wake
        }
    }
}
