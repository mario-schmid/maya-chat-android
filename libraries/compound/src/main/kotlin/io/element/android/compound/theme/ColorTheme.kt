/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.compound.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import io.element.android.compound.tokens.generated.SemanticColors
import io.element.android.compound.tokens.generated.compoundColorsDark
import io.element.android.compound.tokens.generated.compoundColorsLight

/**
 * Calculates the HSV saturation value of a [Color] on a scale of 0.0f to 1.0f (0% to 100%).
 */
fun Color.hsvSaturation(): Float {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(toArgb(), hsv)
    return hsv[1]
}

/**
 * Calculates the HSV value (brightness) of a [Color] on a scale of 0.0f to 1.0f (0% to 100%).
 */
fun Color.hsvValue(): Float {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(toArgb(), hsv)
    return hsv[2]
}

/**
 * Calculates the HSV hue value of a [Color] on a scale of 0.0f to 360.0f.
 */
fun Color.hsvHue(): Float {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(toArgb(), hsv)
    return hsv[0]
}

/**
 * Converts a [Color] to a hex string (e.g., "#4D00B2").
 */
fun Color.toHex(): String {
    return String.format("#%06X", 0xFFFFFF and toArgb())
}

/**
 * Creates a [Color] from HSV values.
 */
fun Color.Companion.fromHsv(h: Float, s: Float, v: Float): Color {
    return Color(android.graphics.Color.HSVToColor(floatArrayOf(h, s, v)))
}

/**
 * Returns a [Color] with the HSV value adjusted by [deltaV].
 */
fun Color.adjustHsvValue(deltaV: Float): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(toArgb(), hsv)
    hsv[2] = (hsv[2] + deltaV).coerceIn(0f, 1f)
    return Color(android.graphics.Color.HSVToColor(hsv))
}

/**
 * Parses a hex color string (e.g., "#4d00b2" or "4d00b2") into a Compose [Color], or returns null if invalid.
 */
fun parseColorOrNull(hex: String): Color? {
    return try {
        val trimmed = hex.trim()
        val formatted = if (trimmed.startsWith("#")) trimmed else "#$trimmed"
        val parsedInt = android.graphics.Color.parseColor(formatted)
        Color(parsedInt)
    } catch (_: Exception) {
        null
    }
}

/**
 * Creates a custom [SemanticColors] theme based on a chosen [mainColor].
 *
 * Rule:
 * - If the S (saturation) value of the HSV color is lower than 50 (S < 50%), the font colors shall be black.
 * - Else if the S (saturation) value of the HSV color is higher than 50 (S >= 50%), the font colors shall be white.
 */
fun createCompoundColorTheme(mainColor: Color): SemanticColors {
    val saturationPercent = mainColor.hsvSaturation() * 100f
    val isFontBlack = saturationPercent < 50f
    val base = if (isFontBlack) compoundColorsLight else compoundColorsDark

    val fontColor = if (isFontBlack) Color.Black else Color.White
    val secondaryFontColor = if (isFontBlack) Color.Black.copy(alpha = 0.70f) else Color.White.copy(alpha = 0.70f)
    val disabledFontColor = if (isFontBlack) Color.Black.copy(alpha = 0.38f) else Color.White.copy(alpha = 0.38f)

    val overlayColor = if (isFontBlack) Color.Black else Color.White
    val bgCanvasLevel1 = overlayColor.copy(alpha = 0.05f).compositeOver(mainColor)
    val bgSubtlePrimary = overlayColor.copy(alpha = 0.14f).compositeOver(mainColor)
    val bgSubtleSecondary = overlayColor.copy(alpha = 0.09f).compositeOver(mainColor)
    val bgSubtleTertiary = overlayColor.copy(alpha = 0.05f).compositeOver(mainColor)

    return base.copy(
        bgCanvasDefault = mainColor,
        bgCanvasDefaultLevel1 = bgCanvasLevel1,
        bgSubtlePrimary = bgSubtlePrimary,
        bgSubtleSecondary = bgSubtleSecondary,
        bgSubtleSecondaryLevel0 = bgSubtleSecondary,
        bgSubtleTertiary = bgSubtleTertiary,
        bgActionPrimaryRest = bgSubtleSecondary,
        bgActionPrimaryHovered = overlayColor.copy(alpha = 0.12f).compositeOver(mainColor),
        bgActionPrimaryPressed = overlayColor.copy(alpha = 0.15f).compositeOver(mainColor),
        bgActionPrimaryDisabled = overlayColor.copy(alpha = 0.05f).compositeOver(mainColor),
        bgActionSecondaryRest = bgSubtleSecondary,
        bgActionSecondaryHovered = overlayColor.copy(alpha = 0.12f).compositeOver(mainColor),
        bgActionSecondaryPressed = overlayColor.copy(alpha = 0.15f).compositeOver(mainColor),
        bgActionTertiaryRest = mainColor,
        bgActionTertiaryHovered = overlayColor.copy(alpha = 0.05f).compositeOver(mainColor),
        bgActionTertiarySelected = bgSubtleSecondary,
        bgAccentRest = bgSubtleSecondary,
        bgAccentHovered = overlayColor.copy(alpha = 0.12f).compositeOver(mainColor),
        bgAccentPressed = overlayColor.copy(alpha = 0.15f).compositeOver(mainColor),
        bgAccentSelected = bgSubtleSecondary,
        bgAccentSubtle = bgSubtleTertiary,
        bgBadgeAccent = bgSubtleSecondary,
        textBadgeAccent = fontColor,
        borderAccentPrimary = fontColor,
        borderAccentSubtle = overlayColor.copy(alpha = 0.50f),
        borderFocused = fontColor,
        iconAccentPrimary = fontColor,
        iconAccentTertiary = secondaryFontColor,
        textActionAccent = fontColor,
        gradientActionStop1 = bgSubtleSecondary,
        gradientActionStop2 = bgSubtleSecondary,
        gradientActionStop3 = bgSubtleSecondary,
        gradientActionStop4 = bgSubtleSecondary,
        gradientSubtleStop1 = bgSubtleSecondary,
        gradientSubtleStop2 = bgSubtleSecondary,
        gradientSubtleStop3 = bgSubtleSecondary,
        gradientSubtleStop4 = bgSubtleSecondary,
        gradientSubtleStop5 = bgSubtleSecondary,
        gradientSubtleStop6 = mainColor,
        separatorPrimary = overlayColor.copy(alpha = 0.20f),
        separatorSecondary = overlayColor.copy(alpha = 0.12f),
        borderInteractivePrimary = overlayColor.copy(alpha = 0.30f),
        borderInteractiveSecondary = overlayColor.copy(alpha = 0.15f),
        borderDisabled = overlayColor.copy(alpha = 0.10f),
        textPrimary = fontColor,
        textSecondary = secondaryFontColor,
        textActionPrimary = fontColor,
        textDisabled = disabledFontColor,
        textOnSolidPrimary = fontColor,
        iconPrimary = fontColor,
        iconPrimaryAlpha = fontColor.copy(alpha = 0.87f),
        iconSecondary = secondaryFontColor,
        iconSecondaryAlpha = secondaryFontColor,
        iconTertiary = overlayColor.copy(alpha = 0.50f),
        iconTertiaryAlpha = overlayColor.copy(alpha = 0.50f),
        iconQuaternary = disabledFontColor,
        iconQuaternaryAlpha = disabledFontColor,
        iconDisabled = disabledFontColor,
        iconOnSolidPrimary = fontColor,
        isLight = isFontBlack,
    )
}
