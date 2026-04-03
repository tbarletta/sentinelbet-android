package com.sentinelbet.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class BetResultConverter {
    @TypeConverter
    fun fromString(value: String): BetResult = BetResult.valueOf(value.uppercase())
    @TypeConverter
    fun toString(result: BetResult): String = result.name
}

@Database(
    entities = [BetEntity::class, SettingEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(BetResultConverter::class)
abstract class SentinelBetDatabase : RoomDatabase() {
    abstract fun betDao(): BetDao
    abstract fun settingsDao(): SettingsDao
}
