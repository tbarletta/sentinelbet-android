package com.sentinelbet.app.data.db

import androidx.room.*

@Dao
interface SettingsDao {

    @Query("SELECT value FROM settings WHERE `key` = :key")
    suspend fun get(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(setting: SettingEntity)
}
