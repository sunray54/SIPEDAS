package ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sipedas.app.databinding.ItemChatBinding

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()

    fun submitList(list: List<ChatMessage>) {
        messages.clear()
        messages.addAll(list)
        notifyDataSetChanged()
    }

    fun addMessage(msg: ChatMessage) {
        messages.add(msg)
        notifyItemInserted(messages.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount() = messages.size

    inner class ViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            binding.tvMessage.text = message.content
            // Atur alignment sesuai user atau bot
            if (message.isUser) {
                binding.tvMessage.textAlignment = android.view.View.TEXT_ALIGNMENT_TEXT_END
            } else {
                binding.tvMessage.textAlignment = android.view.View.TEXT_ALIGNMENT_TEXT_START
            }
        }
    }
}