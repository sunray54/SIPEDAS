package com.hafizhihiman.sipedas.ui.research

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hafizhihiman.sipedas.databinding.ActivityResearchBinding

class ResearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Binding implementation for research components
        binding.cardFertilizerCalc.setOnClickListener {
            // Open Calculator
        }
    }
}
