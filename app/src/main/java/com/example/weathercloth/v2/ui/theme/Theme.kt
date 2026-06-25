package com.example.weathercloth.v2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ═══════════════════════════════════════════
//  Modern Vibrant Color System
// ═══════════════════════════════════════════

// Light scheme - warm & soft
private val LightPrimary = Color(0xFF6366F1)
private val LightOnPrimary = Color(0xFFFFFFFF)
private val LightPrimaryContainer = Color(0xFFE0E7FF)
private val LightOnPrimaryContainer = Color(0xFF1E1B4B)
private val LightSecondary = Color(0xFF7C3AED)
private val LightOnSecondary = Color(0xFFFFFFFF)
private val LightSecondaryContainer = Color(0xFFEDE9FE)
private val LightOnSecondaryContainer = Color(0xFF2E1065)
private val LightTertiary = Color(0xFFEC4899)
private val LightOnTertiary = Color(0xFFFFFFFF)
private val LightTertiaryContainer = Color(0xFFFCE7F3)
private val LightOnTertiaryContainer = Color(0xFF500724)
private val LightBackground = Color(0xFFFAFAFE)
private val LightOnBackground = Color(0xFF1B1B2E)
private val LightSurface = Color(0xFFFAFAFE)
private val LightOnSurface = Color(0xFF1B1B2E)
private val LightSurfaceVariant = Color(0xFFF1F0F6)
private val LightOnSurfaceVariant = Color(0xFF494860)
private val LightOutline = Color(0xFF797890)
private val LightOutlineVariant = Color(0xFFCAC9DA)
private val LightError = Color(0xFFDC2626)
private val LightOnError = Color(0xFFFFFFFF)
private val LightErrorContainer = Color(0xFFFEE2E2)
private val LightOnErrorContainer = Color(0xFF7F1D1D)

// Dark scheme - deep & rich
private val DarkPrimary = Color(0xFFA5B4FC)
private val DarkOnPrimary = Color(0xFF1E1B4B)
private val DarkPrimaryContainer = Color(0xFF4F46E5)
private val DarkOnPrimaryContainer = Color(0xFFE0E7FF)
private val DarkSecondary = Color(0xFFC4B5FD)
private val DarkOnSecondary = Color(0xFF2E1065)
private val DarkSecondaryContainer = Color(0xFF6D28D9)
private val DarkOnSecondaryContainer = Color(0xFFEDE9FE)
private val DarkTertiary = Color(0xFFF9A8D4)
private val DarkOnTertiary = Color(0xFF500724)
private val DarkTertiaryContainer = Color(0xFFBE185D)
private val DarkOnTertiaryContainer = Color(0xFFFCE7F3)
private val DarkBackground = Color(0xFF0F0F1A)
private val DarkOnBackground = Color(0xFFE4E4EC)
private val DarkSurface = Color(0xFF0F0F1A)
private val DarkOnSurface = Color(0xFFE4E4EC)
private val DarkSurfaceVariant = Color(0xFF2D2D3F)
private val DarkOnSurfaceVariant = Color(0xFFCAC9DA)
private val DarkOutline = Color(0xFF9392A6)
private val DarkOutlineVariant = Color(0xFF494860)
private val DarkError = Color(0xFFFCA5A5)
private val DarkOnError = Color(0xFF7F1D1D)
private val DarkErrorContainer = Color(0xFF991B1B)
private val DarkOnErrorContainer = Color(0xFFFEE2E2)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary, onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer, onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary, onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer, onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary, onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer, onTertiaryContainer = LightOnTertiaryContainer,
    background = LightBackground, onBackground = LightOnBackground,
    surface = LightSurface, onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant, onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline, outlineVariant = LightOutlineVariant,
    error = LightError, onError = LightOnError,
    errorContainer = LightErrorContainer, onErrorContainer = LightOnErrorContainer,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary, onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer, onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary, onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer, onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary, onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer, onTertiaryContainer = DarkOnTertiaryContainer,
    background = DarkBackground, onBackground = DarkOnBackground,
    surface = DarkSurface, onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant, onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline, outlineVariant = DarkOutlineVariant,
    error = DarkError, onError = DarkOnError,
    errorContainer = DarkErrorContainer, onErrorContainer = DarkOnErrorContainer,
)

// ═══════════════════════════════════════════
//  Weather Gradient System
// ═══════════════════════════════════════════

@Immutable
data class WeatherGradients(
    val sunny: List<Color> = listOf(Color(0xFFFF8C42), Color(0xFFFFD93D), Color(0xFFFF6B35)),
    val cloudy: List<Color> = listOf(Color(0xFF6C7A89), Color(0xFF93A5CF), Color(0xFFE4EFE9)),
    val rainy: List<Color> = listOf(Color(0xFF3A7BD5), Color(0xFF5B86E5), Color(0xFF36D1DC)),
    val snowy: List<Color> = listOf(Color(0xFFE6DADA), Color(0xFFCFDEF3), Color(0xFFE0EAFC)),
    val foggy: List<Color> = listOf(Color(0xFFB8B8B8), Color(0xFFD6DAE0), Color(0xFFEAECEF)),
    val thunder: List<Color> = listOf(Color(0xFF232526), Color(0xFF414345), Color(0xFF614385)),
    val windy: List<Color> = listOf(Color(0xFF43C6AC), Color(0xFF66C6B7), Color(0xFF85E3CD)),
    val night: List<Color> = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364)),
    val default: List<Color> = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFFD946EF)),
)

// ═══════════════════════════════════════════
//  Typography
// ═══════════════════════════════════════════

val AppTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 57.sp, lineHeight = 64.sp),
    displayMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 45.sp, lineHeight = 52.sp),
    displaySmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 44.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 36.sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp),
)

// ═══════════════════════════════════════════
//  CompositionLocal for weather gradients
// ═══════════════════════════════════════════

val LocalWeatherGradients = staticCompositionLocalOf { WeatherGradients() }

@Composable
fun WeatherClothTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
