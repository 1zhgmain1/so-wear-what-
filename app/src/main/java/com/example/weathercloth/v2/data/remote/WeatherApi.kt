package com.example.weathercloth.v2.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun forecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,cloud_cover,weather_code,wind_speed_10m,wind_gusts_10m,uv_index",
        @Query("hourly") hourly: String = "temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,precipitation_probability,cloud_cover,weather_code,wind_speed_10m,wind_gusts_10m,uv_index",
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min",
        @Query("timezone") timezone: String = "auto",
        @Query("wind_speed_unit") windSpeedUnit: String = "ms",
        @Query("forecast_days") forecastDays: Int = 2
    ): ForecastResponse

    @GET("https://air-quality-api.open-meteo.com/v1/air-quality")
    suspend fun airQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "us_aqi",
        @Query("timezone") timezone: String = "auto"
    ): AirQualityResponse

    @GET("https://geocoding-api.open-meteo.com/v1/search")
    suspend fun geocode(
        @Query("name") query: String,
        @Query("count") count: Int = 5,
        @Query("language") language: String = "zh",
        @Query("format") format: String = "json"
    ): GeocodeResponse

    @GET("https://openapi.weathercn.com/locations/v1/cities/geoposition/search.json")
    suspend fun weatherCnGeoPosition(
        @Query("q") location: String,
        @Query("apikey") apiKey: String,
        @Query("language") language: String = "zh-cn"
    ): WeatherCnLocationDto

    @GET("https://openapi.weathercn.com/currentconditions/v1/{locationKey}.json")
    suspend fun weatherCnCurrentConditions(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apiKey: String,
        @Query("language") language: String = "zh-cn",
        @Query("details") details: Boolean = true
    ): List<WeatherCnCurrentDto>

    @GET("https://openapi.weathercn.com/forecasts/v1/hourly/12hour/{locationKey}.json")
    suspend fun weatherCnHourlyForecast(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apiKey: String,
        @Query("language") language: String = "zh-cn",
        @Query("details") details: Boolean = true,
        @Query("metric") metric: Boolean = true
    ): List<WeatherCnHourlyForecastDto>

    @GET("https://openapi.weathercn.com/forecasts/v1/daily/5day/{locationKey}.json")
    suspend fun weatherCnDailyForecast(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apiKey: String,
        @Query("language") language: String = "zh-cn",
        @Query("details") details: Boolean = true,
        @Query("metric") metric: Boolean = true
    ): WeatherCnDailyForecastResponse

    companion object {
        fun create(): WeatherApi {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            val client = OkHttpClient.Builder().addInterceptor(logging).build()
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            return Retrofit.Builder()
                .baseUrl("https://api.open-meteo.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(WeatherApi::class.java)
        }
    }
}

