package com.michaelliu.twsestockinfo.presentation.ui.stockinfolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michaelliu.twsestockinfo.domain.model.StockInfo
import com.michaelliu.twsestockinfo.domain.usecase.GetStockInfoListUseCase
import com.michaelliu.twsestockinfo.utils.onFailure
import com.michaelliu.twsestockinfo.utils.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockInfoListViewModel @Inject constructor(
    private val getStockInfoListUseCase: GetStockInfoListUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<StockInfo>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<StockInfo>>> = _uiState

    fun loadStockInfoList() {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            getStockInfoListUseCase()
                .onSuccess { data ->
                    _uiState.value = UiState.Success(data)
                }
                .onFailure { message, exception ->
                    val errorMessage = message ?: exception?.message ?: "Unknown error"
                    _uiState.value = UiState.Error(errorMessage)
                }
        }
    }
}