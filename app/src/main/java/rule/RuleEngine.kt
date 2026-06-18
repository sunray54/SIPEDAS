package com.hafizhihiman.sipedas.rule

import android.content.Context
import org.json.JSONObject

class RuleEngine(context: Context) {

    val diseaseRules = mutableListOf<DiseaseRule>()
    val questionsMapping = mutableMapOf<String, String>()
    private val ruleLearnerPrefs = context.getSharedPreferences("RuleLearner", Context.MODE_PRIVATE)

    init {
        loadKnowledgeBase(context)
    }

    // ========== LOAD KNOWLEDGE BASE ==========
    private fun loadKnowledgeBase(context: Context) {
        val jsonString = try {
            context.assets.open("knowledge_base.json").bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            return
        }
        val root = JSONObject(jsonString)
        val rulesArray = root.getJSONArray("disease_rules")
        for (i in 0 until rulesArray.length()) {
            val obj = rulesArray.getJSONObject(i)
            val reqFacts = mutableListOf<FactBoost>()
            val reqArr = obj.getJSONArray("required_facts")
            for (j in 0 until reqArr.length()) {
                val f = reqArr.getJSONObject(j)
                reqFacts.add(FactBoost(f.getString("id"), f.getDouble("cf_boost").toFloat()))
            }
            val optFacts = mutableListOf<FactBoost>()
            if (obj.has("optional_facts")) {
                val optArr = obj.getJSONArray("optional_facts")
                for (j in 0 until optArr.length()) {
                    val f = optArr.getJSONObject(j)
                    optFacts.add(FactBoost(f.getString("id"), f.getDouble("cf_boost").toFloat()))
                }
            }
            diseaseRules.add(
                DiseaseRule(
                    disease = obj.getString("disease"),
                    cnnConfidenceMin = obj.getDouble("cnn_confidence_min").toFloat(),
                    requiredFacts = reqFacts,
                    optionalFacts = optFacts,
                    recommendation = obj.getString("recommendation"),
                    cfRule = obj.getDouble("cf_rule").toFloat()
                )
            )
        }
        val qm = root.getJSONObject("questions_mapping")
        qm.keys().forEach { key ->
            questionsMapping[key] = qm.getString(key)
        }
    }

    // ========== GET QUESTIONS ==========
    fun getQuestionsForDisease(disease: String): List<Pair<String, String>> {
        val rule = diseaseRules.find { it.disease == disease } ?: return emptyList()
        val allFacts = (rule.requiredFacts + rule.optionalFacts).map { it.id }.distinct()
        return allFacts.mapNotNull { factId ->
            questionsMapping[factId]?.let { Pair(factId, it) }
        }
    }

