package com.michaelliu.twsestockinfo.data.remote.mapper

import com.michaelliu.twsestockinfo.data.remote.dto.BwiBbuDto
import com.michaelliu.twsestockinfo.data.remote.dto.StockDayAvgDto
import com.michaelliu.twsestockinfo.data.remote.dto.StockDayDto
import com.michaelliu.twsestockinfo.domain.model.StockInfo

object StockInfoMapper {
    fun mergeToStockInfo(
        bwiBbu: BwiBbuDto?,
        stockDayAvg: StockDayAvgDto?,
        stockDay: StockDayDto?
    ): StockInfo {
        return StockInfo(
            code = bwiBbu?.code ?: stockDay?.code ?: stockDayAvg?.code ?: "",
            name = bwiBbu?.name ?: stockDay?.name ?: stockDayAvg?.name ?: "",
            openingPrice = stockDay?.openingPrice?.toDoubleOrNull(),
            highestPrice = stockDay?.highestPrice?.toDoubleOrNull(),
            lowestPrice = stockDay?.lowestPrice?.toDoubleOrNull(),
            closingPrice = stockDay?.closingPrice?.toDoubleOrNull(),
            monthlyAvgPrice = stockDayAvg?.monthlyAvgPrice?.toDoubleOrNull(),
            change = stockDay?.change?.toDoubleOrNull(),
            tradeVolume = stockDay?.tradeVolume?.toLongOrNull(),
            tradeValue = stockDay?.tradeValue?.toLongOrNull(),
            transaction = stockDay?.transaction?.toLongOrNull(),
            peRatio = bwiBbu?.peRatio?.toDoubleOrNull(),
            dividendYield = bwiBbu?.dividendYield?.toDoubleOrNull(),
            pbRatio = bwiBbu?.pbRatio?.toDoubleOrNull()
        )
    }
}