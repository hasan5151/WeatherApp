package com.huzi.forecast.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.huzi.forecast.db.dao.CityDao
import com.huzi.forecast.db.dao.CurrentDao
import com.huzi.forecast.db.dao.HourlyDao
import com.huzi.forecast.db.entity.CityList
import com.huzi.forecast.db.entity.CurrentWeather
import com.huzi.forecast.db.entity.HourlyWeather

@Database(entities = [CityList::class,CurrentWeather::class,HourlyWeather::class],
          version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun currentDao(): CurrentDao
    abstract fun hourlyDao(): HourlyDao
}