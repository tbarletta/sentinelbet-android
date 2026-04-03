package com.sentinelbet.app.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BetDao {

    @Query("SELECT * FROM bets ORDER BY date ASC")
    fun observeAll(): Flow<List<BetEntity>>

    @Query("SELECT * FROM bets ORDER BY date ASC")
    suspend fun getAll(): List<BetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bet: BetEntity): Long

    @Update
    suspend fun update(bet: BetEntity)

    @Query("DELETE FROM bets WHERE id = :id")
    suspend fun deleteById(id: Int)
}
