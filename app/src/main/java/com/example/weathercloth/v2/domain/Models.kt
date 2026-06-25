package com.example.weathercloth.v2.domain

import com.example.weathercloth.v2.data.local.UserPreferenceEntity
import com.example.weathercloth.v2.data.local.WardrobeItemEntity

data class WeatherSnapshot(
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val windGusts: Double,
    val precipitation: Double,
    val cloudCover: Int,
    val rainProbability: Int,
    val uvIndex: Double,
    val airQualityIndex: Int,
    val trend: String,
    val dayNightGap: Double,
    val condition: String,
    val timestampMillis: Long,
    val source: String = "Open-Meteo",
    val hourlyForecasts: List<HourlyForecast> = emptyList()
)

data class HourlyForecast(
    val timeLabel: String,
    val temperature: Double,
    val rainProbability: Int,
    val condition: String
)

data class AdviceItem(
    val title: String,
    val suggestion: String,
    val reason: String,
    val alternatives: List<String> = emptyList()
)

data class OutfitAdvice(
    val summary: String,
    val items: List<AdviceItem>,
    val risks: List<String>
)

data class OutfitInput(
    val weather: WeatherSnapshot,
    val preference: UserPreferenceEntity,
    val wardrobe: List<WardrobeItemEntity>
)
