package com.hafizhihiman.sipedas

import android.app.Application
import com.sipedas.app.util.NotificationHelper
import com.google.firebase.FirebaseApp

class SipedasApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inisialisasi saluran notifikasi
        NotificationHelper.createChannel(this)

        FirebaseApp.initializeApp(this)

        // Pre-populasi data dummy riwayat diagnosis jika kosong
        val db = com.hafizhihiman.sipedas.data.AppDatabase.getInstance(this)
        @optics.Optics
        @Suppress("OPT_IN_USAGE")
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val dao = db.diagnosisDao()
            if (dao.getCount() == 0) {
                val dummyRecords = listOf(
                    com.hafizhihiman.sipedas.data.DiagnosisRecord(
                        disease = "Antraknosa",
                        confidence = 0.88f,
                        finalCF = 0.90f,
                        recommendation = "Semprot fungisida berbahan aktif mankozeb atau propineb. Kurangi kelembaban dengan pemangkasan daun bawah. Buang buah yang terinfeksi.\n\n[CBR - Kasus Serupa Ditemukan]: Kasus ini sangat mirip dengan riwayat diagnosis Anda sebelumnya yang berhasil ditangani. Confidence CF ditingkatkan.",
                        additionalFacts = "bercak_coklat_hitam,lingkaran_konsentris,cuaca_lembab",
                        usedRules = "R1_Antraknosa;CBR_Boost: +0.05",
                        userFeedback = "helpful",
                        timestamp = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000
                    ),
                    com.hafizhihiman.sipedas.data.DiagnosisRecord(
                        disease = "Bercak Daun",
                        confidence = 0.82f,
                        finalCF = 0.85f,
                        recommendation = "Semprot fungisida klorotalonil atau difenokonazol. Perbaiki drainase lahan. Jaga jarak tanam agar sirkulasi udara baik.",
                        additionalFacts = "bercak_kecil,tepi_kuning",
                        usedRules = "R2_BercakDaun",
                        userFeedback = "helpful",
                        timestamp = System.currentTimeMillis() - 24 * 60 * 60 * 1000
                    ),
                    com.hafizhihiman.sipedas.data.DiagnosisRecord(
                        disease = "Keriting Kuning",
                        confidence = 0.79f,
                        finalCF = 0.84f,
                        recommendation = "Cabut dan musnahkan tanaman terinfeksi. Kendalikan kutu kebul dengan insektisida imidakloprid. Gunakan mulsa plastik perak untuk mengusir serangga.",
                        additionalFacts = "daun_mengeriting,warna_kuning_cerah,kutu_kebul",
                        usedRules = "R3_KeritingKuning",
                        userFeedback = "helpful",
                        timestamp = System.currentTimeMillis() - 5 * 60 * 60 * 1000
                    )
                )
                dao.insertAll(dummyRecords)
            }
        }
    }
}