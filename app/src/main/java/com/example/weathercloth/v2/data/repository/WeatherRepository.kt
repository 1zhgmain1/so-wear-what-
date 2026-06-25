package com.example.weathercloth.v2.data.repository

import com.example.weathercloth.v2.BuildConfig
import com.example.weathercloth.v2.data.local.AppDatabase
import com.example.weathercloth.v2.data.local.CityEntity
import com.example.weathercloth.v2.data.local.ReminderEntity
import com.example.weathercloth.v2.data.local.UserPreferenceEntity
import com.example.weathercloth.v2.data.local.WardrobeItemEntity
import com.example.weathercloth.v2.data.remote.CurrentWeatherDto
import com.example.weathercloth.v2.data.remote.HourlyWeatherDto
import com.example.weathercloth.v2.data.remote.WeatherApi
import com.example.weathercloth.v2.domain.OutfitAdvice
import com.example.weathercloth.v2.domain.OutfitAdvisor
import com.example.weathercloth.v2.domain.HourlyForecast
import com.example.weathercloth.v2.domain.OutfitInput
import com.example.weathercloth.v2.data.local.WeatherCacheEntity
import com.example.weathercloth.v2.domain.WeatherSnapshot
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.example.weathercloth.v2.location.LocationTracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.abs

class WeatherRepository(
    private val api: WeatherApi,
    private val database: AppDatabase,
    private val locationTracker: LocationTracker,
    private val advisor: OutfitAdvisor
) {
    private val dao = database.dao()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private var lastAdvice: Pair<WeatherSnapshot, OutfitAdvice>? = null
    private var lastCityId: Long? = null

    suspend fun getCachedAdvice(): Pair<WeatherSnapshot, OutfitAdvice>? {
        lastAdvice?.let { return it }
        return runCatching {
            val cache = dao.getWeatherCache() ?: return null
            val weather = moshi.adapter(WeatherSnapshot::class.java).fromJson(cache.weatherJson) ?: return null
            val advice = moshi.adapter(OutfitAdvice::class.java).fromJson(cache.adviceJson) ?: return null
            Pair(weather, advice)
        }.getOrNull()
    }

    private suspend fun saveToRoom(weather: WeatherSnapshot, advice: OutfitAdvice, cityId: Long?) {
        runCatching {
            val weatherJson = moshi.adapter(WeatherSnapshot::class.java).toJson(weather)
            val adviceJson = moshi.adapter(OutfitAdvice::class.java).toJson(advice)
            dao.saveWeatherCache(
                WeatherCacheEntity(
                    weatherJson = weatherJson,
                    adviceJson = adviceJson,
                    cityId = cityId,
                    timestampMillis = System.currentTimeMillis()
                )
            )
        }
    }

    fun observePreference(): Flow<UserPreferenceEntity?> = dao.observePreference()
    fun observeCities(): Flow<List<CityEntity>> = dao.observeCities()
    fun observeWardrobe(): Flow<List<WardrobeItemEntity>> = dao.observeWardrobe()
    fun observeReminders(): Flow<List<ReminderEntity>> = dao.observeReminders()

    suspend fun ensureDefaults() {
        if (dao.observePreference().first() == null) dao.savePreference(UserPreferenceEntity())
        if (dao.observeCities().first().isEmpty()) {
            dao.addCity(CityEntity(name = "北京", latitude = 39.9042, longitude = 116.4074, selected = true))
        }
    }

    suspend fun savePreference(preference: UserPreferenceEntity) = dao.savePreference(preference)
    suspend fun addWardrobeItem(item: WardrobeItemEntity) = dao.saveWardrobeItem(item)
    suspend fun deleteWardrobeItem(item: WardrobeItemEntity) = dao.deleteWardrobeItem(item)
    suspend fun saveReminder(reminder: ReminderEntity) = dao.saveReminder(reminder)
    suspend fun deleteReminder(reminder: ReminderEntity) = dao.deleteReminder(reminder)
    suspend fun deleteCity(city: CityEntity) = dao.deleteCity(city)
    suspend fun selectCity(city: CityEntity) = dao.selectCity(city.id)

    suspend fun renameCity(city: CityEntity, newName: String) = dao.updateCity(city.copy(name = newName))

    suspend fun addCityByName(name: String): Result<Unit> = runCatching {
        val result = api.geocode(query = name).results.firstOrNull()
            ?: error("没有找到城市：$name")
        dao.addCity(
            CityEntity(
                name = listOfNotNull(result.name, result.state, result.country).joinToString(" "),
                latitude = result.latitude,
                longitude = result.longitude
            )
        )
    }

    suspend fun addCurrentCity(): Result<Unit> = runCatching {
        val loc = locationTracker.currentLocation().getOrThrow()
        // 如果当前位置与已有城市重合（纬度、经度差距<0.03），直接切换到该城市
        val existing = dao.observeCities().first().firstOrNull { city ->
            kotlin.math.abs(city.latitude - loc.latitude) < 0.03 && kotlin.math.abs(city.longitude - loc.longitude) < 0.03
        }
        if (existing != null) {
            dao.selectCity(existing.id)
        } else {
            val cityName = runCatching {
                val geoResult = api.geocode(query = "${loc.latitude},${loc.longitude}")
                geoResult.results.firstOrNull()?.let { r ->
                    listOfNotNull(r.name, r.state, r.country).joinToString(" ")
                } ?: "当前位置"
            }.getOrDefault("当前位置")
            dao.addCity(CityEntity(name = cityName, latitude = loc.latitude, longitude = loc.longitude, selected = true))
        }
    }

    suspend fun loadAdvice(city: CityEntity?, forceRefresh: Boolean = true): Result<Pair<WeatherSnapshot, OutfitAdvice>> = runCatching {
        ensureDefaults()
        val selected = city ?: dao.observeCities().first().firstOrNull { it.selected } ?: dao.observeCities().first().first()
        if (!forceRefresh && lastCityId == selected.id && lastAdvice != null) return@runCatching lastAdvice!!
        val weather = fetchWeather(selected)
        val pref = dao.observePreference().first() ?: UserPreferenceEntity()
        val wardrobe = dao.observeWardrobe().first()
        val result = weather to advisor.build(OutfitInput(weather, pref, wardrobe))
        lastCityId = selected.id
        lastAdvice = result
        saveToRoom(weather, result.second, selected.id)
        result
    }

    private suspend fun fetchWeather(city: CityEntity): WeatherSnapshot = coroutineScope {
        val weatherCnDeferred = async { fetchWeatherCnWeather(city) }
        val forecastDeferred = async { runCatching { api.forecast(city.latitude, city.longitude) }.getOrNull() }
        val weatherCn = weatherCnDeferred.await()
        val forecast = forecastDeferred.await()
            ?: return@coroutineScope currentOnlyWeather(city, weatherCn)
        val air = runCatching { api.airQuality(city.latitude, city.longitude) }.getOrNull()
        val hourly = forecast.hourly
        val now = forecast.current
        val currentIndex = closestHourlyIndex(hourly.time, now.time)
        val next12Indices = (currentIndex until minOf(currentIndex + 12, hourly.temperature.size)).toList()
        val next6Indices = next12Indices.take(6)
        val rainProbability = next12Indices.maxOfOrNull { hourly.precipitationProbability.getOrElse(it) { 0 } } ?: 0
        val next6Temp = next6Indices.lastOrNull()?.let { hourly.temperature.getOrNull(it) }
        val next6Rain = next6Indices.maxOfOrNull { hourly.precipitationProbability.getOrElse(it) { 0 } } ?: 0
        val trend = when {
            next6Indices.count() < 2 -> "未来趋势数据不足"
            next6Temp != null && next6Temp - now.temperature >= 2 -> "未来 6 小时气温上升到约 ${next6Temp.toInt()}℃，最高降雨概率 $next6Rain%"
            next6Temp != null && now.temperature - next6Temp >= 2 -> "未来 6 小时气温下降到约 ${next6Temp.toInt()}℃，最高降雨概率 $next6Rain%"
            else -> "未来 6 小时温度较平稳，最高降雨概率 $next6Rain%"
        }
        val hourlyForecasts = next6Indices.mapNotNull { index ->
            val time = hourly.time.getOrNull(index) ?: return@mapNotNull null
            HourlyForecast(
                timeLabel = time.substringAfter('T', time).take(5),
                temperature = hourly.temperature.getOrElse(index) { now.temperature },
                rainProbability = hourly.precipitationProbability.getOrElse(index) { 0 },
                condition = weatherCodeDescription(hourly.weatherCode.getOrElse(index) { now.weatherCode })
            )
        }
        WeatherSnapshot(
            cityName = city.name,
            latitude = city.latitude,
            longitude = city.longitude,
            temperature = now.temperature,
            feelsLike = now.apparentTemperature,
            humidity = now.relativeHumidity,
            windSpeed = now.windSpeed,
            windGusts = weatherCn?.windGusts ?: now.windGusts ?: hourly.windGusts.getOrElse(currentIndex) { now.windSpeed },
            precipitation = weatherCn?.precipitation ?: now.precipitation ?: hourly.precipitation.getOrElse(currentIndex) { 0.0 },
            cloudCover = weatherCn?.cloudCover ?: now.cloudCover ?: hourly.cloudCover.getOrElse(currentIndex) { 0 },
            rainProbability = rainProbability,
            uvIndex = weatherCn?.uvIndex ?: uvIndex(now, hourly, next12Indices),
            airQualityIndex = airQualityLevel(air?.current?.usAqi),
            trend = trend,
            dayNightGap = dayNightGap(forecast.daily.temperatureMax, forecast.daily.temperatureMin),
            condition = weatherCn?.condition?.takeIf { it.isNotBlank() } ?: weatherCodeDescription(now.weatherCode),
            timestampMillis = timestampMillis(now.time, forecast.timezone),
            source = when {
                weatherCn != null -> "华风爱科实况 + Open-Meteo 预测"
                else -> "Open-Meteo"
            },
            hourlyForecasts = hourlyForecasts
        )
    }

    private fun currentOnlyWeather(
        city: CityEntity,
        weatherCn: WeatherCnWeatherSnapshot?
    ): WeatherSnapshot {
        val temperature = weatherCn?.temperature
            ?: error("天气服务暂时不可用，请稍后刷新")
        val precipitation = weatherCn?.precipitation ?: 0.0
        return WeatherSnapshot(
            cityName = city.name,
            latitude = city.latitude,
            longitude = city.longitude,
            temperature = temperature,
            feelsLike = weatherCn?.feelsLike ?: temperature,
            humidity = weatherCn?.humidity ?: 0,
            windSpeed = weatherCn?.windSpeed ?: 0.0,
            windGusts = weatherCn?.windGusts ?: weatherCn?.windSpeed ?: 0.0,
            precipitation = precipitation,
            cloudCover = weatherCn?.cloudCover ?: 0,
            rainProbability = weatherCn?.rainProbability ?: if (precipitation > 0.0 || (weatherCn?.condition?.contains("雨") == true)) 60 else 0,
            uvIndex = weatherCn?.uvIndex ?: 0.0,
            airQualityIndex = 1,
            trend = weatherCn?.trend ?: "预测服务暂时不可用，当前先显示实况天气；可稍后点击刷新。",
            dayNightGap = weatherCn?.dayNightGap ?: 0.0,
            condition = weatherCn?.condition ?: "实况天气",
            timestampMillis = weatherCn?.timestampMillis ?: System.currentTimeMillis(),
            source = when {
                weatherCn?.hourlyForecasts?.isNotEmpty() == true -> "华风爱科实况 + 华风预测"
                weatherCn != null -> "华风爱科实况"
                else -> "实况天气"
            },
            hourlyForecasts = weatherCn?.hourlyForecasts.orEmpty()
        )
    }

    private suspend fun fetchWeatherCnWeather(city: CityEntity): WeatherCnWeatherSnapshot? {
        val apiKey = BuildConfig.WEATHERCN_API_KEY
        if (apiKey.isBlank()) return null
        return runCatching {
            val location = api.weatherCnGeoPosition(
                location = "${city.latitude},${city.longitude}",
                apiKey = apiKey
            )
            val locationKey = location.key ?: return null
            val deferred = coroutineScope {
                val cur = async { api.weatherCnCurrentConditions(locationKey, apiKey).firstOrNull() }
                val hr = async { runCatching { api.weatherCnHourlyForecast(locationKey, apiKey).take(6) }.getOrDefault(emptyList()) }
                val day = async { runCatching { api.weatherCnDailyForecast(locationKey, apiKey).dailyForecasts.firstOrNull() }.getOrNull() }
                Triple(cur.await(), hr.await(), day.await())
            }
            val (currentRaw, hourlyRaw, daily) = deferred
            val current = currentRaw ?: return null
            val hourlyForecasts = hourlyRaw.map { forecast ->
                HourlyForecast(
                    timeLabel = forecast.dateTime?.substringAfter('T')?.take(5)
                        ?: forecast.epochDateTime?.times(1000)?.let { SimpleDateFormat("HH:mm", java.util.Locale.CHINA).format(java.util.Date(it)) }
                        ?: "--:--",
                    temperature = forecast.temperature?.value ?: current.temperature?.metric?.value ?: 0.0,
                    rainProbability = forecast.precipitationProbability ?: 0,
                    condition = forecast.iconPhrase ?: current.weatherText ?: "实况天气"
                )
            }
            val max = daily?.temperature?.maximum?.value
            val min = daily?.temperature?.minimum?.value
            val next6Rain = hourlyForecasts.maxOfOrNull { it.rainProbability } ?: 0
            val next6Temp = hourlyForecasts.lastOrNull()?.temperature
            val currentTemp = current.temperature?.metric?.value
            val trend = when {
                hourlyForecasts.size < 2 -> null
                next6Temp != null && currentTemp != null && next6Temp - currentTemp >= 2 -> "未来 6 小时气温上升到约 ${next6Temp.toInt()}℃，最高降雨概率 $next6Rain%"
                next6Temp != null && currentTemp != null && currentTemp - next6Temp >= 2 -> "未来 6 小时气温下降到约 ${next6Temp.toInt()}℃，最高降雨概率 $next6Rain%"
                else -> "未来 6 小时温度较平稳，最高降雨概率 $next6Rain%"
            }
            WeatherCnWeatherSnapshot(
                temperature = current.temperature?.metric?.value,
                feelsLike = current.realFeelTemperature?.metric?.value,
                humidity = current.relativeHumidity,
                windSpeed = current.wind?.speed?.value?.let(::kmhToMs),
                windGusts = current.windGust?.speed?.value?.let(::kmhToMs),
                precipitation = current.precipitationSummary?.pastHour?.value,
                cloudCover = current.cloudCover,
                uvIndex = current.uvIndex,
                condition = current.weatherText,
                timestampMillis = current.epochTime?.times(1000),
                rainProbability = hourlyForecasts.maxOfOrNull { it.rainProbability },
                hourlyForecasts = hourlyForecasts,
                dayNightGap = if (max != null && min != null) max - min else null,
                trend = trend
            )
        }.getOrNull()
    }

    private fun kmhToMs(value: Double): Double = value / 3.6


    private fun closestHourlyIndex(times: List<String>, currentTime: String): Int {
        val current = runCatching { LocalDateTime.parse(currentTime) }.getOrNull() ?: return times.indexOfFirst { it >= currentTime }.takeIf { it >= 0 } ?: 0
        return times.indices.minByOrNull { index ->
            val hour = runCatching { LocalDateTime.parse(times[index]) }.getOrNull() ?: current
            abs(Duration.between(current, hour).toMinutes())
        } ?: 0
    }

    private fun timestampMillis(time: String, timezone: String?): Long {
        return runCatching {
            val zone = timezone?.let(ZoneId::of) ?: ZoneId.systemDefault()
            LocalDateTime.parse(time).atZone(zone).toInstant().toEpochMilli()
        }.getOrDefault(System.currentTimeMillis())
    }



    private data class WeatherCnWeatherSnapshot(
        val temperature: Double?,
        val feelsLike: Double?,
        val humidity: Int?,
        val windSpeed: Double?,
        val windGusts: Double?,
        val precipitation: Double?,
        val cloudCover: Int?,
        val uvIndex: Double?,
        val condition: String?,
        val timestampMillis: Long?,
        val rainProbability: Int? = null,
        val hourlyForecasts: List<HourlyForecast> = emptyList(),
        val dayNightGap: Double? = null,
        val trend: String? = null
    )

    private fun uvIndex(now: CurrentWeatherDto, hourly: HourlyWeatherDto, indices: List<Int>): Double {
        val current = now.uvIndex ?: 0.0
        val next = indices.maxOfOrNull { hourly.uvIndex.getOrElse(it) { 0.0 } } ?: 0.0
        return maxOf(current, next)
    }

    private fun dayNightGap(maxValues: List<Double>, minValues: List<Double>): Double {
        val max = maxValues.firstOrNull() ?: return 0.0
        val min = minValues.firstOrNull() ?: return 0.0
        return max - min
    }

    private fun airQualityLevel(usAqi: Int?): Int = when (usAqi) {
        null -> 1
        in 0..50 -> 1
        in 51..100 -> 2
        in 101..150 -> 3
        in 151..200 -> 4
        else -> 5
    }

    private fun weatherCodeDescription(code: Int): String = when (code) {
        0 -> "晴"
        1, 2 -> "少云"
        3 -> "阴"
        45, 48 -> "雾"
        51, 53, 55 -> "毛毛雨"
        56, 57 -> "冻毛毛雨"
        61, 63, 65 -> "雨"
        66, 67 -> "冻雨"
        71, 73, 75 -> "雪"
        77 -> "雪粒"
        80, 81, 82 -> "阵雨"
        85, 86 -> "阵雪"
        95 -> "雷暴"
        96, 99 -> "雷暴伴冰雹"
        else -> "天气状态未知"
    }

}
