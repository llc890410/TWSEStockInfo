package com.michaelliu.twsestockinfo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.michaelliu.twsestockinfo.data.local.entity.StockInfoEntity

@Dao
interface StockInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStocks(stocks: List<StockInfoEntity>)

    @Query("SELECT * FROM stock_info")
    suspend fun getAllStocks(): List<StockInfoEntity>

    @Query("DELETE FROM stock_info")
    suspend fun deleteStocks()

    @Transaction
    suspend fun refreshStocks(stocks: List<StockInfoEntity>) {
        deleteStocks()
        insertStocks(stocks)
    }
}