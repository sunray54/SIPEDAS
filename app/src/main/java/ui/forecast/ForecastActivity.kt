package com.hafizhihiman.sipedas.ui.forecast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hafizhihiman.sipedas.databinding.ActivityForecastBinding

class ForecastActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForecastBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Setup forecast UI data
    }
}
