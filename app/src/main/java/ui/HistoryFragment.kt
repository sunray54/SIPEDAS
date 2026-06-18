package com.hafizhihiman.sipedas.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hafizhihiman.sipedas.data.AppDatabase
import com.hafizhihiman.sipedas.databinding.FragmentHistoryBinding
import com.hafizhihiman.sipedas.ui.adapter.HistoryAdapter
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HistoryAdapter { record ->
            // klik item untuk detail (opsional)
        }
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

        lifecycleScope.launch {
            AppDatabase.getInstance(requireContext())
                .diagnosisDao()
                .getAllRecords()
                .collect { records ->
                    if (records.isEmpty()) {
                        binding.layoutEmptyState.visibility = View.VISIBLE
                        binding.rvHistory.visibility = View.GONE
                    } else {
                        binding.layoutEmptyState.visibility = View.GONE
                        binding.rvHistory.visibility = View.VISIBLE
                        adapter.submitList(records)
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}