package ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sipedas.app.data.DiagnosisRecord
import com.sipedas.app.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private val onItemClick: (DiagnosisRecord) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private var records: List<DiagnosisRecord> = emptyList()

    fun submitList(list: List<DiagnosisRecord>) {
        records = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(records[position])
    }

    override fun getItemCount() = records.size

    inner class ViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(record: DiagnosisRecord) {
            binding.tvDisease.text = record.disease
            binding.tvConfidence.text = "CF: ${"%.2f".format(record.finalCF)}"
            binding.tvDate.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(Date(record.timestamp))

            binding.root.setOnClickListener {
                onItemClick(record)
            }
        }
    }
}
