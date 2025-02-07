package com.michaelliu.twsestockinfo.domain.usecase

import com.michaelliu.twsestockinfo.domain.model.StockInfo
import com.michaelliu.twsestockinfo.domain.repository.StockRepository
import com.michaelliu.twsestockinfo.utils.AppResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetStockInfoListUseCase @Inject constructor(
    private val stockRepository: StockRepository
) {
    suspend operator fun invoke(): AppResult<List<StockInfo>> {
        return stockRepository.getStockInfoList()
    }
}