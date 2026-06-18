package com.hafizhhilman.sipedas.app.rule

class BackwardChainingEngine(private val ruleEngine: RuleEngine) {

    data class QuestionState(
        val disease: String,
        val question: Pair<String, String>?, // factId, text
        val totalQuestions: Int,
        val currentIndex: Int,
        val message: String
    )

    /**
     * Mulai sesi backward chaining.
     * @param hypothesis penyakit yang dicurigai
     * @return daftar pertanyaan yang harus diajukan
     */
    fun startBackwardChaining(hypothesis: String): List<Pair<String, String>> {
        val allQuestions = ruleEngine.getQuestionsForDisease(hypothesis)
        if (allQuestions.isEmpty()) {
            throw IllegalArgumentException("Penyakit $hypothesis tidak ditemukan di basis pengetahuan")
        }
        return allQuestions // Kembalikan daftar (factId, pertanyaan)
    }

    /**
     * Evaluasi hasil backward chaining.
     */
    fun evaluate(hypothesis: String, userFacts: List<String>): String {
        // Gunakan inferensi CF dari RuleEngine (cukup dengan CNN confidence default, atau 0.5)
        val result = ruleEngine.infer(hypothesis, 0.5f, userFacts)
        return if (result.finalCF >= 0.6) {
            "TERBUKTI: $hypothesis (CF: ${"%.2f".format(result.finalCF)})"
        } else {
            "TIDAK TERBUKTI: $hypothesis (CF: ${"%.2f".format(result.finalCF)}). Kemungkinan penyakit lain."
        }
    }
}