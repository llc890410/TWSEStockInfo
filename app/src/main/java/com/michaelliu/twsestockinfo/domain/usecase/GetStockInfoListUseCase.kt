package com.michaelliu.twsestockinfo.domain.usecase

import com.michaelliu.twsestockinfo.domain.model.StockInfo
import com.michaelliu.twsestockinfo.domain.repository.StockRepository
import com.michaelliu.twsestockinfo.utils.NetworkResult
import javax.inject.Inject

class GetStockInfoListUseCase @Inject constructor(
    private val stockRepository: StockRepository
) {
    suspend operator fun invoke(): NetworkResult<List<StockInfo>> {
        return stockRepository.getStockInfoList()
    }
}