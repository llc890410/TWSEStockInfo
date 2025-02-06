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
import com.michaelliu.twsestockinfo.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StockRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : StockRepository {

    override suspend fun getStockInfoList(): NetworkResult<List<StockInfo>> =
        withContext(Dispatchers.IO) {
            supervisorScope {
                val bwiBbuDeferred = async { remoteDataSource.fetchBwiBbuAll() }
                val stockDayAvgDeferred = async { remoteDataSource.fetchStockDayAvgAll() }
                val stockDayDeferred = async { remoteDataSource.fetchStockDayAll() }

                val bwiBbuResult = bwiBbuDeferred.await()
                val stockDayAvgResult = stockDayAvgDeferred.await()
                val stockDayResult = stockDayDeferred.await()

                val isAllFailed = listOf(bwiBbuResult, stockDayAvgResult, stockDayResult).all { it is NetworkResult.Failure }

                if (isAllFailed) {
                    val localList = localDataSource.getAllStocks()
                    return@supervisorScope if (localList.isNotEmpty()) {
                        NetworkResult.Success(localList.map { it.toDomain() })
                    } else {
                        NetworkResult.Failure(message = "All Network Requests Failed and Local Data is Empty")
                    }
                }

                val bwiBbuList = (bwiBbuResult as? NetworkResult.Success)?.data ?: emptyList()
                val stockDayAvgList = (stockDayAvgResult as? NetworkResult.Success)?.data ?: emptyList()
                val stockDayList = (stockDayResult as? NetworkResult.Success)?.data ?: emptyList()

                val stockInfoList = mergeAllData(bwiBbuList, stockDayAvgList, stockDayList)

                if (stockInfoList.isNotEmpty()) {
                    localDataSource.deleteAllStocks()
                    localDataSource.saveStocks(stockInfoList.map { it.toEntity() })
                }

                NetworkResult.Success(stockInfoList)
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