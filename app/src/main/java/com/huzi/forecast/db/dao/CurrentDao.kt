package com.huzi.forecast.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.huzi.forecast.db.entity.CurrentWeather
import com.huzi.shared.db.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentDao : BaseDao<CurrentWeather>{
    @Query("Delete From CurrentWeather")
    suspend fun deleteALl()

    @Query("Select * From CurrentWeather")
    fun getCurrent() : Flow<CurrentWeather>
}