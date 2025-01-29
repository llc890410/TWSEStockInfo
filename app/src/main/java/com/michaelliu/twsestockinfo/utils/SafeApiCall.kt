package com.michaelliu.twsestockinfo.utils

import kotlinx.coroutines.delay

suspend fun <T> safeApiCallWithRetry(
    maxRetries: Int = 3,
    delayStrategy: DelayStrategy = DelayStrategy.Exponential,
    apiCall: suspend () -> T
): NetworkResult<T> {
    var attempt = 0
    var currentDelay = 1000L

    while (true) {
        try {
            val response = apiCall()
            return NetworkResult.Success(response)
        } catch (e: Exception) {
            attempt++
            if (attempt >= maxRetries) {
                return NetworkResult.Failure(e)
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
