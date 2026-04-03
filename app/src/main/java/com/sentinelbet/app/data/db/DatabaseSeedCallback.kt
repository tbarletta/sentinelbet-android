package com.sentinelbet.app.data.db

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DatabaseSeedCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Seed initial settings
        db.execSQL("INSERT OR IGNORE INTO settings (key, value) VALUES ('bankroll', '1000.0')")
        db.execSQL("INSERT OR IGNORE INTO settings (key, value) VALUES ('kelly_fraction', '0.25')")
        // Seed demo bets
        val now = LocalDateTime.now()
        val fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val demos = listOf(
            Triple("Man City vs Arsenal",      "Casa",        Triple(1.72,  50.0, Triple("WIN",  36.0,  8))),
            Triple("Real Madrid vs Barcelona", "Empate",      Triple(3.40,  30.0, Triple("LOSS", -30.0, 7))),
            Triple("Bayern vs Dortmund",       "Casa",        Triple(1.55,  80.0, Triple("WIN",  44.0,  6))),
            Triple("Flamengo vs Palmeiras",    "Visitante",   Triple(2.10,  40.0, Triple("WIN",  44.0,  5))),
            Triple("Inter vs Napoli",          "Casa",        Triple(1.90,  60.0, Triple("LOSS", -60.0, 4))),
            Triple("Tottenham vs Chelsea",     "Mais de 2.5", Triple(1.85,  45.0, Triple("WIN",  38.25, 3))),
        )
        for ((match, market, d) in demos) {
            val (odd, stake, r) = d
            val (result, profit, daysAgo) = r
            val date = now.minusDays(daysAgo.toLong()).format(fmt)
            db.execSQL(
                "INSERT INTO bets (match, market, odd, stake, result, profit, date) VALUES (?, ?, ?, ?, ?, ?, ?)",
                arrayOf(match, market, odd, stake, result, profit, date)
            )
        }
    }
}
