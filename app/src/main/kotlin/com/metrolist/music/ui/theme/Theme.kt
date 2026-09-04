/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.theme

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import com.materialkolor.score.Score

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

val AppleMusicRed = Color(0xFFFA243C)
val DefaultThemeColor = AppleMusicRed

fun createIosColorScheme(isDark: Boolean, accentColor: Color = AppleMusicRed): ColorScheme {
    return if (isDark) {
        darkColorScheme(
            primary = accentColor,
            onPrimary = Color.White,
            primaryContainer = accentColor.copy(alpha = 0.22f),
            onPrimaryContainer = Color(0xFFFFDADE),
            inversePrimary = Color.White,
            secondary = accentColor,
            onSecondary = Color.White,
            secondaryContainer = Color(0xFF1C1C1E),
            onSecondaryContainer = Color.White,
            tertiary = accentColor,
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFF2C2C2E),
            onTertiaryContainer = Color.White,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color.Black,
            onSurface = Color.White,
            surfaceVariant = Color(0xFF1C1C1E),
            onSurfaceVariant = Color(0xFF8E8E93),
            surfaceTint = Color.Transparent,
            inverseSurface = Color.White,
            inverseOnSurface = Color.Black,
            error = Color(0xFFFF453A),
            onError = Color.White,
            errorContainer = Color(0x33FF453A),
            onErrorContainer = Color(0xFFFF453A),
            outline = Color(0xFF38383A),
            outlineVariant = Color(0xFF2C2C2E),
            scrim = Color.Black.copy(alpha = 0.6f),
            surfaceBright = Color(0xFF2C2C2E),
            surfaceDim = Color.Black,
            surfaceContainer = Color(0xFF1C1C1E),
            surfaceContainerHigh = Color(0xFF2C2C2E),
            surfaceContainerHighest = Color(0xFF3A3A3C),
            surfaceContainerLow = Color(0xFF121214),
            surfaceContainerLowest = Color.Black,
        )
    } else {
        lightColorScheme(
            primary = accentColor,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFDADE),
            onPrimaryContainer = Color(0xFF41000B),
            inversePrimary = Color.White,
            secondary = accentColor,
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFE5E5EA),
            onSecondaryContainer = Color.Black,
            tertiary = accentColor,
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFF2F2F7),
            onTertiaryContainer = Color.Black,
            background = Color.White,
            onBackground = Color.Black,
            surface = Color.White,
            onSurface = Color.Black,
            surfaceVariant = Color(0xFFF2F2F7),
            onSurfaceVariant = Color(0xFF8E8E93),
            surfaceTint = Color.Transparent,
            inverseSurface = Color.Black,
            inverseOnSurface = Color.White,
            error = Color(0xFFFF3B30),
            onError = Color.White,
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),
            outline = Color(0xFFC6C6C8),
            outlineVariant = Color(0xFFE5E5EA),
            scrim = Color.Black.copy(alpha = 0.4f),
            surfaceBright = Color.White,
            surfaceDim = Color(0xFFE5E5EA),
            surfaceContainer = Color(0xFFF2F2F7),
            surfaceContainerHigh = Color(0xFFE5E5EA),
            surfaceContainerHighest = Color(0xFFD1D1D6),
            surfaceContainerLow = Color(0xFFF8F8F9),
            surfaceContainerLowest = Color.White,
        )
    }
}

@Composable
fun MetrolistTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    pureBlack: Boolean = false,
    themeColor: Color = DefaultThemeColor,
    content: @Composable () -> Unit,
) {
    val colorScheme = remember(themeColor, darkTheme, pureBlack) {
        val base = createIosColorScheme(isDark = darkTheme, accentColor = themeColor)
        if (darkTheme && pureBlack) {
            base.copy(surface = Color.Black, background = Color.Black)
        } else {
            base
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}

fun Bitmap.extractThemeColor(): Color = Color(
    Palette.from(this)
        .maximumColorCount(8)
        .generate()
        .rankedColors(1, DefaultThemeColor.toArgb())
        .first()
)

internal fun Palette.rankedColors(
    desiredColorCount: Int,
    fallbackColor: Int,
): List<Int> = Score.score(
    swatches.associate { it.rgb to it.population },
    desiredColorCount,
    fallbackColor,
    true,
)

fun ColorScheme.pureBlack(apply: Boolean) =
    if (apply) copy(
        surface = Color.Black,
        background = Color.Black
    ) else this

val ColorSaver = object : Saver<Color, Int> {
    override fun restore(value: Int): Color = Color(value)
    override fun SaverScope.save(value: Color): Int = value.toArgb()
}
