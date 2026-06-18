package com.hafizhihiman.sipedas.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hafizhihiman.sipedas.R
import com.hafizhihiman.sipedas.databinding.ActivityMainContainerBinding

class MainContainerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainContainerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        binding.bottomNavigation.setupWithNavController(navController)
    }
}
