package com.michaelliu.twsestockinfo.data.remote

import com.michaelliu.twsestockinfo.data.remote.api.StockApiService
import com.michaelliu.twsestockinfo.data.remote.dto.BwiBbuDto
import com.michaelliu.twsestockinfo.data.remote.dto.StockDayAvgDto
import com.michaelliu.twsestockinfo.data.remote.dto.StockDayDto
import com.michaelliu.twsestockinfo.utils.NetworkResult
import com.michaelliu.twsestockinfo.utils.safeApiCallWithRetry
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiService: StockApiService
) {
    suspend fun fetchBwiBbuAll(): NetworkResult<List<BwiBbuDto>> {
        return safeApiCallWithRetry { apiService.getBwiBbuList() }
    }

    suspend fun fetchStockDayAvgAll(): NetworkResult<List<StockDayAvgDto>> {
        return safeApiCallWithRetry { apiService.getStockDayAvgList() }
    }

    suspend fun fetchStockDayAll(): NetworkResult<List<StockDayDto>> {
        return safeApiCallWithRetry { apiService.getStockDayList() }
    }
}