@JsonClass(generateAdapter = true)
data class ForecastResponse(
    val timezone: String?,
    val current: CurrentWeatherDto,
    val hourly: HourlyWeatherDto = HourlyWeatherDto(),
    val daily: DailyWeatherDto = DailyWeatherDto()
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherDto(
    val time: String,
    @Json(name = "temperature_2m") val temperature: Double,
    @Json(name = "apparent_temperature") val apparentTemperature: Double,
    @Json(name = "relative_humidity_2m") val relativeHumidity: Int,
    val precipitation: Double? = null,
    @Json(name = "cloud_cover") val cloudCover: Int? = null,
    @Json(name = "wind_speed_10m") val windSpeed: Double,
    @Json(name = "wind_gusts_10m") val windGusts: Double? = null,
    @Json(name = "uv_index") val uvIndex: Double? = null,
    @Json(name = "weather_code") val weatherCode: Int
)

@JsonClass(generateAdapter = true)
data class HourlyWeatherDto(
    val time: List<String> = emptyList(),
    @Json(name = "temperature_2m") val temperature: List<Double> = emptyList(),
    @Json(name = "apparent_temperature") val apparentTemperature: List<Double> = emptyList(),
    @Json(name = "relative_humidity_2m") val relativeHumidity: List<Int> = emptyList(),
    val precipitation: List<Double> = emptyList(),
    @Json(name = "cloud_cover") val cloudCover: List<Int> = emptyList(),
    @Json(name = "wind_speed_10m") val windSpeed: List<Double> = emptyList(),
    @Json(name = "wind_gusts_10m") val windGusts: List<Double> = emptyList(),
    @Json(name = "precipitation_probability") val precipitationProbability: List<Int> = emptyList(),
    @Json(name = "uv_index") val uvIndex: List<Double> = emptyList(),
    @Json(name = "weather_code") val weatherCode: List<Int> = emptyList()
)

@JsonClass(generateAdapter = true)
data class DailyWeatherDto(
    val time: List<String> = emptyList(),
    @Json(name = "temperature_2m_max") val temperatureMax: List<Double> = emptyList(),
    @Json(name = "temperature_2m_min") val temperatureMin: List<Double> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AirQualityResponse(val current: AirQualityCurrentDto? = null)

@JsonClass(generateAdapter = true)
data class AirQualityCurrentDto(@Json(name = "us_aqi") val usAqi: Int? = null)

@JsonClass(generateAdapter = true)
data class GeocodeResponse(val results: List<GeocodeResultDto> = emptyList())

@JsonClass(generateAdapter = true)
data class GeocodeResultDto(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    @Json(name = "admin1") val state: String? = null
)

@JsonClass(generateAdapter = true)
data class WeatherCnLocationDto(
    @Json(name = "Key") val key: String? = null,
    @Json(name = "LocalizedName") val localizedName: String? = null
)

@JsonClass(generateAdapter = true)
data class WeatherCnCurrentDto(
    @Json(name = "WeatherText") val weatherText: String? = null,
    @Json(name = "EpochTime") val epochTime: Long? = null,
    @Json(name = "Temperature") val temperature: WeatherCnTemperatureDto? = null,
    @Json(name = "RealFeelTemperature") val realFeelTemperature: WeatherCnTemperatureDto? = null,
    @Json(name = "RelativeHumidity") val relativeHumidity: Int? = null,
    @Json(name = "Wind") val wind: WeatherCnWindDto? = null,
    @Json(name = "WindGust") val windGust: WeatherCnWindDto? = null,
    @Json(name = "UVIndex") val uvIndex: Double? = null,
    @Json(name = "CloudCover") val cloudCover: Int? = null,
    @Json(name = "PrecipitationSummary") val precipitationSummary: WeatherCnPrecipitationSummaryDto? = null
)

@JsonClass(generateAdapter = true)
data class WeatherCnTemperatureDto(@Json(name = "Metric") val metric: WeatherCnMetricDto? = null)

@JsonClass(generateAdapter = true)
data class WeatherCnMetricDto(
    @Json(name = "Value") val value: Double? = null,
    @Json(name = "Unit") val unit: String? = null
)

@JsonClass(generateAdapter = true)
data class WeatherCnWindDto(@Json(name = "Speed") val speed: WeatherCnMetricDto? = null)

@JsonClass(generateAdapter = true)
data class WeatherCnPrecipitationSummaryDto(@Json(name = "PastHour") val pastHour: WeatherCnMetricDto? = null)

@JsonClass(generateAdapter = true)
data class WeatherCnHourlyForecastDto(
    @Json(name = "DateTime") val dateTime: String? = null,
    @Json(name = "EpochDateTime") val epochDateTime: Long? = null,
    @Json(name = "IconPhrase") val iconPhrase: String? = null,
    @Json(name = "Temperature") val temperature: WeatherCnMetricDto? = null,
    @Json(name = "PrecipitationProbability") val precipitationProbability: Int? = null
)

@JsonClass(generateAdapter = true)
data class WeatherCnDailyForecastResponse(
    @Json(name = "DailyForecasts") val dailyForecasts: List<WeatherCnDailyForecastDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class WeatherCnDailyForecastDto(
    @Json(name = "Temperature") val temperature: WeatherCnDailyTemperatureDto? = null
)

@JsonClass(generateAdapter = true)
data class WeatherCnDailyTemperatureDto(
    @Json(name = "Minimum") val minimum: WeatherCnMetricDto? = null,
    @Json(name = "Maximum") val maximum: WeatherCnMetricDto? = null
)
