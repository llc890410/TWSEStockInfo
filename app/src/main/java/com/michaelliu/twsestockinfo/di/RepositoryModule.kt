package com.michaelliu.twsestockinfo.di

import com.michaelliu.twsestockinfo.data.local.LocalDataSource
import com.michaelliu.twsestockinfo.data.local.dao.StockInfoDao
import com.michaelliu.twsestockinfo.data.remote.RemoteDataSource
import com.michaelliu.twsestockinfo.data.remote.api.StockApiService
import com.michaelliu.twsestockinfo.data.repository.StockRepositoryImpl
import com.michaelliu.twsestockinfo.domain.repository.StockRepository
import com.michaelliu.twsestockinfo.domain.usecase.GetStockInfoListUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        stockApiService: StockApiService
    ): RemoteDataSource {
        return RemoteDataSource(stockApiService)
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(
        stockInfoDao: StockInfoDao
    ): LocalDataSource {
        return LocalDataSource(stockInfoDao)
    }

    @Provides
    @Singleton
    fun provideStockRepository(
        remoteDataSource: RemoteDataSource
    ): StockRepository {
        return StockRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideGetStockInfoListUseCase(
        repository: StockRepository
    ): GetStockInfoListUseCase {
        return GetStockInfoListUseCase(repository)
    }
}
