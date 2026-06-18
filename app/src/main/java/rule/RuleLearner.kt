package com.hafizhhilman.sipedas.app.rule

import com.hafizhihiman.sipedas.data.DiagnosisRecord
import kotlin.math.abs

object RuleLearner {

    fun updateCF(
        currentRule: RuleEngine.DiseaseRule,
        records: List<DiagnosisRecord>
    ): Float {
        val relevant = records.filter { it.disease == currentRule.disease && it.userFeedback != null }
        if (relevant.isEmpty()) return currentRule.cfRule

        val helpfulCount = relevant.count { it.userFeedback == "helpful" }
        val total = relevant.size
        val successRate = helpfulCount.toFloat() / total

        // Formula sederhana: CF baru = (CF lama + successRate) / 2
        val newCF = (currentRule.cfRule + successRate) / 2f
        // Jangan terlalu ekstrem
        return newCF.coerceIn(0.1f, 0.99f)
    }
}