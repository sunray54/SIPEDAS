package com.hafizhihiman.sipedas.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hafizhihiman.sipedas.databinding.ActivityManualDiagnosisBinding
import com.hafizhihiman.sipedas.rule.BackwardChainingEngine
import com.hafizhihiman.sipedas.rule.RuleEngine

class ManualDiagnosisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManualDiagnosisBinding
    private lateinit var ruleEngine: RuleEngine
    private lateinit var bcEngine: BackwardChainingEngine

    private var selectedHypothesis: String? = null
    private var questions: List<Pair<String, String>> = emptyList()
    private var currentIndex = 0
    private val userFacts = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManualDiagnosisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ruleEngine = RuleEngine(this)
        bcEngine = BackwardChainingEngine(ruleEngine)

        // Isi Spinner dengan daftar penyakit
        val diseases = listOf("Antraknosa", "Bercak Daun", "Keriting Kuning", "Layu Fusarium", "Busuk Buah")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, diseases)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDisease.adapter = adapter

        binding.spinnerDisease.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedHypothesis = diseases[position]
                // Reset sesi
                currentIndex = 0
                userFacts.clear()
                binding.layoutQuestion.visibility = View.GONE
                binding.tvResult.visibility = View.GONE
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Tombol Mulai Diagnosis
        binding.btnStart.setOnClickListener {
            val hypothesis = selectedHypothesis
            if (hypothesis == null) {
                Toast.makeText(this, "Pilih penyakit yang dicurigai", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Ambil pertanyaan
            questions = bcEngine.startBackwardChaining(hypothesis)
            if (questions.isNotEmpty()) {
                currentIndex = 0
                binding.layoutQuestion.visibility = View.VISIBLE
                showQuestion()
            } else {
                Toast.makeText(this, "Tidak ada pertanyaan untuk penyakit ini", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Selanjutnya
        binding.btnNext.setOnClickListener {
            val selectedId = binding.radioGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Pilih Ya atau Tidak", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val answer = findViewById<RadioButton>(selectedId).text.toString()
            if (answer == "Ya") {
                userFacts.add(questions[currentIndex].first) // Simpan factId
            }
            currentIndex++
            if (currentIndex < questions.size) {
                showQuestion()
            } else {
                // Selesai, evaluasi
                finishDiagnosis()
            }
        }
    }

    private fun showQuestion() {
        binding.tvQuestion.text = questions[currentIndex].second
        binding.radioGroup.clearCheck()
    }

    private fun finishDiagnosis() {
        binding.layoutQuestion.visibility = View.GONE
        val hypothesis = selectedHypothesis ?: return
        val resultText = bcEngine.evaluate(hypothesis, userFacts)
        binding.tvResult.text = resultText
        binding.tvResult.visibility = View.VISIBLE
    }
}