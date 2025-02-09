package com.michaelliu.twsestockinfo.data.remote

import com.michaelliu.twsestockinfo.data.remote.api.StockApiService
import com.michaelliu.twsestockinfo.data.remote.dto.BwiBbuDto
import com.michaelliu.twsestockinfo.data.remote.dto.StockDayAvgDto
import com.michaelliu.twsestockinfo.data.remote.dto.StockDayDto
import com.michaelliu.twsestockinfo.utils.AppResult
import com.michaelliu.twsestockinfo.utils.safeApiCallWithRetry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(
    private val apiService: StockApiService
) {
    suspend fun fetchBwiBbuAll(): AppResult<List<BwiBbuDto>> {
        return safeApiCallWithRetry { apiService.getBwiBbuList() }
    }

    suspend fun fetchStockDayAvgAll(): AppResult<List<StockDayAvgDto>> {
        return safeApiCallWithRetry { apiService.getStockDayAvgList() }
    }

    suspend fun fetchStockDayAll(): AppResult<List<StockDayDto>> {
        return safeApiCallWithRetry { apiService.getStockDayList() }
    }
}