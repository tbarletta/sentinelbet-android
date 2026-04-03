package com.sentinelbet.app.data.repository

import com.sentinelbet.app.data.db.SettingEntity
import com.sentinelbet.app.data.db.SettingsDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dao: SettingsDao,
) {
    suspend fun getBankroll(): Double =
        dao.get("bankroll")?.toDoubleOrNull() ?: 1000.0

    suspend fun getKellyFraction(): Double =
        dao.get("kelly_fraction")?.toDoubleOrNull() ?: 0.25

    suspend fun setBankroll(value: Double) =
        dao.upsert(SettingEntity("bankroll", value.toString()))

    suspend fun setKellyFraction(value: Double) =
        dao.upsert(SettingEntity("kelly_fraction", value.toString()))

    suspend fun getApiFootballKey(): String? =
        dao.get("api_football_key")?.takeIf { it.isNotBlank() }

    suspend fun setApiFootballKey(value: String) =
        dao.upsert(SettingEntity("api_football_key", value.trim()))

    // ── Theme ──────────────────────────────────────────────────────────────
    /** 0 = follow system, 1 = force light, 2 = force dark */
    suspend fun getThemeMode(): Int =
        dao.get("theme_mode")?.toIntOrNull() ?: 0

    suspend fun setThemeMode(value: Int) =
        dao.upsert(SettingEntity("theme_mode", value.toString()))

    // ── Night Shift ────────────────────────────────────────────────────────
    suspend fun getNightShiftEnabled(): Boolean =
        dao.get("night_shift_enabled") == "true"

    suspend fun setNightShiftEnabled(value: Boolean) =
        dao.upsert(SettingEntity("night_shift_enabled", value.toString()))

    /** Intensity of the amber overlay 0..100 */
    suspend fun getNightShiftIntensity(): Int =
        dao.get("night_shift_intensity")?.toIntOrNull() ?: 40

    suspend fun setNightShiftIntensity(value: Int) =
        dao.upsert(SettingEntity("night_shift_intensity", value.toString()))

    /** Bedtime hour (0-23) when night shift auto-activates */
    suspend fun getNightShiftBedtime(): Int =
        dao.get("night_shift_bedtime")?.toIntOrNull() ?: 22

    suspend fun setNightShiftBedtime(value: Int) =
        dao.upsert(SettingEntity("night_shift_bedtime", value.toString()))

    /** Wake hour (0-23) when night shift auto-deactivates */
    suspend fun getNightShiftWake(): Int =
        dao.get("night_shift_wake")?.toIntOrNull() ?: 7

    suspend fun setNightShiftWake(value: Int) =
        dao.upsert(SettingEntity("night_shift_wake", value.toString()))
}
