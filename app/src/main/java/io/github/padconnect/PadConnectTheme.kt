/*
 * Copyright (C) 2026 Ishan
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3 only.
 *
 * This program is distributed without any warranty. See the GNU General Public License for more details.
 */


package io.github.padconnect

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import io.github.padconnect.utils.settings.GlobalConfig

object colors {
    val primaryLight = Color(0xFF3F5F90)
    val onPrimaryLight = Color(0xFFFFFFFF)
    val primaryContainerLight = Color(0xFFD6E3FF)
    val onPrimaryContainerLight = Color(0xFF254777)
    val secondaryLight = Color(0xFF555F71)
    val onSecondaryLight = Color(0xFFFFFFFF)
    val secondaryContainerLight = Color(0xFFD9E3F8)
    val onSecondaryContainerLight = Color(0xFF3E4758)
    val tertiaryLight = Color(0xFF6F5675)
    val onTertiaryLight = Color(0xFFFFFFFF)
    val tertiaryContainerLight = Color(0xFFF9D8FE)
    val onTertiaryContainerLight = Color(0xFF563E5C)
    val errorLight = Color(0xFFBA1A1A)
    val onErrorLight = Color(0xFFFFFFFF)
    val errorContainerLight = Color(0xFFFFDAD6)
    val onErrorContainerLight = Color(0xFF93000A)
    val backgroundLight = Color(0xFFF9F9FF)
    val onBackgroundLight = Color(0xFF191C20)
    val surfaceLight = Color(0xFFF9F9FF)
    val onSurfaceLight = Color(0xFF191C20)
    val surfaceVariantLight = Color(0xFFE0E2EC)
    val onSurfaceVariantLight = Color(0xFF43474E)
    val outlineLight = Color(0xFF74777F)
    val outlineVariantLight = Color(0xFFC4C6CF)
    val scrimLight = Color(0xFF000000)
    val inverseSurfaceLight = Color(0xFF2E3035)
    val inverseOnSurfaceLight = Color(0xFFF0F0F7)
    val inversePrimaryLight = Color(0xFFA8C8FF)
    val surfaceDimLight = Color(0xFFD9D9E0)
    val surfaceBrightLight = Color(0xFFF9F9FF)
    val surfaceContainerLowestLight = Color(0xFFFFFFFF)
    val surfaceContainerLowLight = Color(0xFFF3F3FA)
    val surfaceContainerLight = Color(0xFFEDEDF4)
    val surfaceContainerHighLight = Color(0xFFE7E8EE)
    val surfaceContainerHighestLight = Color(0xFFE2E2E9)

    val primaryDark = Color(0xFFA8C8FF)
    val onPrimaryDark = Color(0xFF06305F)
    val primaryContainerDark = Color(0xFF254777)
    val onPrimaryContainerDark = Color(0xFFD6E3FF)
    val secondaryDark = Color(0xFFBDC7DC)
    val onSecondaryDark = Color(0xFF273141)
    val secondaryContainerDark = Color(0xFF3E4758)
    val onSecondaryContainerDark = Color(0xFFD9E3F8)
    val tertiaryDark = Color(0xFFDCBCE1)
    val onTertiaryDark = Color(0xFF3E2845)
    val tertiaryContainerDark = Color(0xFF563E5C)
    val onTertiaryContainerDark = Color(0xFFF9D8FE)
    val errorDark = Color(0xFFFFB4AB)
    val onErrorDark = Color(0xFF690005)
    val errorContainerDark = Color(0xFF93000A)
    val onErrorContainerDark = Color(0xFFFFDAD6)
    val backgroundDark = Color(0xFF111318)
    val onBackgroundDark = Color(0xFFE2E2E9)
    val surfaceDark = Color(0xFF111318)
    val onSurfaceDark = Color(0xFFE2E2E9)
    val surfaceVariantDark = Color(0xFF43474E)
    val onSurfaceVariantDark = Color(0xFFC4C6CF)
    val outlineDark = Color(0xFF8E9099)
    val outlineVariantDark = Color(0xFF43474E)
    val scrimDark = Color(0xFF000000)
    val inverseSurfaceDark = Color(0xFFE2E2E9)
    val inverseOnSurfaceDark = Color(0xFF2E3035)
    val inversePrimaryDark = Color(0xFF3F5F90)
    val surfaceDimDark = Color(0xFF111318)
    val surfaceBrightDark = Color(0xFF37393E)
    val surfaceContainerLowestDark = Color(0xFF0C0E13)
    val surfaceContainerLowDark = Color(0xFF191C20)
    val surfaceContainerDark = Color(0xFF1D2024)
    val surfaceContainerHighDark = Color(0xFF282A2F)
    val surfaceContainerHighestDark = Color(0xFF33353A)
}

