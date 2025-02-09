package com.michaelliu.twsestockinfo.domain.repository

import com.michaelliu.twsestockinfo.domain.model.StockInfo
import com.michaelliu.twsestockinfo.utils.AppResult

interface StockRepository {
    suspend fun getStockInfoList(): AppResult<List<StockInfo>>
}