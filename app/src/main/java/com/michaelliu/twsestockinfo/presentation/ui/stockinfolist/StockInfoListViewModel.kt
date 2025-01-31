package com.michaelliu.twsestockinfo.presentation.ui.stockinfolist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michaelliu.twsestockinfo.domain.model.StockInfo
import com.michaelliu.twsestockinfo.domain.usecase.GetStockInfoListUseCase
import com.michaelliu.twsestockinfo.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockInfoListViewModel @Inject constructor(
    private val getStockInfoListUseCase: GetStockInfoListUseCase
) : ViewModel() {
    private val _stockState = MutableLiveData<NetworkResult<List<StockInfo>>>()
    val stockState: LiveData<NetworkResult<List<StockInfo>>> = _stockState

    fun fetchStockInfoList() {
        viewModelScope.launch {
            val result = getStockInfoListUseCase()
            _stockState.value = result
        }
    }
}