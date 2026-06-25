package com.example.weathercloth.v2.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

class LocationTracker(private val context: Context) {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    suspend fun currentLocation(): Result<LatLon> = runCatching {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fine != PackageManager.PERMISSION_GRANTED && coarse != PackageManager.PERMISSION_GRANTED) {
            error("定位权限被拒绝，请在系统设置中允许定位。")
        }
        val location = client.lastLocation.await()
            ?: client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token).await()
            ?: error("暂时无法获取当前位置，请稍后重试或手动添加城市。")
        LatLon(location.latitude, location.longitude)
    }
}

data class LatLon(val latitude: Double, val longitude: Double)
