package com.michaelliu.twsestockinfo.data.mapper

import com.michaelliu.twsestockinfo.data.local.entity.StockInfoEntity
import com.michaelliu.twsestockinfo.domain.model.StockInfo

fun StockInfoEntity.toDomain(): StockInfo = StockInfo(
    code = code,
    name = name,
    openingPrice = openingPrice,
    closingPrice = closingPrice,
    highestPrice = highestPrice,
    lowestPrice = lowestPrice,
    change = change,
    monthlyAvgPrice = monthlyAvgPrice,
    tradeVolume = tradeVolume,
    tradeValue = tradeValue,
    transaction = transaction,
    peRatio = peRatio,
    dividendYield = dividendYield,
    pbRatio = pbRatio
)

fun StockInfo.toEntity(): StockInfoEntity = StockInfoEntity(
    code = code,
    name = name,
    openingPrice = openingPrice,
    closingPrice = closingPrice,
    highestPrice = highestPrice,
    lowestPrice = lowestPrice,
    change = change,
    monthlyAvgPrice = monthlyAvgPrice,
    tradeVolume = tradeVolume,
    tradeValue = tradeValue,
    transaction = transaction,
    peRatio = peRatio,
    dividendYield = dividendYield,
    pbRatio = pbRatio
)
