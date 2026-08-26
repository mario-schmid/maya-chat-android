/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl

import io.element.android.compound.theme.Theme

data class ChatBackgroundImageState(
    val isEnabled: Boolean,
    val uri: String?,
    val theme: Theme,
)
