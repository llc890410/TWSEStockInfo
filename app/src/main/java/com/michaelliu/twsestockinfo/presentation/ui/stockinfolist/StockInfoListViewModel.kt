package com.michaelliu.twsestockinfo.presentation.ui.stockinfolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michaelliu.twsestockinfo.domain.model.SortType
import com.michaelliu.twsestockinfo.domain.model.StockInfo
import com.michaelliu.twsestockinfo.domain.usecase.GetStockInfoListUseCase
import com.michaelliu.twsestockinfo.utils.NetworkMonitor
import com.michaelliu.twsestockinfo.utils.NetworkStatus
import com.michaelliu.twsestockinfo.utils.onFailure
import com.michaelliu.twsestockinfo.utils.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockInfoListViewModel @Inject constructor(
    networkMonitor: NetworkMonitor,
    private val getStockInfoListUseCase: GetStockInfoListUseCase
) : ViewModel() {

    val networkStatus = networkMonitor.networkStatus.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NetworkStatus.UnKnown
    )

    private val _uiState = MutableStateFlow<UiState<List<StockInfo>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<StockInfo>>> = _uiState

    private var currentSortType = SortType.BY_CODE_DESC

    fun loadStockInfoList() {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            getStockInfoListUseCase()
                .onSuccess { stockInfoList ->
                    val sortedList = sortList(stockInfoList, currentSortType)
                    _uiState.value = UiState.Success(sortedList)
                }
                .onFailure { appError ->
                    _uiState.value = UiState.Error(appError)
                }
        }
    }

    fun getCurrentSortType(): SortType {
        return currentSortType
    }

    fun sortStockList(sortType: SortType) {
        currentSortType = sortType
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            _uiState.value = UiState.Loading
            val sortedList = sortList(currentState.data, sortType)
            _uiState.value = UiState.Success(sortedList)
        }
    }

    private fun sortList(stockInfoList: List<StockInfo>, sortType: SortType): List<StockInfo> {
        return when (sortType) {
            SortType.BY_CODE_ASC -> stockInfoList.sortedBy { it.code }
            SortType.BY_CODE_DESC -> stockInfoList.sortedByDescending { it.code }
        }
    }
}