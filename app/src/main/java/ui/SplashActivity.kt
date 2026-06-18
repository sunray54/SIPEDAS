package com.hafizhihiman.sipedas.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.hafizhihiman.sipedas.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Tampilkan splash screen selama 2 detik
        Handler(Looper.getMainLooper()).postDelayed({
            // Periksa apakah pengguna sudah login
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                // Sudah login, langsung ke MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Belum login, arahkan ke LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish() // tutup splash agar tidak bisa kembali
        }, 2000)
    }
}