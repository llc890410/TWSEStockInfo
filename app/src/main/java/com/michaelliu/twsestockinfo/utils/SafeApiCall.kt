package com.michaelliu.twsestockinfo.utils

import kotlinx.coroutines.delay
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

suspend fun <T> safeApiCallWithRetry(
    maxRetries: Int = 3,
    delayStrategy: DelayStrategy = DelayStrategy.Exponential,
    apiCall: suspend () -> T
): AppResult<T> {
    var attempt = 0
    var currentDelay = 1000L

    while (true) {
        try {
            val response = apiCall()
            return AppResult.Success(response)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Timber.e(e)

            attempt++
            if (attempt >= maxRetries) {
                return AppResult.Failure(AppError.MaxRetryExceeded)
            }

            currentDelay = computeDelay(attempt, currentDelay, delayStrategy)
            delay(currentDelay)
        }
    }
}

private fun computeDelay(
    attempt: Int,
    currentDelay: Long,
    strategy: DelayStrategy
): Long {
    return when (strategy) {
        is DelayStrategy.Exponential -> currentDelay * 2
        is DelayStrategy.Fixed -> strategy.delayMillis
        is DelayStrategy.Custom -> strategy.block(attempt, currentDelay)
    }
}
