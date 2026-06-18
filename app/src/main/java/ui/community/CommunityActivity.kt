package com.hafizhihiman.sipedas.ui.community

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hafizhihiman.sipedas.databinding.ActivityCommunityBinding

class CommunityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Setup community UI data
    }
}
