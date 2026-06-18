package ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.sipedas.app.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = FirebaseAuth.getInstance().currentUser
        binding.tvEmail.text = "Email: ${user?.email ?: "Tidak tersedia"}"
        binding.tvName.text = "Nama: ${user?.displayName ?: "Belum diatur"}"

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }
}