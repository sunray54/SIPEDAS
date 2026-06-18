package com.sipedas.app.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("diagnosis_history")

    suspend fun saveRecord(record: DiagnosisRecord) {
        collection.add(record).await()
    }

    suspend fun getRecords(): List<DiagnosisRecord> {
        return collection.get().await().documents.mapNotNull {
            it.toObject(DiagnosisRecord::class.java)
        }
    }
}