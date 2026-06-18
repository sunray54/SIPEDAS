package com.hafizhihiman.sipedas.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hafizhihiman.sipedas.databinding.ActivityDetailReasoningBinding

class DetailReasoningActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailReasoningBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailReasoningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val disease = intent.getStringExtra("DISEASE") ?: "Tidak diketahui"
        val cf = intent.getFloatExtra("FINAL_CF", 0f)
        val rules = intent.getStringExtra("USED_RULES") ?: "-"

        binding.tvDisease.text = intent.getStringExtra("DISEASE")
        binding.tvCF.text = "Certainty Factor: ${"%.2f".format(intent.getFloatExtra("FINAL_CF", 0f))}"
        binding.tvRules.text = intent.getStringExtra("USED_RULES")?.replace(";", "\n")
    }
}