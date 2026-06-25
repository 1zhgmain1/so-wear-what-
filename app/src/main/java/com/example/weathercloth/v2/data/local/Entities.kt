package com.example.weathercloth.v2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preferences")
data class UserPreferenceEntity(
    @PrimaryKey val id: Int = 1,
    val gender: String = "不限",
    val style: String = "简洁通勤",
    val thermalSensitivity: Int = 0,
    val oftenBikes: Boolean = false,
    val likesHat: Boolean = false,
    val scene: String = "通勤",
    val reminderHour: Int = 7,
    val reminderMinute: Int = 30,
    val reminderEnabled: Boolean = false
)

@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val selected: Boolean = false
)

@Entity(tableName = "wardrobe")
data class WardrobeItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val color: String? = null,
    val status: String = "可穿",
    val quantity: Int = 1,
    val statusQuantity: Int = 0,
    val warmth: Int,
    val waterproof: Boolean,
    val sunProtective: Boolean,
    val style: String,
    val imageUri: String? = null
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean,
    val createdAtMillis: Long = System.currentTimeMillis()
)
@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey val id: Int = 1,
    val cityId: Long? = null,
    val weatherJson: String = "",
    val adviceJson: String = "",
    val timestampMillis: Long = 0
)
