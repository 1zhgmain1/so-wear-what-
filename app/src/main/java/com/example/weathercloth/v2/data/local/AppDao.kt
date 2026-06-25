package com.example.weathercloth.v2.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM preferences WHERE id = 1")
    fun observePreference(): Flow<UserPreferenceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePreference(preference: UserPreferenceEntity)

    @Query("SELECT * FROM cities ORDER BY selected DESC, name")
    fun observeCities(): Flow<List<CityEntity>>

    @Insert
    suspend fun addCity(city: CityEntity)

    @Update
    suspend fun updateCity(city: CityEntity)

    @Delete
    suspend fun deleteCity(city: CityEntity)

    @Query("UPDATE cities SET selected = CASE WHEN id = :id THEN 1 ELSE 0 END")
    suspend fun selectCity(id: Long)

    @Query("SELECT * FROM wardrobe ORDER BY category, name")
    fun observeWardrobe(): Flow<List<WardrobeItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWardrobeItem(item: WardrobeItemEntity)

    @Delete
    suspend fun deleteWardrobeItem(item: WardrobeItemEntity)

    @Query("SELECT * FROM reminders ORDER BY createdAtMillis DESC")
    fun observeReminders(): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)
    @Query("SELECT * FROM weather_cache WHERE id = 1")
    suspend fun getWeatherCache(): WeatherCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWeatherCache(cache: WeatherCacheEntity)

}
