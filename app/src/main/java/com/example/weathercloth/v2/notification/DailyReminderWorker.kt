package com.example.weathercloth.v2.notification

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weathercloth.v2.data.local.AppDatabase
import com.example.weathercloth.v2.data.local.WardrobeItemEntity
import com.example.weathercloth.v2.data.remote.WeatherApi
import com.example.weathercloth.v2.domain.HourlyForecast
import com.example.weathercloth.v2.domain.OutfitAdvisor
import com.example.weathercloth.v2.domain.OutfitInput
import com.example.weathercloth.v2.domain.WeatherSnapshot
import kotlinx.coroutines.flow.first

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    companion object {
        private const val TAG = "ReminderWorker"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "定时提醒任务开始执行")
        return try {
            val db = AppDatabase.create(applicationContext)
            val dao = db.dao()

            val reminders = dao.observeReminders().first()
            val enabledReminder = reminders.firstOrNull { it.enabled }
            if (enabledReminder == null) {
                Log.d(TAG, "没有启用的提醒，跳过")
                ReminderScheduler.scheduleNext(applicationContext)
                return Result.success()
            }

            val pref = dao.observePreference().first() ?: return Result.success()
            val cities = dao.observeCities().first()
            val city = cities.firstOrNull { it.selected } ?: cities.firstOrNull() ?: return Result.success()
            val wardrobe = dao.observeWardrobe().first()

            val api = WeatherApi.create()
            val advisor = OutfitAdvisor()

            val forecastResponse = api.forecast(
                latitude = city.latitude,
                longitude = city.longitude
            )

            val current = forecastResponse.current
            val hourly = forecastResponse.hourly
            val daily = forecastResponse.daily

            val dayNightGap = if (daily.temperatureMax.isNotEmpty() && daily.temperatureMin.isNotEmpty())
                daily.temperatureMax.first() - daily.temperatureMin.first()
            else 0.0

            val hForecasts = hourly.time.take(12).mapIndexed { i, t ->
                HourlyForecast(
                    timeLabel = t,
                    temperature = hourly.temperature.getOrElse(i) { 0.0 },
                    rainProbability = hourly.precipitationProbability.getOrElse(i) { 0 },
                    condition = if (i < hourly.weatherCode.size) weatherCodeDesc(hourly.weatherCode[i]) else "晴"
                )
            }

            val snapshot = WeatherSnapshot(
                cityName = city.name,
                latitude = city.latitude,
                longitude = city.longitude,
                temperature = current.temperature,
                feelsLike = current.apparentTemperature,
                humidity = current.relativeHumidity,
                windSpeed = current.windSpeed,
                windGusts = current.windGusts ?: 0.0,
                precipitation = current.precipitation ?: 0.0,
                cloudCover = current.cloudCover ?: 0,
                rainProbability = if (hourly.precipitationProbability.isNotEmpty()) hourly.precipitationProbability.maxOrNull()!! else 0,
                uvIndex = current.uvIndex ?: 0.0,
                airQualityIndex = 1,
                trend = "",
                dayNightGap = dayNightGap,
                condition = weatherCodeDesc(current.weatherCode),
                timestampMillis = System.currentTimeMillis(),
                hourlyForecasts = hForecasts
            )

            val input = OutfitInput(
                weather = snapshot,
                preference = pref,
                wardrobe = wardrobe
            )
            val advice = advisor.build(input)

            NotificationHelper.showDailyAdvice(
                applicationContext,
                "今日穿衣建议 - " + city.name,
                advice.summary + "\n" + advice.items.joinToString("; ") { it.suggestion }.take(200)
            )
            Log.d(TAG, "通知已发送: " + city.name)

            ReminderScheduler.scheduleNext(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "定时提醒任务失败", e)
            ReminderScheduler.scheduleNext(applicationContext)
            Result.success()
        }
    }

    private fun weatherCodeDesc(code: Int): String = when (code) {
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
        else -> "未知"
    }
}
