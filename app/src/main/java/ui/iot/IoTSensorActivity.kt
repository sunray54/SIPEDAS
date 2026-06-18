package com.hafizhihiman.sipedas.ui.iot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hafizhihiman.sipedas.databinding.ActivityIotSensorBinding

class IoTSensorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIotSensorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIotSensorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Logic for fetching actual IoT data can go here
        simulateIoTData()
    }

    private fun simulateIoTData() {
        androidx.lifecycle.lifecycleScope.launchWhenStarted {
            while (true) {
                val temp = kotlin.random.Random.nextInt(25, 35)
                val hum = kotlin.random.Random.nextInt(60, 85)
                val soil = kotlin.random.Random.nextInt(40, 70)
                val ph = String.format(java.util.Locale.US, "%.1f", kotlin.random.Random.nextDouble(6.0, 7.2))
                binding.tvTemp.text = "$temp°C"
                binding.tvHumidity.text = "$hum%"
                binding.tvSoilMoisture.text = "$soil%"
                binding.tvPh.text = ph
                kotlinx.coroutines.delay(2000)
            }
        }
    }
}
