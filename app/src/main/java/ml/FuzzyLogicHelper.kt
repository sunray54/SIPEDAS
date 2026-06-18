package com.hafizhhilman.sipedas.app.ml

object FuzzyLogicHelper {

    // ======= FUNGSI KEANGGOTAAN SUHU (°C) =======
    private fun suhuRendah(x: Double): Double = when {
        x <= 20 -> 1.0
        x in 20.0..25.0 -> (25 - x) / 5.0
        else -> 0.0
    }

    private fun suhuNormal(x: Double): Double = when {
        x < 20 -> 0.0
        x in 20.0..25.0 -> (x - 20) / 5.0
        x in 25.0..30.0 -> 1.0
        x in 30.0..32.0 -> (32 - x) / 2.0
        else -> 0.0
    }

    private fun suhuTinggi(x: Double): Double = when {
        x <= 30 -> 0.0
        x in 30.0..32.0 -> (x - 30) / 2.0
        else -> 1.0
    }

    // ======= FUNGSI KEANGGOTAAN KELEMBABAN (%) =======
    private fun humKering(x: Double): Double = when {
        x <= 40 -> 1.0
        x in 40.0..60.0 -> (60 - x) / 20.0
        else -> 0.0
    }

    private fun humNormal(x: Double): Double = when {
        x < 40 -> 0.0
        x in 40.0..60.0 -> (x - 40) / 20.0
        x in 60.0..80.0 -> 1.0
        x in 80.0..90.0 -> (90 - x) / 10.0
        else -> 0.0
    }

    private fun humLembab(x: Double): Double = when {
        x <= 80 -> 0.0
        x in 80.0..90.0 -> (x - 80) / 10.0
        else -> 1.0
    }

    // ======= ATURAN FUZZY =======
    /**
     * Menghitung derajat “cuaca lembab” (kondisi ideal untuk jamur)
     * IF suhu TINGGI AND kelembaban LEMBAB THEN cuaca_lembab
     * Operator AND = min
     */
    fun derajatLembab(temp: Double, hum: Double): Double {
        val uTinggi = suhuTinggi(temp)
        val hLembab = humLembab(hum)
        return minOf(uTinggi, hLembab)
    }

    /**
     * Menghitung derajat “cuaca kering” (kondisi kurang air)
     * IF suhu TINGGI AND kelembaban KERING THEN cuaca_kering
     */
    fun derajatKering(temp: Double, hum: Double): Double {
        val uTinggi = suhuTinggi(temp)
        val hKering = humKering(hum)
        return minOf(uTinggi, hKering)
    }

    // ======= FUZZY UNTUK FAKTA GEJALA TAMBAHAN =======
    /**
     * Konversi nilai fuzzy menjadi boost CF untuk sebuah fakta.
     * @param degree derajat keanggotaan (0..1)
     * @param maxBoost boost maksimum yang bisa diberikan
     * @return boost CF aktual = degree * maxBoost
     */
    fun fuzzyToCFBoost(degree: Double, maxBoost: Float): Float {
        return (degree * maxBoost).toFloat()
    }
}