package com.michaelliu.twsestockinfo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_info")
data class StockInfoEntity(
    @PrimaryKey val code: String,
    val name: String,
    val openingPrice: Double?,
    val closingPrice: Double?,
    val highestPrice: Double?,
    val lowestPrice: Double?,
    val change: Double?,
    val monthlyAvgPrice: Double?,
    val tradeVolume: Long?,
    val tradeValue: Long?,
    val transaction: Long?,
    val peRatio: Double?,
    val dividendYield: Double?,
    val pbRatio: Double?
)
