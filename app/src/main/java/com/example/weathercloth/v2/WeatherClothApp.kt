package com.example.weathercloth.v2

import android.app.Application
import com.example.weathercloth.v2.data.local.AppDatabase
import com.example.weathercloth.v2.data.remote.WeatherApi
import com.example.weathercloth.v2.data.repository.WeatherRepository
import com.example.weathercloth.v2.domain.OutfitAdvisor
import com.example.weathercloth.v2.location.LocationTracker
import com.example.weathercloth.v2.notification.NotificationHelper

class WeatherClothApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.ensureChannel(this)
        val db = AppDatabase.create(this)
        container = AppContainer(
            repository = WeatherRepository(
                api = WeatherApi.create(),
                database = db,
                locationTracker = LocationTracker(this),
                advisor = OutfitAdvisor()
            )
        )
    }
}

data class AppContainer(val repository: WeatherRepository)
