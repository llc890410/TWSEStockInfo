package com.michaelliu.twsestockinfo.utils

sealed interface NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>
    data class Failure(
        val exception: Exception? = null,
        val message: String? = null
    ) : NetworkResult<Nothing>
}


inline fun <T> NetworkResult<T>.onSuccess(action: (T) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Success) {
        action(data)
    }
    return this
}

inline fun <T> NetworkResult<T>.onFailure(action: (String?, Exception?) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Failure) {
        action(message, exception)
    }
    return this
}
