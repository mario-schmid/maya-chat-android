/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.advanced

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.theme.fromHsv
import io.element.android.compound.theme.hsvHue
import io.element.android.compound.theme.hsvSaturation
import io.element.android.compound.theme.hsvValue
import io.element.android.compound.theme.parseColorOrNull
import io.element.android.compound.theme.toHex
import io.element.android.features.preferences.impl.R
import io.element.android.libraries.designsystem.components.dialogs.ListDialog
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.TextField
import io.element.android.libraries.ui.strings.CommonStrings
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun ColorThemePickerDialog(
    initialColorHex: String,
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val initialColor = remember { parseColorOrNull(initialColorHex) ?: Color(0xFF4D00B2) }
    
    var hue by remember { mutableFloatStateOf(initialColor.hsvHue()) }
    var saturation by remember { mutableFloatStateOf(initialColor.hsvSaturation()) }
    var value by remember { mutableFloatStateOf(initialColor.hsvValue()) }
    
    var hexInput by remember { mutableStateOf(initialColorHex) }

    val currentColor by remember {
        derivedStateOf {
            Color.fromHsv(hue, saturation, value)
        }
    }

    val saturationPercent = (currentColor.hsvSaturation() * 100f).roundToInt()
    val isFontBlack = saturationPercent < 50
    val previewFontColor = if (isFontBlack) Color.Black else Color.White

    ListDialog(
        modifier = modifier,
        submitText = stringResource(CommonStrings.action_save),
        cancelText = stringResource(CommonStrings.action_cancel),
        onSubmit = { onSubmit(currentColor.toHex()) },
        onDismissRequest = onDismiss,
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Color wheel
                ColorWheel(
                    hue = hue,
                    saturation = saturation,
                    value = value,
                    onColorChanged = { newHue, newSaturation ->
                        hue = newHue
                        saturation = newSaturation
                        hexInput = currentColor.toHex()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .align(Alignment.CenterHorizontally),
                )

                // Brightness slider
                BrightnessSlider(
                    hue = hue,
                    saturation = saturation,
                    value = value,
                    onValueChange = { newValue ->
                        value = newValue
                        hexInput = currentColor.toHex()
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                // Custom Hex input
                TextField(
                    value = hexInput,
                    onValueChange = { input ->
                        hexInput = input
                        parseColorOrNull(input)?.let { parsed ->
                            hue = parsed.hsvHue()
                            saturation = parsed.hsvSaturation()
                            value = parsed.hsvValue()
                        }
                    },
                    placeholder = "#4D00B2",
                    modifier = Modifier.fillMaxWidth(),
                )

                // Live Preview box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(currentColor)
                        .border(1.dp, ElementTheme.colors.borderInteractiveSecondary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = if (isFontBlack) {
                                stringResource(R.string.screen_advanced_settings_preview_font_black)
                            } else {
                                stringResource(R.string.screen_advanced_settings_preview_font_white)
                            },
                            color = previewFontColor,
                            style = ElementTheme.typography.fontBodyMdMedium,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Saturation: $saturationPercent%",
                            color = previewFontColor.copy(alpha = 0.8f),
                            style = ElementTheme.typography.fontBodySmRegular,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColorWheel(
    hue: Float,
    saturation: Float,
    value: Float,
    onColorChanged: (hue: Float, saturation: Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val updatedOnColorChanged by rememberUpdatedState(onColorChanged)
    val updatedValue by rememberUpdatedState(value)

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = minOf(size.width, size.height) / 2f
                    
                    fun updateColor(offset: Offset) {
                        val distance = (offset - center).getDistance()
                        val angle = ((atan2(offset.y - center.y, offset.x - center.x) * 180 / PI)).toFloat()
                        val newHue = (angle + 360f) % 360f
                        val newSaturation = (distance / radius).coerceIn(0f, 1f)
                        updatedOnColorChanged(newHue, newSaturation)
                    }

                    updateColor(down.position)
                    down.consume()

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id }
                        if (change == null || !change.pressed) break
                        updateColor(change.position)
                        change.consume()
                    }
                }
            }
    ) {
        val radius = size.minDimension / 2
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red
                ),
                center = center
            ),
            radius = radius
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, Color.Transparent),
                center = center,
                radius = radius
            ),
            radius = radius
        )
        // Selector
        val angleRad = hue * PI / 180
        val selectorX = center.x + cos(angleRad) * saturation * radius
        val selectorY = center.y + sin(angleRad) * saturation * radius
        drawCircle(
            color = Color.White,
            radius = 8.dp.toPx(),
            center = Offset(selectorX.toFloat(), selectorY.toFloat()),
            style = Stroke(width = 2.dp.toPx())
        )
        drawCircle(
            color = Color.Black,
            radius = 7.dp.toPx(),
            center = Offset(selectorX.toFloat(), selectorY.toFloat()),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

@Composable
private fun BrightnessSlider(
    hue: Float,
    saturation: Float,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val updatedOnValueChange by rememberUpdatedState(onValueChange)
    val updatedHue by rememberUpdatedState(hue)
    val updatedSaturation by rememberUpdatedState(saturation)

    Canvas(
        modifier = modifier
            .height(24.dp)
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    updatedOnValueChange((down.position.x / size.width).coerceIn(0f, 1f))
                    down.consume()

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id }
                        if (change == null || !change.pressed) break
                        updatedOnValueChange((change.position.x / size.width).coerceIn(0f, 1f))
                        change.consume()
                    }
                }
            }
    ) {
        val radius = 8.dp.toPx()
        val trackHeight = 12.dp.toPx()
        val trackRect = androidx.compose.ui.geometry.Rect(
            Offset(0f, (size.height - trackHeight) / 2),
            androidx.compose.ui.geometry.Size(size.width, trackHeight)
        )
        
        val gradient = Brush.horizontalGradient(
            colors = listOf(Color.Black, Color.fromHsv(hue, saturation, 1f))
        )
        
        drawRoundRect(
            brush = gradient,
            topLeft = trackRect.topLeft,
            size = trackRect.size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(trackHeight / 2)
        )

        // Selector
        val handleX = value * size.width
        drawCircle(
            color = Color.White,
            radius = radius,
            center = Offset(handleX, size.height / 2),
            style = Stroke(width = 2.dp.toPx())
        )
        drawCircle(
            color = Color.Black,
            radius = radius - 1.dp.toPx(),
            center = Offset(handleX, size.height / 2),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

@PreviewsDayNight
@Composable
internal fun ColorThemePickerDialogPreview() = ElementPreview {
    ColorThemePickerDialog(
        initialColorHex = "#22004D",
        onSubmit = {},
        onDismiss = {},
    )
}
