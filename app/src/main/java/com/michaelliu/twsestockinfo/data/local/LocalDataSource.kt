package com.michaelliu.twsestockinfo.data.local

import com.michaelliu.twsestockinfo.data.local.dao.StockInfoDao
import com.michaelliu.twsestockinfo.data.local.entity.StockInfoEntity
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val stockInfoDao: StockInfoDao
) {
    suspend fun saveStocks(stocks: List<StockInfoEntity>) = stockInfoDao.insertStocks(stocks)

    suspend fun getAllStocks(): List<StockInfoEntity> = stockInfoDao.getAllStocks()

    suspend fun deleteAllStocks() = stockInfoDao.deleteStocks()
}