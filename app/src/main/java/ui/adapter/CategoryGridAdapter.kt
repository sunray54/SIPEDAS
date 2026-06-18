package com.hafizhihiman.sipedas.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hafizhihiman.sipedas.databinding.ItemCategoryGridBinding
import com.hafizhihiman.sipedas.data.CategoryItem

class CategoryGridAdapter(
    private val items: List<CategoryItem>,
    private val onItemClick: (CategoryItem) -> Unit
) : RecyclerView.Adapter<CategoryGridAdapter.Vh>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        val binding = ItemCategoryGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Vh(binding)
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class Vh(private val binding: ItemCategoryGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryItem) {
            binding.ivIcon.setImageResource(item.iconResId)
            binding.tvName.text = item.nama
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }
}