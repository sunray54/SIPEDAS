package com.hafizhihiman.sipedas.forecasting

import kotlin.math.*

object ForecastEngine {
    fun arima(data: List<Double>, steps: Int): List<Pair<Int, Double>> {
        val lastValue = data.last()
        return (1..steps).map { i ->
            // Simulasi ARIMA(1,1,1) sederhana: nilai terakhir + noise
            val predicted = lastValue + (Math.random() - 0.5) * 2000
            Pair(i, predicted)
        }
    }
}