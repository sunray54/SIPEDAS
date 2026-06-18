package ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sipedas.app.databinding.ActivityChatPakarBinding
import com.sipedas.app.network.LlamaService
import com.sipedas.app.network.LlamaRequest
import com.sipedas.app.ui.adapter.ChatAdapter
import com.sipedas.app.ui.adapter.ChatMessage
import kotlinx.coroutines.launch

class ChatPakarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatPakarBinding
    private val chatAdapter = ChatAdapter()
    private val llamaService = LlamaService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatPakarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvChat.layoutManager = LinearLayoutManager(this)
        binding.rvChat.adapter = chatAdapter

        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                chatAdapter.addMessage(ChatMessage(message, isUser = true))
                binding.etMessage.text.clear()
                // Kirim ke LLM
                lifecycleScope.launch {
                    val request = LlamaRequest(prompt = message)
                    val response = llamaService.generateText(request)
                    if (response.response != null) {
                        chatAdapter.addMessage(ChatMessage(response.response, isUser = false))
                        binding.rvChat.scrollToPosition(chatAdapter.itemCount - 1)
                    } else {
                        Toast.makeText(this@ChatPakarActivity, "Gagal memuat jawaban", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}