package com.michaelliu.twsestockinfo.data.remote.api

import com.michaelliu.twsestockinfo.data.remote.dto.BwiBbuDto
import com.michaelliu.twsestockinfo.data.remote.dto.StockDayAvgDto
import com.michaelliu.twsestockinfo.data.remote.dto.StockDayDto
import retrofit2.http.GET

interface StockApiService {
    @GET("/v1/exchangeReport/BWIBBU_ALL")
    suspend fun getBwiBbuList(): List<BwiBbuDto>

    @GET("/v1/exchangeReport/STOCK_DAY_AVG_ALL")
    suspend fun getStockDayAvgList(): List<StockDayAvgDto>

    @GET("/v1/exchangeReport/STOCK_DAY_ALL")
    suspend fun getStockDayList(): List<StockDayDto>
}