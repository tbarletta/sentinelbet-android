package com.sentinelbet.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelbet.app.data.db.BetEntity
import com.sentinelbet.app.data.db.BetResult
import com.sentinelbet.app.data.repository.BetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class PerformanceStats(
    val totalProfit: Double,
    val roi: Double,
    val winRate: Double,
    val totalBets: Int,
    val wins: Int,
    val losses: Int,
    val cumulativeProfits: List<Double>,
)

sealed class HistoryUiState {
    data object Loading : HistoryUiState()
    data class Loaded(val bets: List<BetEntity>, val performance: PerformanceStats) : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repo: BetRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repo.observeBets().collectLatest { bets ->
                val perf = computePerformance(bets)
                _uiState.value = HistoryUiState.Loaded(bets, perf)
            }
        }
    }

    private fun computePerformance(bets: List<BetEntity>): PerformanceStats {
        if (bets.isEmpty()) return PerformanceStats(0.0, 0.0, 0.0, 0, 0, 0, emptyList())
        val wins         = bets.count { it.result == BetResult.WIN }
        val losses       = bets.count { it.result == BetResult.LOSS }
        val totalStake   = bets.sumOf { it.stake }
        val totalProfit  = bets.sumOf { it.profit }
        var cum = 0.0
        val cumList = bets.map { b -> cum += b.profit; cum }
        return PerformanceStats(
            totalProfit   = totalProfit,
            roi           = if (totalStake > 0) (totalProfit / totalStake) * 100 else 0.0,
            winRate       = wins.toDouble() / bets.size,
            totalBets     = bets.size,
            wins          = wins,
            losses        = losses,
            cumulativeProfits = cumList,
        )
    }

    fun saveBet(bet: BetEntity) = viewModelScope.launch {
        try { repo.saveBet(bet) } catch (e: Exception) { /* handle */ }
    }

    fun deleteBet(id: Int) = viewModelScope.launch {
        try { repo.deleteBet(id) } catch (e: Exception) { /* handle */ }
    }

    fun newBet(
        match: String, market: String, odd: Double, stake: Double,
        result: BetResult, profit: Double, notes: String?,
    ) = viewModelScope.launch {
        val bet = BetEntity(
            match = match, market = market, odd = odd, stake = stake,
            result = result, profit = profit,
            date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            notes = notes,
        )
        repo.saveBet(bet)
    }
}
