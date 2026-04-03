package com.sentinelbet.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BetResult { WIN, LOSS, PENDING }

@Entity(tableName = "bets")
data class BetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val match: String,
    val market: String,
    val odd: Double,
    val stake: Double,
    val result: BetResult = BetResult.PENDING,
    val profit: Double = 0.0,
    val date: String,
    val notes: String? = null,
)
