package com.hafizhhilman.sipedas.app.rule

import com.hafizhihiman.sipedas.data.DiagnosisRecord

class CaseBasedReasoner(private val allRecords: List<DiagnosisRecord>) {

    data class SimilarCase(
        val record: DiagnosisRecord,
        val similarity: Double
    )

    /**
     * Mencari kasus paling mirip berdasarkan penyakit yang sama.
     * @param disease penyakit yang dicari kesamaannya
     * @param currentFacts fakta yang diiyakan pengguna
     * @param topN jumlah kasus teratas yang dikembalikan
     * @param minSimilarity threshold minimal kemiripan (0..1)
     */
    fun findSimilarCases(
        disease: String? = null,
        currentFacts: List<String>,
        topN: Int = 3,
        minSimilarity: Double = 0.3
    ): List<SimilarCase> {
        val candidates = if (disease != null) {
            allRecords.filter { it.disease == disease }
        } else {
            allRecords
        }

        val currentSet = currentFacts.toSet()

        return candidates
            .map { record ->
                val recordSet = record.additionalFacts.split(",").map { it.trim() }.toSet()
                val intersection = currentSet.intersect(recordSet).size
                val union = currentSet.union(recordSet).size
                val sim = if (union == 0) 0.0 else intersection.toDouble() / union
                SimilarCase(record, sim)
            }
            .filter { it.similarity >= minSimilarity }
            .sortedByDescending { it.similarity }
            .take(topN)
    }

    /**
     * Mendapatkan rekomendasi dari kasus paling mirip.
     * @return rekomendasi jika ada yang di atas threshold, null jika tidak ada.
     */
    fun getRecommendation(
        disease: String,
        currentFacts: List<String>,
        threshold: Double = 0.7
    ): String? {
        val similar = findSimilarCases(disease, currentFacts, topN = 1, minSimilarity = threshold)
        return similar.firstOrNull()?.record?.recommendation
    }
}