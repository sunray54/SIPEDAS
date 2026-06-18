package network

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class UploadResponse(
    val success: Boolean,
    val message: String?,
    val image_url: String? // URL gambar yang tersimpan
)

interface UploadService {
    @Multipart
    @POST("upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): UploadResponse

    companion object {
        fun create(): UploadService {
            return RetrofitClient.instance.create(UploadService::class.java)
        }
    }
}