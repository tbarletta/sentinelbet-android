package com.sentinelbet.app.data.repository

import com.sentinelbet.app.data.db.BetDao
import com.sentinelbet.app.data.db.BetEntity
import com.sentinelbet.app.data.db.BetResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BetRepository @Inject constructor(
    private val dao: BetDao,
) {
    fun observeBets(): Flow<List<BetEntity>> = dao.observeAll()

    suspend fun saveBet(bet: BetEntity): BetEntity {
        return if (bet.id == 0) {
            val newId = dao.insert(bet)
            bet.copy(id = newId.toInt())
        } else {
            dao.update(bet)
            bet
        }
    }

    suspend fun deleteBet(id: Int) = dao.deleteById(id)
}
