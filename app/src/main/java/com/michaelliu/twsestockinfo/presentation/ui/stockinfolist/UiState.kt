package com.michaelliu.twsestockinfo.presentation.ui.stockinfolist

import com.michaelliu.twsestockinfo.utils.AppError

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val error: AppError) : UiState<Nothing>()
}