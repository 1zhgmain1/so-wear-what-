package com.example.weathercloth.v2.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathercloth.v2.data.local.CityEntity
import com.example.weathercloth.v2.data.local.UserPreferenceEntity
import com.example.weathercloth.v2.data.local.ReminderEntity
import com.example.weathercloth.v2.data.local.WardrobeItemEntity
import com.example.weathercloth.v2.domain.HourlyForecast
import com.example.weathercloth.v2.domain.OutfitAdvice
import com.example.weathercloth.v2.domain.WeatherSnapshot
import com.example.weathercloth.v2.notification.NotificationHelper
import com.example.weathercloth.v2.notification.ReminderScheduler
import com.example.weathercloth.v2.ui.theme.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// ═══════════════════════════════════════════
//  Tab Navigation
// ═══════════════════════════════════════════
private enum class Tab(val title: String, val icon: ImageVector) {
    Home("\u9996\u9875", Icons.Filled.Home),
    Advice("\u5efa\u8bae", Icons.Filled.Lightbulb),
    Wardrobe("\u8863\u6a71", Icons.Filled.Checkroom),
    Cities("\u57ce\u5e02", Icons.Filled.LocationOn),
    Settings("\u8bbe\u7f6e", Icons.Filled.Settings),
    Reminder("\u63d0\u9192", Icons.Filled.Notifications)
}

private val WardrobeStatuses = listOf(
    "\u53ef\u7a7f", "\u6d17\u6da4\u4e2d", "\u5f85\u4fee\u8865", "\u4e0d\u60f3\u7a7f", "\u5df2\u501f\u51fa"
)

private val CATEGORY_TREE = mapOf(
    "\u4e0a\u88c5" to listOf("T\u6064", "\u886c\u886b", "Polo\u886b", "\u536b\u8863", "\u6bdb\u8863", "\u9488\u7ec7\u886b", "\u957f\u8896\u6253\u5e95", "\u80cc\u5fc3", "\u540a\u5e26", "\u77ed\u8896"),
    "\u4e0b\u88c5" to listOf("\u725b\u4ed4\u88e4", "\u4f11\u95f2\u88e4", "\u897f\u88e4", "\u8fd0\u52a8\u88e4", "\u77ed\u88e4", "\u534a\u8eab\u88d9", "\u9614\u817f\u88e4", "\u5de5\u88c5\u88e4", "\u7491\u4f3d\u88e4"),
    "\u978b\u5b50" to listOf("\u8fd0\u52a8\u978b", "\u5e06\u5e03\u978b", "\u76ae\u978b", "\u9774\u5b50", "\u51c9\u978b", "\u62d6\u978b", "\u677f\u978b", "\u4e50\u798f\u978b", "\u9ad8\u8ddf\u978b"),
    "\u5916\u5957" to listOf("\u5939\u514b", "\u98ce\u8863", "\u7fbd\u7ed2\u670d", "\u68c9\u670d", "\u5927\u8863", "\u897f\u88c5", "\u725b\u4ed4\u5916\u5957", "\u51b2\u950b\u8863", "\u9488\u7ec7\u5f00\u886b", "\u9a6c\u7532"),
    "\u5e3d\u5b50" to listOf("\u68d2\u7403\u5e3d", "\u6e14\u592b\u5e3d", "\u6bdb\u7ebf\u5e3d", "\u8d1d\u96f7\u5e3d", "\u906e\u9633\u5e3d"),
    "\u96e8\u5177" to listOf("\u96e8\u4f1e", "\u96e8\u8863"),
    "\u914d\u9970" to listOf("\u56f4\u5dfe", "\u624b\u5957", "\u8170\u5e26", "\u58a8\u955c", "\u624b\u8868"),
    "\u5176\u4ed6" to listOf("\u6cf3\u8863", "\u7761\u8863", "\u5bb6\u5c45\u670d", "\u889c\u5b50", "\u5185\u8863")
)
private val ALL_BROAD_CATEGORIES = CATEGORY_TREE.keys.toList()
private val ALL_SUB_CATEGORIES: Map<String, String> = run {
    val map = mutableMapOf<String, String>()
    CATEGORY_TREE.forEach { (broad, subs) -> subs.forEach { map[it] = broad } }
    map
}

// ═══════════════════════════════════════════
//  Weather Visual Helpers
// ═══════════════════════════════════════════

private fun weatherGradient(condition: String): List<Color> {
    val gradients = WeatherGradients()
    val c = condition
    return when {
        c.contains("\u6674") -> gradients.sunny
        c.contains("\u96e8") || c.contains("rain") -> gradients.rainy
        c.contains("\u96ea") || c.contains("snow") -> gradients.snowy
        c.contains("\u96fe") || c.contains("\u973e") || c.contains("fog") -> gradients.foggy
        c.contains("\u96f7") || c.contains("thunder") -> gradients.thunder
        c.contains("\u98ce") || c.contains("wind") -> gradients.windy
        c.contains("\u4e91") || c.contains("cloud") || c.contains("\u9634") -> gradients.cloudy
        else -> gradients.default
    }
}

