package com.michaelliu.twsestockinfo.utils

sealed class AppError {
    data object NoDataBothNetworkAndLocal : AppError()
    data object MaxRetryExceeded : AppError()
    data class Unknown(val throwable: Throwable?) : AppError()
}
