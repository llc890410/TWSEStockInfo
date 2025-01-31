package com.michaelliu.twsestockinfo.domain.model

/**
 * 股票資訊
 *
 * @property code 股票代號
 * @property name 股票名稱
 * @property openingPrice 開盤價
 * @property highestPrice 最高價
 * @property lowestPrice 最低價
 * @property closingPrice 收盤價
 * @property monthlyAvgPrice 月平均價
 * @property change 漲跌價差
 * @property tradeVolume 成交股數
 * @property tradeValue 成交金額
 * @property transaction 成交筆數
 * @property peRatio 本益比
 * @property dividendYield 殖利率(%)
 * @property pbRatio 股價淨值比
 */
data class StockInfo(
    val code: String,
    val name: String,
    val openingPrice: Double?,
    val highestPrice: Double?,
    val lowestPrice: Double?,
    val closingPrice: Double?,
    val monthlyAvgPrice: Double?,
    val change: Double?,
    val tradeVolume: Long?,
    val tradeValue: Long?,
    val transaction: Long?,
    val peRatio: Double?,
    val dividendYield: Double?,
    val pbRatio: Double?
)
