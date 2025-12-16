package com.pagadasports.pagada.ui.theme

import android.os.Build
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Modern Dark Color Scheme - Vibrant sports theme with purple-pink gradients
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = PrimaryPurpleDark,
    onPrimaryContainer = PrimaryPurpleLight,
    
    secondary = AccentPink,
    onSecondary = Color.White,
    secondaryContainer = AccentPinkDark,
    onSecondaryContainer = AccentPinkLight,
    
    tertiary = SecondaryTeal,
    onTertiary = Color.White,
    tertiaryContainer = SecondaryTealDark,
    onTertiaryContainer = Color.White,
    
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceDarkElevated,
    onSurfaceVariant = TextSecondary,
    
    error = Error,
    onError = Color.White,
    
    outline = TextTertiary,
    outlineVariant = Color(0xFF404656),
    
    scrim = Overlay
)

// Modern Light Color Scheme - Clean and vibrant
private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurpleDark,
    onPrimary = Color.White,
    primaryContainer = PrimaryPurpleLight,
    onPrimaryContainer = PrimaryPurpleDark,
    
    secondary = AccentPinkDark,
    onSecondary = Color.White,
    secondaryContainer = AccentPink,
    onSecondaryContainer = Color.White,
    
    tertiary = SecondaryTeal,
    onTertiary = Color.White,
    tertiaryContainer = SecondaryTealDark,
    onTertiaryContainer = SecondaryTeal,
    
    background = BackgroundLight,
    onBackground = TextDark,
    surface = SurfaceLight,
    onSurface = TextDark,
    surfaceVariant = SurfaceLightElevated,
    onSurfaceVariant = TextTertiary,
    
    error = Error,
    onError = Color.White,
    
    outline = TextTertiary,
    outlineVariant = Color(0xFFDEE2E6),
    
    scrim = Overlay
)

// Modern Shape System - Smooth, rounded corners
private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Design System - Spacing, Elevations, Animations
@Immutable
object PagadaSpacing {
    val none: Dp = 0.dp
    val extraSmall: Dp = 4.dp
    val small: Dp = 8.dp
    val medium: Dp = 16.dp
    val large: Dp = 24.dp
    val extraLarge: Dp = 32.dp
    val huge: Dp = 48.dp
}

@Immutable
object PagadaElevation {
    val none: Dp = 0.dp
    val low: Dp = 2.dp
    val medium: Dp = 4.dp
    val high: Dp = 8.dp
    val veryHigh: Dp = 16.dp
}

@Immutable
object PagadaAnimations {
    // Smooth spring animation for interactive elements
    fun <T> standardSpring(): AnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
    
    // Quick animation for micro-interactions
    fun <T> quickTween(): AnimationSpec<T> = tween(
        durationMillis = 150
    )
    
    // Standard fade/slide animations
    fun <T> standardTween(): AnimationSpec<T> = tween(
        durationMillis = 300
    )
    
    // Smooth, slower transitions
    fun <T> slowTween(): AnimationSpec<T> = tween(
        durationMillis = 500
    )
}

@Composable
fun PagadaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
