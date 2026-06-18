package network

import retrofit2.http.Body
import retrofit2.http.POST

// Request
data class LlamaRequest(
    val prompt: String,
    val history: List<List<String>>? = null, // array of [user, assistant]
    val max_tokens: Int = 512,
    val temperature: Double = 0.7
)

// Response (sesuaikan dengan format server Anda)
data class LlamaResponse(
    val response: String?,
    val error: String?
)

interface LlamaService {
    @POST("api/generate") // endpoint misal /api/generate
    suspend fun generateText(@Body request: LlamaRequest): LlamaResponse

    companion object {
        fun create(): LlamaService {
            // Jika URL berbeda dari BASE_URL, Anda bisa buat Retrofit instance khusus
            return RetrofitClient.instance.create(LlamaService::class.java)
        }
    }

    // Di dalam LlamaService companion object alternatif:
    fun create(baseUrl: String = "http://10.0.2.2:8000/"): LlamaService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(LlamaService::class.java)
    }
}