private val lightScheme = lightColorScheme(
    primary = colors.primaryLight,
    onPrimary = colors.onPrimaryLight,
    primaryContainer = colors.primaryContainerLight,
    onPrimaryContainer = colors.onPrimaryContainerLight,
    secondary = colors.secondaryLight,
    onSecondary = colors.onSecondaryLight,
    secondaryContainer = colors.secondaryContainerLight,
    onSecondaryContainer = colors.onSecondaryContainerLight,
    tertiary = colors.tertiaryLight,
    onTertiary = colors.onTertiaryLight,
    tertiaryContainer = colors.tertiaryContainerLight,
    onTertiaryContainer = colors.onTertiaryContainerLight,
    error = colors.errorLight,
    onError = colors.onErrorLight,
    errorContainer = colors.errorContainerLight,
    onErrorContainer = colors.onErrorContainerLight,
    background = colors.backgroundLight,
    onBackground = colors.onBackgroundLight,
    surface = colors.surfaceLight,
    onSurface = colors.onSurfaceLight,
    surfaceVariant = colors.surfaceVariantLight,
    onSurfaceVariant = colors.onSurfaceVariantLight,
    outline = colors.outlineLight,
    outlineVariant = colors.outlineVariantLight,
    scrim = colors.scrimLight,
    inverseSurface = colors.inverseSurfaceLight,
    inverseOnSurface = colors.inverseOnSurfaceLight,
    inversePrimary = colors.inversePrimaryLight,
    surfaceDim = colors.surfaceDimLight,
    surfaceBright = colors.surfaceBrightLight,
    surfaceContainerLowest = colors.surfaceContainerLowestLight,
    surfaceContainerLow = colors.surfaceContainerLowLight,
    surfaceContainer = colors.surfaceContainerLight,
    surfaceContainerHigh = colors.surfaceContainerHighLight,
    surfaceContainerHighest = colors.surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = colors.primaryDark,
    onPrimary = colors.onPrimaryDark,
    primaryContainer = colors.primaryContainerDark,
    onPrimaryContainer = colors.onPrimaryContainerDark,
    secondary = colors.secondaryDark,
    onSecondary = colors.onSecondaryDark,
    secondaryContainer = colors.secondaryContainerDark,
    onSecondaryContainer = colors.onSecondaryContainerDark,
    tertiary = colors.tertiaryDark,
    onTertiary = colors.onTertiaryDark,
    tertiaryContainer = colors.tertiaryContainerDark,
    onTertiaryContainer = colors.onTertiaryContainerDark,
    error = colors.errorDark,
    onError = colors.onErrorDark,
    errorContainer = colors.errorContainerDark,
    onErrorContainer = colors.onErrorContainerDark,
    background = colors.backgroundDark,
    onBackground = colors.onBackgroundDark,
    surface = colors.surfaceDark,
    onSurface = colors.onSurfaceDark,
    surfaceVariant = colors.surfaceVariantDark,
    onSurfaceVariant = colors.onSurfaceVariantDark,
    outline = colors.outlineDark,
    outlineVariant = colors.outlineVariantDark,
    scrim = colors.scrimDark,
    inverseSurface = colors.inverseSurfaceDark,
    inverseOnSurface = colors.inverseOnSurfaceDark,
    inversePrimary = colors.inversePrimaryDark,
    surfaceDim = colors.surfaceDimDark,
    surfaceBright = colors.surfaceBrightDark,
    surfaceContainerLowest = colors.surfaceContainerLowestDark,
    surfaceContainerLow = colors.surfaceContainerLowDark,
    surfaceContainer = colors.surfaceContainerDark,
    surfaceContainerHigh = colors.surfaceContainerHighDark,
    surfaceContainerHighest = colors.surfaceContainerHighestDark,
)


@Composable
fun PadConnectTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val activity = view.context as? Activity

    val themeMode by GlobalConfig.themeMode.collectAsState(initial = 0)
    val useDynamicColors: Boolean = themeMode == 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val darkTheme: Boolean = isSystemInDarkTheme() && themeMode != 1

    val colors = if (darkTheme) {
        if (useDynamicColors) dynamicDarkColorScheme(view.context) else darkScheme
    } else {
        if (useDynamicColors) dynamicLightColorScheme(view.context) else lightScheme
    }

    SideEffect {
        activity?.window?.apply {
            statusBarColor = android.graphics.Color.TRANSPARENT
            navigationBarColor = android.graphics.Color.TRANSPARENT
            isNavigationBarContrastEnforced = false
            val insetsController = WindowInsetsControllerCompat(this, decorView)
            insetsController.isAppearanceLightNavigationBars = !darkTheme
            insetsController.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        content = content,
    )
}