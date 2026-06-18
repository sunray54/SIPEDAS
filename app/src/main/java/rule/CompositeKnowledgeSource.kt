package com.hafizhhilman.sipedas.app.rule

class CompositeKnowledgeSource(
    private val local: List<RuleEngine.DiseaseRule>,
    private val cloud: List<RuleEngine.DiseaseRule>? = null,
    private val expert: List<RuleEngine.DiseaseRule>? = null
) {
    fun getAllRules(): List<RuleEngine.DiseaseRule> {
        val rules = mutableListOf<RuleEngine.DiseaseRule>()
        rules.addAll(local)
        cloud?.let { rules.addAll(it) }
        expert?.let { rules.addAll(it) }
        // Jika ada duplikasi penyakit, prioritas: expert > cloud > local
        return rules.distinctBy { it.disease }.reversed().distinctBy { it.disease }
    }
}