package com.sentinelbet.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelbet.app.data.repository.ApiKeyRepository
import com.sentinelbet.app.domain.entities.MatchAnalysis
import com.sentinelbet.app.domain.entities.Team
import com.sentinelbet.app.domain.entities.LEAGUES
import com.sentinelbet.app.domain.entities.getTeamsForLeague
import com.sentinelbet.app.domain.usecases.ComputeAnalysisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AnalysisUiState {
    data object Idle      : AnalysisUiState()
    data object Computing : AnalysisUiState()
    data class  Ready(val analysis: MatchAnalysis) : AnalysisUiState()
    data class  Streaming(val analysis: MatchAnalysis, val streamedText: String) : AnalysisUiState()
    data class  Error(val message: String) : AnalysisUiState()
}

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val useCase: ComputeAnalysisUseCase,
    private val apiKeyRepo: ApiKeyRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnalysisUiState>(AnalysisUiState.Idle)
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    // ── Seleção de partida — sobrevive à navegação ─────────────────────────
    // Guardar aqui garante que ao voltar ao Dashboard os dropdowns
    // mostram exatamente a liga/times que o usuário escolheu.

    private val _selectedLeague = MutableStateFlow(LEAGUES.first())
    val selectedLeague: StateFlow<String> = _selectedLeague.asStateFlow()

    private val _homeTeam = MutableStateFlow(getTeamsForLeague(LEAGUES.first()).first())
    val homeTeam: StateFlow<Team> = _homeTeam.asStateFlow()

    private val _awayTeam = MutableStateFlow(
        getTeamsForLeague(LEAGUES.first()).let { teams ->
            if (teams.size > 1) teams[1] else teams.first()
        }
    )
    val awayTeam: StateFlow<Team> = _awayTeam.asStateFlow()

    // ── Selectors chamados pelo DashboardScreen ────────────────────────────

    fun selectLeague(league: String) {
        val teams = getTeamsForLeague(league)
        _selectedLeague.value = league
        _homeTeam.value = teams.first()
        _awayTeam.value = if (teams.size > 1) teams[1] else teams.first()
    }

    fun selectHomeTeam(team: Team) {
        _homeTeam.value = team
    }

    fun selectAwayTeam(team: Team) {
        _awayTeam.value = team
    }

    // ── Análise Poisson ────────────────────────────────────────────────────

    fun compute(league: String, home: Team, away: Team, kellyFraction: Double = 0.25) {
        // Persiste a seleção no ViewModel antes de computar
        _selectedLeague.value = league
        _homeTeam.value = home
        _awayTeam.value = away

        viewModelScope.launch {
            _uiState.value = AnalysisUiState.Computing
            try {
                val analysis = useCase.compute(home, away, kellyFraction)
                _uiState.value = AnalysisUiState.Ready(analysis)
            } catch (e: Exception) {
                _uiState.value = AnalysisUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    // ── AI Streaming ───────────────────────────────────────────────────────

    fun streamAiAnalysis(apiKey: String, bankroll: Double, kellyFraction: Double) {
        val current = _uiState.value
        if (current !is AnalysisUiState.Ready) return

        apiKeyRepo.saveApiKey(apiKey)
        val savedKey = apiKeyRepo.getApiKey()
        if (savedKey.isNullOrBlank()) {
            _uiState.value = AnalysisUiState.Error("Erro ao salvar a chave API. Tente novamente.")
            return
        }

        val buffer = StringBuilder()
        viewModelScope.launch {
            try {
                useCase.streamAiAnalysis(current.analysis, bankroll, kellyFraction, savedKey)
                    .collect { token ->
                        buffer.append(token)
                        _uiState.value = AnalysisUiState.Streaming(
                            analysis = current.analysis,
                            streamedText = buffer.toString(),
                        )
                    }
                _uiState.value = AnalysisUiState.Ready(
                    current.analysis.copy(aiAnalysis = buffer.toString())
                )
            } catch (e: Exception) {
                _uiState.value = AnalysisUiState.Error(e.message ?: "Erro na análise IA")
            }
        }
    }

    fun getSavedApiKey(): String? = apiKeyRepo.getApiKey()
}
