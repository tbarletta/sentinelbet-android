package com.sentinelbet.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelbet.app.data.repository.ApiFootballRepository
import com.sentinelbet.app.data.repository.LiveFixture
import com.sentinelbet.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FixturesUiState {
    data object Idle    : FixturesUiState()
    data object Loading : FixturesUiState()
    data class  Loaded(val fixtures: List<LiveFixture>, val isLive: Boolean) : FixturesUiState()
    data class  Error(val message: String) : FixturesUiState()
    data object NoApiKey : FixturesUiState()
}

@HiltViewModel
class FixturesViewModel @Inject constructor(
    private val repo: ApiFootballRepository,
    private val settingsRepo: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<FixturesUiState>(FixturesUiState.Idle)
    val uiState: StateFlow<FixturesUiState> = _uiState.asStateFlow()

    private val _apiKey = MutableStateFlow<String>("")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    init {
        viewModelScope.launch {
            val key = settingsRepo.getApiFootballKey() ?: ""
            _apiKey.value = key
        }
    }

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            settingsRepo.setApiFootballKey(key)
            _apiKey.value = key
        }
    }

    fun loadLiveFixtures() {
        viewModelScope.launch {
            _uiState.value = FixturesUiState.Loading
            val key = settingsRepo.getApiFootballKey()
            if (key.isNullOrBlank()) {
                _uiState.value = FixturesUiState.NoApiKey
                return@launch
            }
            repo.getLiveFixturesWithOdds()
                .onSuccess { fixtures ->
                    _uiState.value = FixturesUiState.Loaded(fixtures, isLive = true)
                }
                .onFailure { e ->
                    _uiState.value = FixturesUiState.Error(e.message ?: "Erro desconhecido")
                }
        }
    }

    fun loadTodayFixtures(leagueName: String) {
        val leagueId = ApiFootballRepository.LEAGUE_IDS[leagueName] ?: run {
            _uiState.value = FixturesUiState.Error("Liga não suportada: $leagueName")
            return
        }
        viewModelScope.launch {
            _uiState.value = FixturesUiState.Loading
            val key = settingsRepo.getApiFootballKey()
            if (key.isNullOrBlank()) {
                _uiState.value = FixturesUiState.NoApiKey
                return@launch
            }
            repo.getTodayFixtures(leagueId)
                .onSuccess { fixtures ->
                    _uiState.value = FixturesUiState.Loaded(fixtures, isLive = false)
                }
                .onFailure { e ->
                    _uiState.value = FixturesUiState.Error(e.message ?: "Erro desconhecido")
                }
        }
    }

    fun refresh() {
        when (val s = _uiState.value) {
            is FixturesUiState.Loaded -> if (s.isLive) loadLiveFixtures()
            else -> loadLiveFixtures()
        }
    }
}
