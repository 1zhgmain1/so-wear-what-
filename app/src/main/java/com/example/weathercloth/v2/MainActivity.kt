package com.example.weathercloth.v2

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weathercloth.v2.ui.WeatherClothRoot
import com.example.weathercloth.v2.ui.WeatherClothViewModel
import com.example.weathercloth.v2.ui.WeatherClothViewModelFactory
import com.example.weathercloth.v2.ui.theme.WeatherClothTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Hide navigation bar for immersive experience
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        val app = application as WeatherClothApp
        setContent {
            WeatherClothTheme {
                val vm: WeatherClothViewModel = viewModel(
                    factory = WeatherClothViewModelFactory(app.container.repository)
                )
                WeatherClothRoot(vm)
            }
        }
    }
}
