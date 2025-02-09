package com.michaelliu.twsestockinfo.data.repository

import com.michaelliu.twsestockinfo.data.local.LocalDataSource
import com.michaelliu.twsestockinfo.data.remote.RemoteDataSource
import com.michaelliu.twsestockinfo.data.remote.dto.BwiBbuDto
import com.michaelliu.twsestockinfo.data.remote.dto.StockDayAvgDto
import com.michaelliu.twsestockinfo.data.remote.dto.StockDayDto
import com.michaelliu.twsestockinfo.data.mapper.StockInfoMapper
import com.michaelliu.twsestockinfo.data.mapper.toDomain
import com.michaelliu.twsestockinfo.data.mapper.toEntity
import com.michaelliu.twsestockinfo.domain.model.StockInfo
import com.michaelliu.twsestockinfo.domain.repository.StockRepository
import com.michaelliu.twsestockinfo.utils.AppError
import com.michaelliu.twsestockinfo.utils.AppResult
import com.michaelliu.twsestockinfo.utils.NetworkMonitor
import com.michaelliu.twsestockinfo.utils.NetworkStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val networkMonitor: NetworkMonitor
) : StockRepository {

    override suspend fun getStockInfoList(): AppResult<List<StockInfo>> =
        withContext(Dispatchers.IO) {
            supervisorScope {
                if (networkMonitor.getCurrentNetworkStatus() is NetworkStatus.Unavailable) {
                    val localList = localDataSource.getAllStocks()
                    return@supervisorScope if (localList.isNotEmpty()) {
                        AppResult.Success(localList.map { it.toDomain() })
                    } else {
                        AppResult.Failure(AppError.NoDataBothNetworkAndLocal)
                    }
                }

                val bwiBbuDeferred = async { remoteDataSource.fetchBwiBbuAll() }
                val stockDayAvgDeferred = async { remoteDataSource.fetchStockDayAvgAll() }
                val stockDayDeferred = async { remoteDataSource.fetchStockDayAll() }

                val bwiBbuResult = bwiBbuDeferred.await()
                val stockDayAvgResult = stockDayAvgDeferred.await()
                val stockDayResult = stockDayDeferred.await()

                val isAllFailed = listOf(bwiBbuResult, stockDayAvgResult, stockDayResult).all { it is AppResult.Failure }

                if (isAllFailed) {
                    val localList = localDataSource.getAllStocks()
                    return@supervisorScope if (localList.isNotEmpty()) {
                        AppResult.Success(localList.map { it.toDomain() })
                    } else {
                        AppResult.Failure(AppError.NoDataBothNetworkAndLocal)
                    }
                }

                val bwiBbuList = (bwiBbuResult as? AppResult.Success)?.data ?: emptyList()
                val stockDayAvgList = (stockDayAvgResult as? AppResult.Success)?.data ?: emptyList()
                val stockDayList = (stockDayResult as? AppResult.Success)?.data ?: emptyList()

                val stockInfoList = mergeAllData(bwiBbuList, stockDayAvgList, stockDayList)

                if (stockInfoList.isNotEmpty()) {
                    localDataSource.refreshStocks(stockInfoList.map { it.toEntity() })
                }

                AppResult.Success(stockInfoList)
            }
        }

    private fun mergeAllData(
        bwiBbuList: List<BwiBbuDto>,
        stockDayAvgList: List<StockDayAvgDto>,
        stockDayList: List<StockDayDto>
    ): List<StockInfo> {
        // 轉成以 code 為鍵的 map
        val bwiBbuMap = bwiBbuList.associateBy { it.code }
        val stockDayAvgMap = stockDayAvgList.associateBy { it.code }
        val stockDayMap = stockDayList.associateBy { it.code }

        val allCodes = (bwiBbuMap.keys + stockDayAvgMap.keys + stockDayMap.keys).toSet()

        return allCodes.map { code ->
            val bwiBbu = bwiBbuMap[code]
            val stockDayAvg = stockDayAvgMap[code]
            val stockDay = stockDayMap[code]
            StockInfoMapper.mergeToStockInfo(bwiBbu, stockDayAvg, stockDay)
        }
    }
}