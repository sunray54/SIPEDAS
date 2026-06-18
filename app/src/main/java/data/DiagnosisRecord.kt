package com.hafizhihiman.sipedas.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "diagnosis_history")
data class DiagnosisRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imagePath: String? = null,
    val disease: String,
    val confidence: Float,          // dari CNN
    val finalCF: Float,             // certainty factor setelah inferensi
    val recommendation: String,
    val additionalFacts: String = "", // fakta pengguna (dipisah koma)
    val usedRules: String = "",      // penjelasan aturan
    val userFeedback: String? = null, // "helpful" atau "not_helpful"
    val timestamp: Long = Date().time
)