package network

import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponse(
    val main: Main?,
    val weather: List<Weather>?,
    val name: String?
)

data class Main(
    val temp: Double?,
    val humidity: Int?,
    val pressure: Int?
)

data class Weather(
    val main: String?,
    val description: String?
)

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    companion object {
        fun create(): WeatherService {
            return RetrofitClient.instance.create(WeatherService::class.java)
        }
    }
}