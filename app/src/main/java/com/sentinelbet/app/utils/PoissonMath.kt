package com.sentinelbet.app.utils

import kotlin.math.exp
import kotlin.math.ln

fun poissonProbability(lambda: Double, k: Int): Double {
    if (lambda <= 0.0) return if (k == 0) 1.0 else 0.0
    var logP = -lambda + k * ln(lambda)
    for (i in 1..k) logP -= ln(i.toDouble())
    return exp(logP)
}

data class PoissonResult(
    val homeWin: Double,
    val draw: Double,
    val awayWin: Double,
    val matrix: List<List<Double>>,
)

fun simulateMatch(lambdaHome: Double, lambdaAway: Double, maxGoals: Int = 8): PoissonResult {
    var homeWin = 0.0
    var draw = 0.0
    var awayWin = 0.0
    val matrix = List(maxGoals + 1) { h ->
        List(maxGoals + 1) { a ->
            poissonProbability(lambdaHome, h) * poissonProbability(lambdaAway, a)
        }
    }
    for (h in 0..maxGoals) {
        for (a in 0..maxGoals) {
            val p = matrix[h][a]
            when {
                h > a -> homeWin += p
                h == a -> draw += p
                else -> awayWin += p
            }
        }
    }
    return PoissonResult(homeWin, draw, awayWin, matrix)
}

fun fairOdd(prob: Double): Double = if (prob > 0) 1.0 / prob else Double.MAX_VALUE

fun valueEdge(prob: Double, odd: Double): Double = (prob * odd - 1) * 100.0

fun kellyCriterion(prob: Double, odd: Double, fraction: Double = 0.25): Double {
    val b = odd - 1.0
    val q = 1.0 - prob
    val k = (b * prob - q) / b
    return if (k > 0) k * fraction else 0.0
}
