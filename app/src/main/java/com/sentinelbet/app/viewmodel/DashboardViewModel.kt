package com.sentinelbet.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelbet.app.data.db.BetEntity
import com.sentinelbet.app.data.db.BetResult
import com.sentinelbet.app.data.repository.BetRepository
import com.sentinelbet.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardSummary(
    val bankroll: Double = 1000.0,
    val kellyFraction: Double = 0.25,
    val totalProfit: Double = 0.0,
    val roi: Double = 0.0,
    val winRate: Double = 0.0,
    val totalBets: Int = 0,
    val recentBets: List<BetEntity> = emptyList(),
)

sealed class DashboardUiState {
    data object Loading : DashboardUiState()
    data class Loaded(val summary: DashboardSummary) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val betRepo: BetRepository,
    private val settingsRepo: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            betRepo.observeBets().collectLatest { bets ->
                load(bets)
            }
        }
    }

    private suspend fun load(bets: List<BetEntity> = emptyList()) {
        val bankroll = settingsRepo.getBankroll()
        val kf       = settingsRepo.getKellyFraction()
        val wins     = bets.count { it.result == BetResult.WIN }
        val totalStake  = bets.sumOf { it.stake }
        val totalProfit = bets.sumOf { it.profit }
        _uiState.value = DashboardUiState.Loaded(
            DashboardSummary(
                bankroll      = bankroll,
                kellyFraction = kf,
                totalProfit   = totalProfit,
                roi           = if (totalStake > 0) (totalProfit / totalStake) * 100 else 0.0,
                winRate       = if (bets.isNotEmpty()) wins.toDouble() / bets.size else 0.0,
                totalBets     = bets.size,
                recentBets    = bets.reversed().take(5),
            )
        )
    }

    fun updateBankroll(value: Double) = viewModelScope.launch {
        settingsRepo.setBankroll(value)
    }

    fun updateKellyFraction(value: Double) = viewModelScope.launch {
        settingsRepo.setKellyFraction(value)
    }
}
