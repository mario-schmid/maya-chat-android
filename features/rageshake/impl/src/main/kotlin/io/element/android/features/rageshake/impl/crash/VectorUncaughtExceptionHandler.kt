/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.crash

import android.content.Context
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.core.data.tryOrNull
import io.element.android.libraries.core.meta.BuildMeta
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.PrintWriter
import java.io.StringWriter

class VectorUncaughtExceptionHandler(
    private val context: Context,
    private val buildMeta: BuildMeta,
    private val dispatchers: CoroutineDispatchers,
    private val crashDataStore: CrashDataStore,
) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        Timber.tag("Crash").e(throwable, "Uncaught exception in thread ${thread.name}")

        val appName = "MayaChat"
        val appVersion = buildMeta.versionName
        val builder = StringBuilder()
        builder.append("App: $appName\n")
        builder.append("Version: $appVersion\n")
        builder.append("OS: Android ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})\n")
        builder.append("Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}\n")
        builder.append("Thread: ${thread.name}\n\n")

        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        builder.append(sw.toString())

        val crashDetail = builder.toString()

        runBlocking(dispatchers.io) {
            tryOrNull {
                crashDataStore.setCrashData(crashDetail)
            }
        }

        defaultHandler?.uncaughtException(thread, throwable)
    }
}
