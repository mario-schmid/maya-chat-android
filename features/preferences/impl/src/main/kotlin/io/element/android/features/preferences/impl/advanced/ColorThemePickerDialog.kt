/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.advanced

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.theme.hsvSaturation
import io.element.android.compound.theme.parseColorOrNull
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.preferences.impl.R
import io.element.android.libraries.designsystem.components.dialogs.ListDialog
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.TextField
import io.element.android.libraries.ui.strings.CommonStrings
import kotlin.math.roundToInt

private val PRESET_COLORS = listOf(
    "#22004D", // Dark Purple (S = 100%, Font: White)
    "#004D1A", // Dark Green (S = 100%, Font: White)
    "#4D0000", // Dark Red (S = 100%, Font: White)
    "#004D4D", // Dark Teal (S = 100%, Font: White)
    "#000C4D", // Dark Blue (S = 100%, Font: White)
    "#4D004D", // Dark Pink (S = 100%, Font: White)
    "#BA84FF", // Light Purple (S = 48% < 50%, Font: Black)
    "#85FFAE", // Light Green (S = 48% < 50%, Font: Black)
    "#FF8484", // Light Red (S = 48% < 50%, Font: Black)
    "#86FFFF", // Light Teal (S = 48% < 50%, Font: Black)
    "#859CFF", // Light Blue (S = 48% < 50%, Font: Black)
    "#FF86FF", // Light Pink (S = 48% < 50%, Font: Black)
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorThemePickerDialog(
    initialColorHex: String,
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedHex by remember { mutableStateOf(initialColorHex) }
    var hexInput by remember { mutableStateOf(initialColorHex) }

    val currentColor = remember(selectedHex) {
        parseColorOrNull(selectedHex) ?: parseColorOrNull(initialColorHex) ?: Color(0xFF4D00B2)
    }

    val saturationPercent = (currentColor.hsvSaturation() * 100f).roundToInt()
    val isFontBlack = saturationPercent < 50
    val previewFontColor = if (isFontBlack) Color.Black else Color.White

    ListDialog(
        modifier = modifier,
        title = stringResource(R.string.screen_advanced_settings_choose_theme_color),
        submitText = stringResource(CommonStrings.action_save),
        cancelText = stringResource(CommonStrings.action_cancel),
        onSubmit = { onSubmit(selectedHex) },
        onDismissRequest = onDismiss,
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Preset color swatches
                Text(
                    text = stringResource(R.string.screen_advanced_settings_preset_colors),
                    style = ElementTheme.typography.fontBodyMdMedium,
                    color = ElementTheme.colors.textPrimary,
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    for (presetHex in PRESET_COLORS) {
                        val presetColor = parseColorOrNull(presetHex) ?: continue
                        val isSelected = selectedHex.equals(presetHex, ignoreCase = true)
                        val presetSaturation = (presetColor.hsvSaturation() * 100f).roundToInt()
                        val checkTint = if (presetSaturation < 50) Color.Black else Color.White

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(presetColor)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) ElementTheme.colors.borderFocused else ElementTheme.colors.borderInteractiveSecondary,
                                    shape = CircleShape,
                                )
                                .clickable {
                                    selectedHex = presetHex
                                    hexInput = presetHex
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = CompoundIcons.Check(),
                                    contentDescription = null,
                                    tint = checkTint,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    }
                }

                // Custom Hex input
                TextField(
                    value = hexInput,
                    onValueChange = { input ->
                        hexInput = input
                        val parsed = parseColorOrNull(input)
                        if (parsed != null) {
                            selectedHex = if (input.startsWith("#")) input else "#$input"
                        }
                    },
                    label = stringResource(R.string.screen_advanced_settings_hex_color),
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

@PreviewsDayNight
@Composable
internal fun ColorThemePickerDialogPreview() = ElementPreview {
    ColorThemePickerDialog(
        initialColorHex = "#22004D",
        onSubmit = {},
        onDismiss = {},
    )
}
