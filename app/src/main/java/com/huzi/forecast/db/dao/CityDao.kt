package com.huzi.forecast.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.huzi.forecast.db.entity.CityList
import com.huzi.shared.db.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao : BaseDao<CityList>{

    @Query("Select * From CityList Order By isGPS DESC ")
    fun getAllList(): Flow<List<CityList>>

    @Query("Select * From CityList where isSelected=1 ")
    fun getSelectedCity(): Flow<CityList?>

    @Query("UPDATE CityList set isSelected=0")
    suspend fun clearSelection()

    @Query("UPDATE CityList set isSelected=1 where id=:id")
    suspend fun select(id: String)

    @Query("UPDATE CityList set isSelected=1 where isGPS=1")
    suspend fun selectGPSLocation()

    @Query("Select * From CityList where isGPS=1")
    suspend fun getGPSCity(): CityList?

    @Query("Select * From CityList where id=:id")
    suspend fun getCityById(id: String?): CityList?

}