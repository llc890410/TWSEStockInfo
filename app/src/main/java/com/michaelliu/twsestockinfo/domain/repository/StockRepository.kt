package com.michaelliu.twsestockinfo.domain.repository

import com.michaelliu.twsestockinfo.domain.model.StockInfo
import com.michaelliu.twsestockinfo.utils.NetworkResult

interface StockRepository {
    suspend fun getStockInfoList(): NetworkResult<List<StockInfo>>
}