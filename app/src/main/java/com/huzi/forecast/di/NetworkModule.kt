package com.huzi.forecast.di

import com.huzi.forecast.services.CityService
import com.huzi.forecast.services.WeatherService
import com.huzi.shared.di.NetworkModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideWeatherService(@Named(NetworkModule.WEATHER)  retrofit: Retrofit): WeatherService = retrofit.create()

    @Provides
    @Singleton
    fun provideCityService(@Named(NetworkModule.CITY)  retrofit: Retrofit): CityService = retrofit.create()
}