private fun weatherIcon(condition: String): ImageVector = when {
    condition.contains("\u6674") -> Icons.Filled.WbSunny
    condition.contains("\u5c11\u4e91") -> Icons.Filled.WbCloudy
    condition.contains("\u96e8") || condition.contains("rain") -> Icons.Filled.WaterDrop
    condition.contains("\u96ea") || condition.contains("snow") -> Icons.Filled.AcUnit
    condition.contains("\u96fe") || condition.contains("\u973e") -> Icons.Filled.Cloud
    condition.contains("\u96f7") || condition.contains("thunder") -> Icons.Filled.Thunderstorm
    condition.contains("\u98ce") || condition.contains("wind") -> Icons.Filled.Air
    condition.contains("\u4e91") || condition.contains("cloud") || condition.contains("\u9634") -> Icons.Filled.Cloud
    else -> Icons.Filled.WbCloudy
}

private fun weatherEmoji(condition: String): String = when {
    condition.contains("\u6674") -> "\u2600\ufe0f"
    condition.contains("\u5c11\u4e91") -> "\u26c5"
    condition.contains("\u96e8") || condition.contains("rain") -> "\ud83c\udf27\ufe0f"
    condition.contains("\u96ea") || condition.contains("snow") -> "\u2744\ufe0f"
    condition.contains("\u96fe") || condition.contains("\u973e") -> "\ud83c\udf2b\ufe0f"
    condition.contains("\u96f7") || condition.contains("thunder") -> "\u26c8\ufe0f"
    condition.contains("\u98ce") || condition.contains("wind") -> "\ud83d\udca8"
    condition.contains("\u4e91") || condition.contains("\u9634") -> "\u2601\ufe0f"
    else -> "\ud83c\udf24\ufe0f"
}

