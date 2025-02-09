package com.michaelliu.twsestockinfo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.michaelliu.twsestockinfo.data.local.dao.StockInfoDao
import com.michaelliu.twsestockinfo.data.local.entity.StockInfoEntity

@Database(entities = [StockInfoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stockInfoDao(): StockInfoDao
}