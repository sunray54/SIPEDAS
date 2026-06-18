package ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sipedas.app.data.CommunityPost
import com.sipedas.app.databinding.ItemCommunityPostBinding

class CommunityAdapter : RecyclerView.Adapter<CommunityAdapter.ViewHolder>() {
    private var posts: List<CommunityPost> = emptyList()

    fun submitList(list: List<CommunityPost>) {
        posts = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommunityPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size

    class ViewHolder(private val binding: ItemCommunityPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: CommunityPost) {
            binding.tvUserName.text = post.userName
            binding.tvContent.text = post.content
        }
    }
}