// ═══════════════════════════════════════════
//  Main Scaffold
// ═══════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherClothRoot(vm: WeatherClothViewModel) {
    val weatherState by vm.weatherState.collectAsState()
    val appState by vm.appState.collectAsState()
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(Tab.Home) }
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap == null) return@rememberLauncherForActivityResult
        scope.launch {
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            labeler.process(inputImage).await()
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        vm.initializeWithLocation(useLocation = granted)
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Tab.entries.forEach { tab ->
                        val selected = selectedTab == tab
                        val color = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .then(if (selected) Modifier.background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                ) else Modifier)
                                .clickable { selectedTab = tab }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(tab.icon, null, tint = color, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.height(2.dp))
                            Text(tab.title, style = MaterialTheme.typography.labelSmall, color = color,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    (fadeIn(tween(250)) + slideInVertically(tween(250)) { it / 8 })
                        .togetherWith(fadeOut(tween(200)) + slideOutVertically(tween(200)) { -it / 8 })
                },
                label = "tab"
            ) { tab ->
                when (tab) {
                    Tab.Home -> HomeTab(weatherState, appState, vm, scope)
                    Tab.Advice -> AdviceTab(weatherState, vm, scope)
                    Tab.Wardrobe -> WardrobeTab(appState, vm)
                    Tab.Cities -> CitiesTab(appState, vm, scope)
                    Tab.Settings -> SettingsTab(appState, vm)
                    Tab.Reminder -> ReminderTab(appState, vm, notificationPermissionLauncher)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════
//  Home Screen
// ═══════════════════════════════════════════

@Composable
private fun HomeTab(
    weatherState: WeatherUiState,
    appState: AppUiState,
    vm: WeatherClothViewModel,
    scope: kotlinx.coroutines.CoroutineScope
) {
    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulse.animateFloat(1f, 1.02f, infiniteRepeatable(tween(3000, easing = EaseInOutCubic), RepeatMode.Reverse), label = "s")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        if (weatherState.loading) {
            item { LoadingHeader() }
        } else {
            weatherState.weather?.let { weather ->
                item { WeatherHero(weather, pulseScale, scope, vm) }
                item { Spacer(Modifier.height(16.dp)) }
                item { MetricsGrid(weather) }
                if (weather.hourlyForecasts.isNotEmpty()) {
                    item { Spacer(Modifier.height(20.dp)) }
                    item { SectionHeader("\u23f0 \u9010\u5c0f\u65f6\u9884\u62a5") }
                    item { HourlyRow(weather.hourlyForecasts) }
                }
                item { Spacer(Modifier.height(20.dp)) }
                item { TrendBanner(weather.trend) }
            }
            weatherState.advice?.let { advice ->
                item { Spacer(Modifier.height(20.dp)) }
                item { SectionHeader("\ud83d\udc54 \u4eca\u65e5\u7a7f\u642d\u5efa\u8bae") }
                item { AdvicePreview(advice) }
            }
        }
        weatherState.error?.let { err ->
            item { Spacer(Modifier.height(12.dp)) }
            item { ErrorBanner(err) }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun LoadingHeader() {
    Box(
        modifier = Modifier.fillMaxWidth().height(280.dp).padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )) {
            Column(
                Modifier.fillMaxWidth().padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(Modifier.size(40.dp), strokeWidth = 3.dp)
                Spacer(Modifier.height(16.dp))
                Text("\u6b63\u5728\u83b7\u53d6\u5929\u6c14\u6570\u636e...", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun WeatherHero(
    weather: WeatherSnapshot,
    scale: Float,
    scope: kotlinx.coroutines.CoroutineScope,
    vm: WeatherClothViewModel
) {
    val colors = weatherGradient(weather.condition)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .scale(scale)
            .shadow(12.dp, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.verticalGradient(colors))
            .padding(28.dp)
    ) {
        Column {
            // Top row: city + refresh
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(weather.cityName, style = MaterialTheme.typography.titleLarge,
                        color = Color.White, fontWeight = FontWeight.Bold)
                    Text(
                        SimpleDateFormat("MM\u6708dd\u65e5 EEEE", Locale.CHINESE).format(Date(weather.timestampMillis)),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
                IconButton(onClick = { scope.launch { vm.refresh() } },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)) {
                    Icon(Icons.Filled.Refresh, "\u5237\u65b0", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            // Weather icon + emoji
            Text(weatherEmoji(weather.condition), style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 40.sp
            ))

            Spacer(Modifier.height(12.dp))

            // Temperature
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    weather.temperature.toInt().toString(),
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
                    fontWeight = FontWeight.Light, color = Color.White
                )
                Text("\u00b0C", style = MaterialTheme.typography.headlineMedium,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 12.dp))
            }

            Spacer(Modifier.height(8.dp))

            // Condition + feels like
            Text(weather.condition, style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f))
            Text("\u4f53\u611f ${weather.feelsLike.toInt()}\u00b0C",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.65f))
        }
    }
}

@Composable
private fun MetricsGrid(weather: WeatherSnapshot) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("\ud83d\udca7 \u6e7f\u5ea6", weather.humidity.toString() + "%", Modifier.weight(1f))
            MetricCard("\ud83d\udca8 \u98ce\u901f", weather.windSpeed.toInt().toString() + " m/s", Modifier.weight(1f))
            MetricCard("\ud83c\udf27 \u964d\u96e8\u6982\u7387", weather.rainProbability.toString() + "%", Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("\u2600\ufe0f UV\u6307\u6570", String.format("%.1f", weather.uvIndex), Modifier.weight(1f))
            MetricCard("\ud83c\udf21 \u6e29\u5dee", weather.dayNightGap.toInt().toString() + "\u00b0C", Modifier.weight(1f))
            val aqi = when(weather.airQualityIndex){1->"\u4f18";2->"\u826f";3->"\u8f7b\u6c61";4->"\u4e2d\u6c61";else->"\u91cd\u6c61"}
            MetricCard("\ud83c\udf2c \u7a7a\u6c14", aqi, Modifier.weight(1f))
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier, shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
}

@Composable
private fun HourlyRow(forecasts: List<HourlyForecast>) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(forecasts) { fc ->
            Card(shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(fc.timeLabel, style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Icon(weatherIcon(fc.condition), null, Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(4.dp))
                    Text("${fc.temperature.toInt()}\u00b0", fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodySmall)
                    Text("${fc.rainProbability}%", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }
}

@Composable
private fun TrendBanner(trend: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.TrendingUp, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(trend, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun AdvicePreview(advice: OutfitAdvice) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(advice.summary, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            if (advice.items.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                advice.items.take(3).forEach { item ->
                    Row(Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Filled.CheckCircle, null, Modifier.size(16.dp).padding(top = 2.dp),
                            tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(item.suggestion, fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium)
                            Text(item.reason, style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorBanner(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f))
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Error, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
        }
    }
}

// ═══════════════════════════════════════════
//  Advice Screen
// ═══════════════════════════════════════════

@Composable
private fun AdviceTab(weatherState: WeatherUiState, vm: WeatherClothViewModel, scope: kotlinx.coroutines.CoroutineScope) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { PageTitle("\ud83d\udca1 \u7a7f\u642d\u5efa\u8bae") }

        weatherState.weather?.let { w ->
            item {
                Card(shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(44.dp).clip(CircleShape).background(
                            Brush.verticalGradient(weatherGradient(w.condition))),
                            contentAlignment = Alignment.Center) {
                            Icon(weatherIcon(w.condition), null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text("${w.cityName}  ${w.temperature.toInt()}\u00b0C", fontWeight = FontWeight.Bold)
                            Text(w.condition, style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        weatherState.advice?.let { advice ->
            item {
                Card(shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                    Column(Modifier.padding(20.dp)) {
                        Text("\ud83d\udcdd \u603b\u7ed3", fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(8.dp))
                        Text(advice.summary, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    }
                }
            }

            advice.items.forEach { item ->
                item {
                    Card(shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.CheckCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(10.dp))
                                Text(item.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("\ud83d\udc54 ${item.suggestion}", fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(4.dp))
                            Text("\ud83d\udca1 ${item.reason}", style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (item.alternatives.isNotEmpty()) {
                                Spacer(Modifier.height(6.dp))
                                Text("\u5907\u9009: ${item.alternatives.joinToString(" / ")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                    }
                }
            }

            if (advice.risks.isNotEmpty()) {
                item {
                    Card(shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))) {
                        Column(Modifier.padding(16.dp)) {
                            Text("\u26a0\ufe0f \u6ce8\u610f\u4e8b\u9879", fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(6.dp))
                            advice.risks.forEach { risk ->
                                Text("\u2022 $risk", style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }

        item {
            Button(onClick = { scope.launch { vm.refresh() } },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                Icon(Icons.Filled.Refresh, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("\u5237\u65b0\u5efa\u8bae")
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

// ═══════════════════════════════════════════
//  Wardrobe Screen
// ═══════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WardrobeTab(appState: AppUiState, vm: WeatherClothViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var editItem by remember { mutableStateOf<WardrobeItemEntity?>(null) }
    var deleteItem by remember { mutableStateOf<WardrobeItemEntity?>(null) }
    var filter by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { PageTitle("\ud83d\udc5a \u8863\u6a71\u7ba1\u7406") }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(selected = filter == null, onClick = { filter = null },
                        label = { Text("\u5168\u90e8") },
                        leadingIcon = if (filter == null) {{ Icon(Icons.Filled.Check, null, Modifier.size(16.dp)) }} else null)
                }
                items(ALL_BROAD_CATEGORIES) { cat ->
                    FilterChip(selected = filter == cat,
                        onClick = { filter = if (filter == cat) null else cat },
                        label = { Text(cat) })
                }
            }
        }

        val filtered = if (filter == null) appState.wardrobe
        else appState.wardrobe.filter { ALL_SUB_CATEGORIES[it.category] == filter || it.category == filter }

        if (filtered.isEmpty()) {
            item {
                Card(shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))) {
                    Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("\ud83d\udc54", style = MaterialTheme.typography.displaySmall)
                        Spacer(Modifier.height(8.dp))
                        Text("\u8863\u6a71\u7a7a\u7a7a\uff0c\u70b9\u51fb\u4e0b\u65b9\u6309\u94ae\u6dfb\u52a0\u8863\u7269\u5427",
                            color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        } else {
            items(filtered) { item -> WardrobeCard(item, onEdit = { editItem = item; showDialog = true }, onDelete = { deleteItem = item }) }
        }

        item {
            Button(onClick = {
                editItem = null; showDialog = true
            }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("\u6dfb\u52a0\u8863\u7269")
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }

    if (showDialog) {
        WardrobeDialog(
            editItem = editItem,
            onSave = { item ->
                vm.saveWardrobe(item); showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

    deleteItem?.let { item ->
        AlertDialog(
            onDismissRequest = { deleteItem = null },
            title = { Text("\u786e\u8ba4\u5220\u9664") },
            text = { Text("\u786e\u5b9a\u5220\u9664\u300c${item.name}\u300d\u5417\uff1f") },
            confirmButton = {
                TextButton(onClick = { vm.deleteWardrobe(item); deleteItem = null }) {
                    Text("\u5220\u9664", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { deleteItem = null }) { Text("\u53d6\u6d88") } }
        )
    }
}

@Composable
private fun WardrobeCard(item: WardrobeItemEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Checkroom, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row {
                    item.color?.let { Text("$it ", fontWeight = FontWeight.Medium) }
                    Text(item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AssistChip(onClick = {}, label = { Text(item.category, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.height(22.dp))
                    val av = if (item.status == WardrobeStatuses[0]) item.quantity
                    else (item.quantity - item.statusQuantity).coerceAtLeast(0)
                    AssistChip(onClick = {}, label = {
                        Text(if (av > 1) "x$av" else item.status, style = MaterialTheme.typography.labelSmall)
                    }, modifier = Modifier.height(22.dp))
                }
            }
            IconButton(onClick = onEdit) { Icon(Icons.Filled.Edit, null, Modifier.size(18.dp)) }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WardrobeDialog(
    editItem: WardrobeItemEntity?,
    onSave: (WardrobeItemEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(editItem?.name ?: "") }
    var color by remember { mutableStateOf(editItem?.color ?: "") }
    var selCat by remember { mutableStateOf(editItem?.category?.let { ALL_SUB_CATEGORIES[it] } ?: ALL_BROAD_CATEGORIES[0]) }
    var selSub by remember { mutableStateOf(editItem?.category ?: CATEGORY_TREE[ALL_BROAD_CATEGORIES[0]]?.firstOrNull() ?: "") }
    var warmth by remember { mutableStateOf((editItem?.warmth ?: 3).toFloat()) }
    var waterproof by remember { mutableStateOf(editItem?.waterproof ?: false) }
    var sun by remember { mutableStateOf(editItem?.sunProtective ?: false) }
    var style by remember { mutableStateOf(editItem?.style ?: "\u7b80\u7ea6\u901a\u52e4") }
    var status by remember { mutableStateOf(editItem?.status ?: WardrobeStatuses[0]) }
    var qty by remember { mutableStateOf((editItem?.quantity ?: 1).toString()) }
    var sqty by remember { mutableStateOf((editItem?.statusQuantity ?: 0).toString()) }

    // Auto-detect category and warmth from name
    LaunchedEffect(name) {
        if (name.isBlank()) return@LaunchedEffect
        // Only auto-detect for new items (not editing existing ones)
        if (editItem != null) return@LaunchedEffect
        val match = NAME_TO_CATEGORY.entries.firstOrNull { (key, _) ->
            name.contains(key)
        }
        if (match != null) {
            val (_, catInfo) = match
            selCat = catInfo.first
            selSub = catInfo.second
            warmth = catInfo.third.toFloat()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (editItem == null) "\u6dfb\u52a0\u8863\u7269" else "\u7f16\u8f91\u8863\u7269", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item { OutlinedTextField(name, { name = it }, label = { Text("\u540d\u79f0") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { Text("\u5927\u7c7b", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge) }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(ALL_BROAD_CATEGORIES) { cat ->
                            FilterChip(selected = selCat == cat, onClick = {
                                selCat = cat; selSub = CATEGORY_TREE[cat]?.firstOrNull() ?: ""
                            }, label = { Text(cat) })
                        }
                    }
                }
                item { Text("\u5c0f\u7c7b", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge) }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(CATEGORY_TREE[selCat] ?: emptyList()) { sub ->
                            FilterChip(selected = selSub == sub, onClick = { selSub = sub }, label = { Text(sub) })
                        }
                    }
                }
                item { OutlinedTextField(color, { color = it }, label = { Text("\u989c\u8272\uff08\u53ef\u9009\uff09") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { Text("\u4fdd\u6696\u5ea6: ${warmth.toInt()}", fontWeight = FontWeight.SemiBold) }
                item { Slider(warmth, { warmth = it }, valueRange = 1f..5f, steps = 3) }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("\u72b6\u6001", fontWeight = FontWeight.SemiBold)
                        var exp by remember { mutableStateOf(false) }
                        Box {
                            TextButton(onClick = { exp = true }) { Text(status) }
                            DropdownMenu(exp, { exp = false }) {
                                WardrobeStatuses.forEach { s ->
                                    DropdownMenuItem(text = { Text(s) }, onClick = { status = s; exp = false })
                                }
                            }
                        }
                    }
                }
                item { OutlinedTextField(qty, { qty = it }, label = { Text("\u603b\u6570\u91cf") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                if (status != WardrobeStatuses[0]) {
                    item { OutlinedTextField(sqty, { sqty = it }, label = { Text("\u5904\u4e8e\u300c${status}\u300d\u7684\u6570\u91cf") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("\u98ce\u683c", fontWeight = FontWeight.SemiBold)
                        val styles = listOf("\u7b80\u7ea6\u901a\u52e4", "\u8fd0\u52a8\u4f11\u95f2", "\u6b63\u5f0f\u5546\u52a1", "\u65f6\u5c1a\u6f6e\u6d41", "\u751c\u7f8e\u53ef\u7231")
                        var exp by remember { mutableStateOf(false) }
                        Box {
                            TextButton(onClick = { exp = true }) { Text(style) }
                            DropdownMenu(exp, { exp = false }) {
                                styles.forEach { s ->
                                    DropdownMenuItem(text = { Text(s) }, onClick = { style = s; exp = false })
                                }
                            }
                        }
                    }
                }
                item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("\u9632\u6c34"); Switch(waterproof, { waterproof = it }) } }
                item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("\u9632\u6652"); Switch(sun, { sun = it }) } }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val q = qty.toIntOrNull() ?: 1
                val sq = sqty.toIntOrNull() ?: 0
                onSave(WardrobeItemEntity(
                    id = editItem?.id ?: 0, name = name.ifBlank { selSub }, category = selSub,
                    color = color.ifBlank { null }, status = status, quantity = q, statusQuantity = sq,
                    warmth = warmth.toInt(), waterproof = waterproof, sunProtective = sun, style = style
                ))
            }) { Text("\u4fdd\u5b58") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("\u53d6\u6d88") } }
    )
}

// ═══════════════════════════════════════════
//  Cities Screen
// ═══════════════════════════════════════════

@Composable
private fun CitiesTab(appState: AppUiState, vm: WeatherClothViewModel, scope: kotlinx.coroutines.CoroutineScope) {
    var name by remember { mutableStateOf("") }
    var editId by remember { mutableStateOf<Long?>(null) }
    var editName by remember { mutableStateOf("") }
    var delCity by remember { mutableStateOf<CityEntity?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { PageTitle("\ud83c\udf0d \u57ce\u5e02\u7ba1\u7406") }

        item {
            OutlinedButton(onClick = { vm.addCurrentCity() }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)) {
                Icon(Icons.Filled.MyLocation, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("\u6dfb\u52a0\u5f53\u524d\u4f4d\u7f6e")
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("\u641c\u7d22\u57ce\u5e02") },
                    singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp))
                Button(onClick = {
                    if (name.isNotBlank()) { vm.addCity(name.trim()); name = "" }
                }, shape = RoundedCornerShape(14.dp)) { Text("\u6dfb\u52a0") }
            }
        }

        items(appState.cities) { city ->
            Card(shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(if (city.selected) 2.dp else 0.5.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (city.selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.clickable { scope.launch { vm.selectCity(city) } }) {
                Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (city.selected) Icons.Filled.LocationOn else Icons.Outlined.LocationOn, null,
                        tint = if (city.selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(city.name, Modifier.weight(1f),
                        fontWeight = if (city.selected) FontWeight.Bold else FontWeight.Normal)
                    IconButton(onClick = { editId = city.id; editName = city.name }) {
                        Icon(Icons.Filled.Edit, null, Modifier.size(18.dp))
                    }
                    if (!city.selected) {
                        IconButton(onClick = { delCity = city }) {
                            Icon(Icons.Filled.Delete, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }

    if (editId != null) {
        AlertDialog(
            onDismissRequest = { editId = null },
            title = { Text("\u91cd\u547d\u540d\u57ce\u5e02") },
            text = { OutlinedTextField(editName, { editName = it }, singleLine = true, label = { Text("\u57ce\u5e02\u540d\u79f0") }) },
            confirmButton = { TextButton(onClick = {
                editId?.let { id -> vm.renameCity(appState.cities.find { it.id == id }!!, editName) }; editId = null
            }) { Text("\u4fdd\u5b58") } },
            dismissButton = { TextButton(onClick = { editId = null }) { Text("\u53d6\u6d88") } }
        )
    }

    delCity?.let { city ->
        AlertDialog(
            onDismissRequest = { delCity = null },
            title = { Text("\u5220\u9664\u57ce\u5e02") },
            text = { Text("\u786e\u5b9a\u5220\u9664\u300c${city.name}\u300d\u5417\uff1f") },
            confirmButton = { TextButton(onClick = { vm.deleteCity(city); delCity = null }) {
                Text("\u5220\u9664", color = MaterialTheme.colorScheme.error)
            }},
            dismissButton = { TextButton(onClick = { delCity = null }) { Text("\u53d6\u6d88") } }
        )
    }
}

// ═══════════════════════════════════════════
//  Settings Screen
// ═══════════════════════════════════════════

@Composable
private fun SettingsTab(appState: AppUiState, vm: WeatherClothViewModel) {
    val p = appState.preference
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { PageTitle("\u2699\ufe0f \u4e2a\u4eba\u504f\u597d") }
        item {
            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    ChipRow("\u6027\u522b", listOf("\u4e0d\u9650", "\u7537", "\u5973"), p.gender) { vm.savePreference(p.copy(gender = it)) }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ChipRow("\u573a\u666f", listOf("\u901a\u52e4", "\u4f11\u95f2", "\u8fd0\u52a8", "\u7ea6\u4f1a", "\u5546\u52a1"), p.scene) { vm.savePreference(p.copy(scene = it)) }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ChipRow("\u98ce\u683c", listOf("\u7b80\u7ea6\u901a\u52e4", "\u8fd0\u52a8\u4f11\u95f2", "\u6b63\u5f0f\u5546\u52a1", "\u65f6\u5c1a\u6f6e\u6d41", "\u751c\u7f8e\u53ef\u7231"), p.style) { vm.savePreference(p.copy(style = it)) }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Text("\u4f53\u611f\u6e29\u5ea6\u504f\u79fb: ${if (p.thermalSensitivity > 0) "+${p.thermalSensitivity}" else "${p.thermalSensitivity}"}", fontWeight = FontWeight.SemiBold)
                    Slider(p.thermalSensitivity.toFloat(), { vm.savePreference(p.copy(thermalSensitivity = it.toInt())) },
                        valueRange = -3f..3f, steps = 5)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("\u6015\u51b7", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                        Text("\u6b63\u5e38", style = MaterialTheme.typography.labelSmall)
                        Text("\u6015\u70ed", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ToggleLine("\u7ecf\u5e38\u9a91\u8f66", p.oftenBikes) { vm.savePreference(p.copy(oftenBikes = it)) }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ToggleLine("\u559c\u6b22\u6234\u5e3d\u5b50", p.likesHat) { vm.savePreference(p.copy(likesHat = it)) }
                }
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

// ═══════════════════════════════════════════
//  Reminder Screen
// ═══════════════════════════════════════════

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ReminderTab(appState: AppUiState, vm: WeatherClothViewModel, notificationPermissionLauncher: androidx.activity.result.ActivityResultLauncher<String>) {
    val ctx = LocalContext.current
    var msg by remember { mutableStateOf<String?>(null) }
    val savedReminder = appState.reminders.firstOrNull()
    var reminderHour by remember(savedReminder) { mutableStateOf(savedReminder?.hour ?: 7) }
    var reminderMinute by remember(savedReminder) { mutableStateOf(savedReminder?.minute ?: 30) }
    var reminderEnabled by remember(savedReminder) { mutableStateOf(savedReminder?.enabled ?: false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 标题
        item { PageTitle("⏰ 定时提醒") }

        // 说明卡片
        item {
            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.NotificationsActive, null, modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(12.dp))
                    Text("设置每日定时推送，到时自动获取天气并给出穿衣建议。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // 时间选择
        item {
            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text("提醒时间", fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = { reminderHour = (reminderHour + 1) % 24 }) {
                                Icon(Icons.Filled.KeyboardArrowUp, "增加小时")
                            }
                            Text("%02d".format(reminderHour),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary)
                            IconButton(onClick = { reminderHour = (reminderHour - 1 + 24) % 24 }) {
                                Icon(Icons.Filled.KeyboardArrowDown, "减少小时")
                            }
                        }
                        Text(" : ", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = { reminderMinute = (reminderMinute + 1) % 60 }) {
                                Icon(Icons.Filled.KeyboardArrowUp, "增加分钟")
                            }
                            Text("%02d".format(reminderMinute),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary)
                            IconButton(onClick = { reminderMinute = (reminderMinute - 1 + 60) % 60 }) {
                                Icon(Icons.Filled.KeyboardArrowDown, "减少分钟")
                            }
                        }
                    }
                }
            }
        }

        // 开启/关闭开关
        item {
            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Row(
                    Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("开启提醒", fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyLarge)
                        Text(
                            if (reminderEnabled) "已启用，每天 %02d:%02d 推送".format(reminderHour, reminderMinute)
                            else "暂未启用",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled) {
                                val hasPermission = if (Build.VERSION.SDK_INT >= 33) {
                                    androidx.core.content.ContextCompat.checkSelfPermission(
                                        ctx, Manifest.permission.POST_NOTIFICATIONS
                                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                } else true
                                if (!hasPermission) {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    msg = "正在申请通知权限，请在弹窗中选择“允许”"
                                    return@Switch
                                }
                            }
                            reminderEnabled = enabled
                            val reminder = com.example.weathercloth.v2.data.local.ReminderEntity(
                                id = savedReminder?.id ?: 0,
                                hour = reminderHour,
                                minute = reminderMinute,
                                enabled = enabled
                            )
                            vm.saveReminder(reminder)
                            if (enabled) {
                                ReminderScheduler.scheduleReminder(ctx, reminderHour, reminderMinute)
                                msg = "已设置每天 %02d:%02d 推送提醒".format(reminderHour, reminderMinute)
                            } else {
                                ReminderScheduler.cancel(ctx)
                                msg = "已关闭提醒"
                            }
                        }
                    )
                }
            }
        }

        // 保存按钮
        item {
            Button(onClick = {
                val reminder = com.example.weathercloth.v2.data.local.ReminderEntity(
                    id = savedReminder?.id ?: 0,
                    hour = reminderHour,
                    minute = reminderMinute,
                    enabled = reminderEnabled
                )
                vm.saveReminder(reminder)
                if (reminderEnabled) {
                    ReminderScheduler.scheduleReminder(ctx, reminderHour, reminderMinute)
                    msg = "提醒已更新：每天 %02d:%02d".format(reminderHour, reminderMinute)
                }
            }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                Icon(Icons.Filled.Save, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("保存设置")
            }
        }

        // 测试通知按钮
        item {
            OutlinedButton(onClick = {
                val hasPermission = if (Build.VERSION.SDK_INT >= 33) {
                    androidx.core.content.ContextCompat.checkSelfPermission(
                        ctx, Manifest.permission.POST_NOTIFICATIONS
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                } else true
                if (!hasPermission) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    msg = "正在申请通知权限，请在弹窗中选择“允许”后再次点击"
                } else {
                    ReminderScheduler.testNotification(ctx)
                    msg = "测试通知已发送，请查看通知栏"
                }
            }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                Icon(Icons.Filled.Send, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("发送测试通知")
            }
        }

        // 反馈消息
        msg?.let { m ->
            item {
                Card(shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, null, tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(m, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}


// ═══════════════════════════════════════════
//  Shared Components
// ═══════════════════════════════════════════

@Composable
private fun PageTitle(text: String) {
    Text(text, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(4.dp))
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
}

@Composable
private fun ChipRow(title: String, choices: List<String>, selected: String, onChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            choices.forEach { c ->
                FilterChip(selected = selected == c, onClick = { onChange(c) }, label = { Text(c) })
            }
        }
    }
}

@Composable
private fun ToggleLine(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontWeight = FontWeight.SemiBold)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

// Keyword -> (BroadCategory, SubCategory, DefaultWarmth)
private val NAME_TO_CATEGORY: Map<String, Triple<String, String, Int>> = mapOf(
    "T恤" to Triple("上装", "T恤", 1),
    "衬衫" to Triple("上装", "衬衫", 2),
    "Polo" to Triple("上装", "Polo衫", 2),
    "卫衣" to Triple("上装", "卫衣", 3),
    "毛衣" to Triple("上装", "毛衣", 5),
    "针织衫" to Triple("上装", "针织衫", 3),
    "针织" to Triple("上装", "针织衫", 3),
    "长袖打底" to Triple("上装", "长袖打底", 2),
    "打底" to Triple("上装", "长袖打底", 2),
    "背心" to Triple("上装", "背心", 1),
    "吊带" to Triple("上装", "吊带", 1),
    "短袖" to Triple("上装", "短袖", 1),
    "牛仔裤" to Triple("下装", "牛仔裤", 2),
    "牛仔" to Triple("下装", "牛仔裤", 2),
    "休闲裤" to Triple("下装", "休闲裤", 2),
    "西裤" to Triple("下装", "西裤", 2),
    "运动裤" to Triple("下装", "运动裤", 2),
    "短裤" to Triple("下装", "短裤", 1),
    "半身裙" to Triple("下装", "半身裙", 2),
    "阔腿裤" to Triple("下装", "阔腿裤", 2),
    "工装裤" to Triple("下装", "工装裤", 2),
    "璑伽裤" to Triple("下装", "璑伽裤", 2),
    "运动鞋" to Triple("鞋子", "运动鞋", 2),
    "帆布鞋" to Triple("鞋子", "帆布鞋", 2),
    "皮鞋" to Triple("鞋子", "皮鞋", 2),
    "靴子" to Triple("鞋子", "靴子", 4),
    "凉鞋" to Triple("鞋子", "凉鞋", 1),
    "拖鞋" to Triple("鞋子", "拖鞋", 1),
    "板鞋" to Triple("鞋子", "板鞋", 2),
    "乐福鞋" to Triple("鞋子", "乐福鞋", 2),
    "高跟鞋" to Triple("鞋子", "高跟鞋", 2),
    "夹克" to Triple("外套", "夹克", 3),
    "风衣" to Triple("外套", "风衣", 3),
    "羽绒服" to Triple("外套", "羽绒服", 5),
    "羽绒" to Triple("外套", "羽绒服", 5),
    "棉服" to Triple("外套", "棉服", 4),
    "大衣" to Triple("外套", "大衣", 4),
    "西装" to Triple("外套", "西装", 3),
    "外套" to Triple("外套", "牛仔外套", 3),
    "冲锋衣" to Triple("外套", "冲锋衣", 4),
    "开衫" to Triple("外套", "针织开衫", 3),
    "马甲" to Triple("外套", "马甲", 2),
    "棒球帽" to Triple("帽子", "棒球帽", 1),
    "渔夫帽" to Triple("帽子", "渔夫帽", 2),
    "毛线帽" to Triple("帽子", "毛线帽", 3),
    "贝雷帽" to Triple("帽子", "贝雷帽", 2),
    "遮阳帽" to Triple("帽子", "遮阳帽", 1),
    "围巾" to Triple("配饰", "围巾", 2),
    "手套" to Triple("配饰", "手套", 3),
    "腰带" to Triple("配饰", "腰带", 1),
    "墨镜" to Triple("配饰", "墨镜", 1),
    "太阳镜" to Triple("配饰", "墨镜", 1),
    "手表" to Triple("配饰", "手表", 1),
    "泳衣" to Triple("其他", "泳衣", 1),
    "睡衣" to Triple("其他", "睡衣", 2),
    "家居服" to Triple("其他", "家居服", 2),
    "袜子" to Triple("其他", "袜子", 1),
    "内衣" to Triple("其他", "内衣", 1),
    "雨伞" to Triple("雨具", "雨伞", 1),
    "雨衣" to Triple("雨具", "雨衣", 2),
)

private fun fmtTime(hour: Int, minute: Int) = "%02d:%02d".format(hour, minute)
