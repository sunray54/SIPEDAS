package ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sipedas.app.data.AppDatabase
import com.sipedas.app.data.DiagnosisRecord
import com.sipedas.app.databinding.ActivityHistoryBinding
import com.sipedas.app.ui.adapter.HistoryAdapter
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = HistoryAdapter { record ->
            // Klik item -> buka DetailReasoningActivity
            val intent = Intent(this, DetailReasoningActivity::class.java).apply {
                putExtra("DISEASE", record.disease)
                putExtra("FINAL_CF", record.finalCF)
                putExtra("USED_RULES", record.usedRules)
            }
            startActivity(intent)
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter

        lifecycleScope.launch {
            AppDatabase.getInstance(this@HistoryActivity)
                .diagnosisDao()
                .getAllRecords()
                .collect { records ->
                    adapter.submitList(records)
                }
        }
    }
}