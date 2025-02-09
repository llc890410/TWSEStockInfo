package com.michaelliu.twsestockinfo.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.michaelliu.twsestockinfo.domain.model.SortType
import com.michaelliu.twsestockinfo.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {

    companion object {
        private val SORT_TYPE_KEY = stringPreferencesKey("sort_type")
        private val DEFAULT_SORT_TYPE = SortType.BY_CODE_DESC
    }

    override val sortTypeFlow: Flow<SortType> = context.dataStore.data
        .map { preferences ->
            preferences[SORT_TYPE_KEY]?.let { SortType.valueOf(it) } ?: DEFAULT_SORT_TYPE
        }

    override suspend fun saveSortType(sortType: SortType) {
        context.dataStore.edit { preferences ->
            preferences[SORT_TYPE_KEY] = sortType.name
        }
    }
}