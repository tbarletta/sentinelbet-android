package com.sentinelbet.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelbet.app.utils.kellyCriterion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class KellyResult(
    val stake: Double,
    val kellyPct: Double,
    val hasValue: Boolean,
)

data class BankrollUiState(
    val bankrollInput: String = "1000",
    val oddInput: String = "2.00",
    val probInput: String = "55",
    val kellyFractionInput: String = "25",
    val result: KellyResult? = null,
)

@HiltViewModel
class BankrollViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(BankrollUiState())
    val uiState: StateFlow<BankrollUiState> = _uiState.asStateFlow()

    fun onBankrollChange(v: String) { _uiState.update { it.copy(bankrollInput = v) }; compute() }
    fun onOddChange(v: String)      { _uiState.update { it.copy(oddInput = v) }; compute() }
    fun onProbChange(v: String)     { _uiState.update { it.copy(probInput = v) }; compute() }
    fun onFractionChange(v: String) { _uiState.update { it.copy(kellyFractionInput = v) }; compute() }

    private fun compute() {
        val state = _uiState.value
        val bankroll = state.bankrollInput.toDoubleOrNull() ?: return
        val odd      = state.oddInput.toDoubleOrNull() ?: return
        val prob     = (state.probInput.toDoubleOrNull() ?: return) / 100.0
        val fraction = (state.kellyFractionInput.toDoubleOrNull() ?: return) / 100.0

        val kellyPct = kellyCriterion(prob, odd, fraction)
        val stake    = bankroll * kellyPct
        _uiState.update {
            it.copy(result = KellyResult(stake = stake, kellyPct = kellyPct * 100, hasValue = stake > 0))
        }
    }
}
