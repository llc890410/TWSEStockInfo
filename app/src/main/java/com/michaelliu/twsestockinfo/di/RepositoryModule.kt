package com.michaelliu.twsestockinfo.di

import com.michaelliu.twsestockinfo.data.repository.StockRepositoryImpl
import com.michaelliu.twsestockinfo.data.repository.UserPreferencesRepositoryImpl
import com.michaelliu.twsestockinfo.domain.repository.StockRepository
import com.michaelliu.twsestockinfo.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        userPreferencesRepository: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}