package com.hafizhihiman.sipedas.ui.forecast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hafizhihiman.sipedas.databinding.ActivityForecastBinding
import com.hafizhihiman.sipedas.forecasting.ForecastEngine
import kotlinx.coroutines.*

class ForecastActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForecastBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Data dummy harga cabai 7 hari terakhir
        val recentPrices = listOf(45000.0, 46000.0, 45500.0, 47000.0, 48000.0, 47500.0, 49000.0)
        val forecast = ForecastEngine.arima(recentPrices, 7)

        val builder = StringBuilder()
        forecast.forEach { (day, price) ->
            builder.append("Hari ke-$day: Rp ${"%.0f".format(price)}\n")
        }
        binding.tvForecast.text = builder.toString()
    }
}