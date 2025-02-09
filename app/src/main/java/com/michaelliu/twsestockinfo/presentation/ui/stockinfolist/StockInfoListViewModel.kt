package com.michaelliu.twsestockinfo.presentation.ui.stockinfolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michaelliu.twsestockinfo.domain.model.SortType
import com.michaelliu.twsestockinfo.domain.model.StockInfo
import com.michaelliu.twsestockinfo.domain.repository.UserPreferencesRepository
import com.michaelliu.twsestockinfo.domain.usecase.GetStockInfoListUseCase
import com.michaelliu.twsestockinfo.utils.NetworkMonitor
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
    private val getStockInfoListUseCase: GetStockInfoListUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val networkStatus = networkMonitor.networkStatus.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = networkMonitor.getCurrentNetworkStatus()
    )

    private val _uiState = MutableStateFlow<UiState<List<StockInfo>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<StockInfo>>> = _uiState

    val currentSortType: StateFlow<SortType> = userPreferencesRepository.sortTypeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = SortType.BY_CODE_DESC
    )

    init {
        observeSortType()
    }

    private fun observeSortType() {
        viewModelScope.launch {
            currentSortType.collect { sortType ->
                val currentState = _uiState.value
                if (currentState is UiState.Success) {
                    _uiState.value = UiState.Success(currentState.data.sortedByType(sortType))
                }
            }
        }
    }

    fun loadStockInfoList(forceRefresh: Boolean = false) {
        if (!forceRefresh && _uiState.value is UiState.Success) {
            return
        }

        _uiState.value = UiState.Loading

        viewModelScope.launch {
            getStockInfoListUseCase()
                .onSuccess { stockInfoList ->
                    _uiState.value = UiState.Success(stockInfoList.sortedByType(currentSortType.value))
                }
                .onFailure { appError ->
                    _uiState.value = UiState.Error(appError)
                }
        }
    }

    fun sortStockList(sortType: SortType) {
        viewModelScope.launch {
            userPreferencesRepository.saveSortType(sortType)
        }
    }

    private fun List<StockInfo>.sortedByType(sortType: SortType): List<StockInfo> {
        return when (sortType) {
            SortType.BY_CODE_ASC -> sortedBy { it.code }
            SortType.BY_CODE_DESC -> sortedByDescending { it.code }
        }
    }
}