    // ========== INFERENSI UTAMA (CF + FUZZY) ==========
    fun infer(
        initialDisease: String,
        cnnConfidence: Float,
        userFacts: List<String>,
        temp: Double? = null,
        humidity: Double? = null
    ): DiagnosisResult {
        // Salin userFacts agar bisa ditambah fakta fuzzy tanpa mengubah list asli
        val mutableFacts = userFacts.toMutableList()

        // 1. FUZZY LOGIC: Auto-tambah fakta berdasarkan data cuaca
        if (temp != null && humidity != null) {
            val derajatLembab = FuzzyLogicHelper.derajatLembab(temp, humidity)
            val derajatKering = FuzzyLogicHelper.derajatKering(temp, humidity)

            if (derajatLembab > 0.5 && !mutableFacts.contains("cuaca_lembab")) {
                mutableFacts.add("cuaca_lembab")
            }
            if (derajatKering > 0.5 && !mutableFacts.contains("cuaca_kering")) {
                mutableFacts.add("cuaca_kering")
            }
        }

        // 2. Cek confidence minimum
        if (cnnConfidence < 0.6) {
            return DiagnosisResult(
                disease = initialDisease,
                finalCF = cnnConfidence,
                recommendation = "Hasil gambar kurang meyakinkan. Silakan ambil ulang.",
                usedRules = emptyList()
            )
        }

        // 3. Cari aturan yang cocok
        val matchedRule = diseaseRules.find { it.disease == initialDisease }
            ?: return DiagnosisResult(
                disease = initialDisease,
                finalCF = cnnConfidence,
                recommendation = "Penyakit tidak dikenali dalam basis pengetahuan.",
                usedRules = emptyList()
            )

        // 4. Hitung kecocokan fakta wajib
        val requiredMatches = matchedRule.requiredFacts.count { mutableFacts.contains(it.id) }
        val totalRequired = matchedRule.requiredFacts.size

        // 5. Evaluasi
        if (requiredMatches == totalRequired) {
            // Semua fakta wajib terpenuhi → hitung boost dari semua fakta yang diiyakan
            val matchedFacts = (matchedRule.requiredFacts + matchedRule.optionalFacts)
                .filter { mutableFacts.contains(it.id) }

            var totalBoost = 0f
            val boostDetails = mutableListOf<String>()

            for (fact in matchedFacts) {
                var boost = fact.cfBoost

                // 6. FUZZY BOOST: jika fakta adalah "cuaca_lembab" atau "cuaca_kering"
                if (fact.id == "cuaca_lembab" && temp != null && humidity != null) {
                    val degree = FuzzyLogicHelper.derajatLembab(temp, humidity)
                    boost = FuzzyLogicHelper.fuzzyToCFBoost(degree, fact.cfBoost)
                } else if (fact.id == "cuaca_kering" && temp != null && humidity != null) {
                    val degree = FuzzyLogicHelper.derajatKering(temp, humidity)
                    boost = FuzzyLogicHelper.fuzzyToCFBoost(degree, fact.cfBoost)
                }

                totalBoost += boost
                boostDetails.add("${fact.id}=${"%.2f".format(boost)}")
            }

            // Membaca dari ruleLearnerPrefs (harus ditambahkan ke class RuleEngine)
            val learnerBoost = ruleLearnerPrefs.getFloat(initialDisease, 0f)

            var cfFinal = (matchedRule.cfRule + learnerBoost) * (cnnConfidence + totalBoost)
            if (cfFinal > 1f) cfFinal = 1f
            if (cfFinal < 0f) cfFinal = 0f

            val usedRules = listOf(
                "Hipotesis: $initialDisease",
                "CF_rule: ${matchedRule.cfRule}",
                "Learner_boost: ${"%.2f".format(learnerBoost)}",
                "CF_cnn: $cnnConfidence",
                "Boost fakta: [${boostDetails.joinToString()}]",
                "CF_final = (${matchedRule.cfRule} + ${"%.2f".format(learnerBoost)}) * ($cnnConfidence + $totalBoost) = ${"%.4f".format(cfFinal)}"
            )

            return DiagnosisResult(
                disease = initialDisease,
                finalCF = cfFinal,
                recommendation = matchedRule.recommendation,
                usedRules = usedRules
            )
        } else {
            // Tidak semua fakta wajib terpenuhi → kurangi CF
            val penalty = (totalRequired - requiredMatches) * 0.25f
            var cf = (cnnConfidence - penalty).coerceAtLeast(0f)

            return DiagnosisResult(
                disease = initialDisease,
                finalCF = cf,
                recommendation = "Beberapa gejala tidak cocok. Kemungkinan $initialDisease rendah. Konsultasi penyuluh.",
                usedRules = listOf(
                    "Hipotesis: $initialDisease",
                    "CF_cnn: $cnnConfidence",
                    "Fakta wajib tidak lengkap: butuh ${matchedRule.requiredFacts.map { it.id }}",
                    "Penalti: -$penalty"
                )
            )
        }
    }

    // ========== DATA CLASSES ==========
    data class DiseaseRule(
        val disease: String,
        val cnnConfidenceMin: Float,
        val requiredFacts: List<FactBoost>,
        val optionalFacts: List<FactBoost>,
        val recommendation: String,
        val cfRule: Float
    )

    data class FactBoost(val id: String, val cfBoost: Float)

    data class DiagnosisResult(
        val disease: String,
        val finalCF: Float,
        val recommendation: String,
        val usedRules: List<String>
    )
}