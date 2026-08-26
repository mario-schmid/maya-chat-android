/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components.virtual

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.messages.impl.timeline.model.virtual.TimelineItemDaySeparatorModel
import io.element.android.features.messages.impl.timeline.model.virtual.TimelineItemDaySeparatorModelProvider
import io.element.android.libraries.dateformatter.api.MayaCalendarHelper
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text

@Composable
internal fun TimelineItemDaySeparatorView(
    model: TimelineItemDaySeparatorModel,
    modifier: Modifier = Modifier
) {
    val mayaDate = MayaCalendarHelper.getMayaDate(model.timestamp)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(all = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .semantics { heading() }
        ) {
            // Maya Badge equivalent
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy((-13).dp),
                    modifier = Modifier
                        .padding(end = 8.dp)
                ) {

                    Image(
                        painter = painterResource(id = mayaDate.toneResId),
                          contentDescription = mayaDate.toneName,
                          modifier = Modifier.size(56.dp),
                          colorFilter = ColorFilter.tint(ElementTheme.colors.textPrimary)
                    )
                    Image(
                        painter = painterResource(id = mayaDate.nahualResId),
                        contentDescription = mayaDate.nahualName,
                        modifier = Modifier.size(60.dp)
                    )
                }
                Text(
                    text = mayaDate.fullMayaText,
                    style = ElementTheme.typography.fontBodyMdMedium.copy(
                        color = ElementTheme.colors.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.offset(y = (16).dp)
                )
            }

            // Original Gregorian Date
            Text(
                text = model.formattedDate,
                style = ElementTheme.typography.fontBodyMdMedium,
                color = ElementTheme.colors.textPrimary,
                modifier = Modifier.offset(y = (16).dp)
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun TimelineItemDaySeparatorViewPreview(
    @PreviewParameter(TimelineItemDaySeparatorModelPreviewParam::class) model: TimelineItemDaySeparatorModel
) = ElementPreview {
    TimelineItemDaySeparatorView(
        model = model,
    )
}
