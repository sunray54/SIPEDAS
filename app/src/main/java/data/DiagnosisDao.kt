package com.hafizhihiman.sipedas.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosisDao {
    @Query("SELECT * FROM diagnosis_history ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<DiagnosisRecord>>

    @Query("SELECT * FROM diagnosis_history WHERE disease = :disease AND additionalFacts = :facts AND userFeedback = 'helpful' ORDER BY timestamp DESC LIMIT 1")
    suspend fun getSimilarHelpfulCase(disease: String, facts: String): DiagnosisRecord?

    @Insert
    suspend fun insert(record: DiagnosisRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<DiagnosisRecord>)

    @Query("SELECT COUNT(*) FROM diagnosis_history")
    suspend fun getCount(): Int

    @Update
    suspend fun update(record: DiagnosisRecord)

    @Query("SELECT * FROM diagnosis_history WHERE id = :id LIMIT 1")
    suspend fun getRecordById(id: Long): DiagnosisRecord?

    @Delete
    suspend fun delete(record: DiagnosisRecord)
}