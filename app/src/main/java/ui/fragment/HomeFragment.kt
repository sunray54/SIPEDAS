package com.hafizhihiman.sipedas.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.hafizhihiman.sipedas.R
import com.hafizhihiman.sipedas.data.AppDatabase
import com.hafizhihiman.sipedas.databinding.FragmentHomeBinding
import com.hafizhihiman.sipedas.ui.adapter.CategoryGridAdapter
import com.hafizhihiman.sipedas.data.CategoryItem
import com.hafizhihiman.sipedas.ui.forecast.ForecastActivity
import com.hafizhihiman.sipedas.ui.iot.IoTSensorActivity
import com.hafizhihiman.sipedas.ui.vr.VRActivity
import com.hafizhihiman.sipedas.ui.research.ResearchActivity
import com.hafizhihiman.sipedas.ui.community.CommunityActivity
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // === HEADER PROFILE ===
        val user = FirebaseAuth.getInstance().currentUser
        binding.tvUserName.text = user?.displayName ?: "Petani SIPEDAS"
        binding.tvUserJob.text = "Petani Cabai"
        // Foto profil bisa diset nanti dengan Glide

        // === KARTU STATISTIK ===
        binding.tvTotalProduksi.text = "1,250 kg"
        binding.tvLuasLahan.text = "2.5 Ha"

        // === GRAFIK PRODUKSI ===
        setupChart()

        binding.btnChart7Days.setOnClickListener { refreshChart(7) }
        binding.btnChart30Days.setOnClickListener { refreshChart(30) }
        binding.btnChart6Months.setOnClickListener { refreshChart(180) }
        binding.btnChart1Year.setOnClickListener { refreshChart(365) }

        // === GRID 12 KATEGORI LAYANAN ===
        val categories = listOf(
            CategoryItem("Cuaca", R.drawable.ic_temp_placeholder),
            CategoryItem("Produk", R.drawable.ic_temp_placeholder),
            CategoryItem("Lahan", R.drawable.ic_temp_placeholder),
            CategoryItem("Budidaya", R.drawable.ic_temp_placeholder),
            CategoryItem("Trader", R.drawable.ic_temp_placeholder),
            CategoryItem("Hama", R.drawable.ic_temp_placeholder),
            CategoryItem("Toko", R.drawable.ic_temp_placeholder),
            CategoryItem("Kantong", R.drawable.ic_temp_placeholder),
            CategoryItem("VR", R.drawable.ic_temp_placeholder),
            CategoryItem("Finance", R.drawable.ic_temp_placeholder),
            CategoryItem("Kalkulator", R.drawable.ic_temp_placeholder),
            CategoryItem("Perdagangan", R.drawable.ic_temp_placeholder)
        )

        val adapter = CategoryGridAdapter(categories) { item ->
            when (item.nama) {
                "VR" -> startActivity(Intent(requireContext(), VRActivity::class.java))
                "Kalkulator" -> startActivity(Intent(requireContext(), ResearchActivity::class.java))
                "Cuaca" -> startActivity(Intent(requireContext(), ForecastActivity::class.java))
                "Hama" -> startActivity(Intent(requireContext(), IoTSensorActivity::class.java))
                "Produk" -> findNavController().navigate(R.id.nav_scan)
                "Toko" -> {
                    // Bisa diarahkan ke activity Maps/Toko terdekat
                }
                else -> { /* placeholder */ }
            }
        }

        binding.rvCategories.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvCategories.adapter = adapter
    }

    private fun setupChart() {
        val entries = listOf(
            Entry(0f, 200f),
            Entry(1f, 300f),
            Entry(2f, 250f),
            Entry(3f, 400f),
            Entry(4f, 350f),
            Entry(5f, 500f)
        )
        val dataSet = LineDataSet(entries, "Produksi (kg)").apply {
            color = resources.getColor(android.R.color.holo_green_dark)
            valueTextColor = resources.getColor(android.R.color.black)
        }
        binding.lineChart.data = LineData(dataSet)
        binding.lineChart.invalidate()
    }

    private fun refreshChart(days: Int) {
        // Nanti bisa diisi data dinamis sesuai filter
        setupChart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}