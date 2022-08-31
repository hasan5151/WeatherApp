package com.huzi.forecast.di

import android.content.Context
import androidx.room.Room
import com.huzi.forecast.constants.ROOM_DB_NAME
import com.huzi.forecast.db.WeatherDatabase
import com.huzi.forecast.db.dao.CityDao
import com.huzi.forecast.db.dao.CurrentDao
import com.huzi.forecast.db.dao.HourlyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideWeatherDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return Room.databaseBuilder(context, WeatherDatabase::class.java, ROOM_DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideCityDao(database: WeatherDatabase): CityDao = database.cityDao()

    @Provides
    @Singleton
    fun provideCurrentDao(database: WeatherDatabase): CurrentDao = database.currentDao()

    @Provides
    @Singleton
    fun provideHourlyDao(database: WeatherDatabase): HourlyDao = database.hourlyDao()
}