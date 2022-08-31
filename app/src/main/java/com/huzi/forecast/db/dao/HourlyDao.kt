package com.huzi.forecast.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.huzi.forecast.db.entity.HourlyWeather
import com.huzi.shared.db.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface HourlyDao: BaseDao<HourlyWeather> {
    @Query("Delete From HourlyWeather ")
    suspend fun deleteAll()

    @Query("Select * From HourlyWeather")
    fun getAll() : Flow<List<HourlyWeather>>
}