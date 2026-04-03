package com.sentinelbet.app.domain.entities

enum class ValueBetLevel { HIGH, MEDIUM, LOW }

data class ValueBet(
    val outcome: String,
    val bookmaker: String,
    val aiProbability: Double,
    val marketOdd: Double,
    val fairOdd: Double,
    val edge: Double,
    val kellyPercent: Double,
    val level: ValueBetLevel,
)

data class MatchAnalysis(
    val homeTeam: Team,
    val awayTeam: Team,
    val lambdaHome: Double,
    val lambdaAway: Double,
    val probHome: Double,
    val probDraw: Double,
    val probAway: Double,
    val fairOddHome: Double,
    val fairOddDraw: Double,
    val fairOddAway: Double,
    val poissonMatrix: List<List<Double>>,
    val valueBets: List<ValueBet>,
    val marketOdds: Map<String, Map<String, Double>>,
    val aiAnalysis: String? = null,
)
