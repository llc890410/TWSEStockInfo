package com.michaelliu.twsestockinfo.domain.repository

import com.michaelliu.twsestockinfo.domain.model.SortType
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val sortTypeFlow: Flow<SortType>
    suspend fun saveSortType(sortType: SortType)
}