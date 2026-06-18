package com.hafizhhilman.sipedas.app.rule

object NLPFactExtractor {
    private val keywordMap = mapOf(
        "lingkaran" to "lingkaran_konsentris",
        "konsentris" to "lingkaran_konsentris",
        "kuning" to "tepi_kuning",
        "keriting" to "daun_mengeriting",
        "layu" to "layu_mendadak",
        "kutu" to "kutu_kebul",
        "bercak" to "bercak_kecil",
        "basah" to "bercak_basah",
        "busuk" to "buah_busuk"
    )

    fun extractFacts(input: String): List<String> {
        val facts = mutableListOf<String>()
        val lower = input.lowercase()
        for ((keyword, factId) in keywordMap) {
            if (lower.contains(keyword)) {
                facts.add(factId)
            }
        }
        return facts.distinct()
    }
}