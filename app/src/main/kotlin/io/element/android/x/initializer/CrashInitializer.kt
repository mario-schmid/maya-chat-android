/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.x.initializer

import android.content.Context
import androidx.startup.Initializer
import io.element.android.features.rageshake.impl.crash.VectorUncaughtExceptionHandler
import io.element.android.features.rageshake.impl.di.RageshakeBindings
import io.element.android.libraries.architecture.bindings
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.x.di.AppBindings

class CrashInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        val appBindings = context.bindings<AppBindings>()
        val rageshakeBindings = context.bindings<RageshakeBindings>()
        
        // CoroutineDispatchers is currently provided by AppModule but not exposed in AppBindings.
        // For now, we'll use the default since it's what AppModule provides anyway.
        val dispatchers = CoroutineDispatchers.Default
        
        VectorUncaughtExceptionHandler(
            context = context,
            buildMeta = appBindings.buildMeta(),
            dispatchers = dispatchers,
            crashDataStore = rageshakeBindings.preferencesCrashDataStore(),
        )
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
