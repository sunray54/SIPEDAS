package com.hafizhihiman.sipedas.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hafizhihiman.sipedas.databinding.ActivitySymptomCheckBinding
import com.hafizhihiman.sipedas.rule.RuleEngine

class SymptomCheckActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySymptomCheckBinding
    private lateinit var ruleEngine: RuleEngine
    private var predictedDisease: String = ""
    private var confidence: Float = 0f
    private val questions = mutableListOf<Pair<String, String>>() // (factId, questionText)
    private var currentIndex = 0
    private val userFacts = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySymptomCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ruleEngine = RuleEngine(this)

        predictedDisease = intent.getStringExtra("DISEASE") ?: "Tidak Diketahui"
        confidence = intent.getFloatExtra("CONFIDENCE", 0f)

        // Ambil pertanyaan dari RuleEngine
        questions.addAll(ruleEngine.getQuestionsForDisease(predictedDisease))
        if (questions.isEmpty()) {
            // Tidak ada pertanyaan, langsung selesaikan
            finishWithResult()
        } else {
            showQuestion()
        }

        binding.btnNext.setOnClickListener {
            nextQuestion()
        }
    }

    private fun showQuestion() {
        if (currentIndex < questions.size) {
            binding.tvQuestion.text = questions[currentIndex].second
            binding.radioGroup.clearCheck()
            binding.radioGroup.visibility = View.VISIBLE
            binding.btnNext.visibility = View.VISIBLE
        } else {
            finishWithResult()
        }
    }

    private fun nextQuestion() {
        val selectedId = binding.radioGroup.checkedRadioButtonId
        if (selectedId == -1) {
            Toast.makeText(this, "Pilih Ya atau Tidak", Toast.LENGTH_SHORT).show()
            return
        }
        val radioButton = findViewById<RadioButton>(selectedId)
        val answer = radioButton.text.toString()
        if (answer == "Ya") {
            userFacts.add(questions[currentIndex].first) // simpan factId
        }
        currentIndex++
        showQuestion()
    }

    private fun finishWithResult() {
        // Gunakan inferensi Certainty Factor
        val result = ruleEngine.infer(predictedDisease, confidence, userFacts)

        val intent = Intent().apply {
            putExtra("DISEASE", result.disease)
            putExtra("CONFIDENCE", confidence) // CNN confidence asli
            putExtra("FINAL_RECOMMENDATION", result.recommendation)
            putExtra("FACTS", userFacts.joinToString(","))
            putExtra("FINAL_CF", result.finalCF)
            putExtra("USED_RULES", result.usedRules.joinToString(";"))
        }
        setResult(RESULT_OK, intent)
        finish()
    }
}