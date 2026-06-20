package com.vehicletelemetry.core.extensions

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import timber.log.Timber
import kotlin.math.min
import kotlin.math.pow

/**
 * retryWithBackoff — wraps any Flow to retry on error with exponential backoff.
 * Use for repository calls that may fail transiently (e.g. DB init race).
 */
fun <T> Flow<T>.retryWithBackoff(
    maxRetries:  Int    = 5,
    baseDelayMs: Long   = 1_000L,
    maxDelayMs:  Long   = 30_000L,
    factor:      Double = 2.0
): Flow<T> = retryWhen { cause, attempt ->
    if (attempt >= maxRetries) {
        Timber.e("Flow max retries exceeded: ${cause.message}")
        false
    } else {
        val d = min((baseDelayMs * factor.pow(attempt.toDouble())).toLong(), maxDelayMs)
        Timber.w("Flow retry #${attempt + 1} in ${d}ms: ${cause.message}")
        delay(d)
        true
    }
}

/**
 * catchWithLog — catch, log, and optionally emit a safe fallback.
 * Use when you want the stream to survive errors gracefully.
 */
fun <T> Flow<T>.catchWithLog(
    tag:      String = "Flow",
    fallback: T?     = null
): Flow<T?> = catch { e ->
    Timber.tag(tag).e(e, "Flow error: ${e.message}")
    if (fallback != null) emit(fallback)
}
