package com.huzi.forecast.services

import com.huzi.forecast.models.ForecastDTO
import com.huzi.forecast.models.ListForecastDTO
import com.huzi.shared.result.ApiResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("data/2.5/forecast")
    fun getHourly(@Query("id") id : String?): Flow<ApiResponse<ListForecastDTO>>

    @GET("data/2.5/forecast")
    fun getHourlyByLocation(@Query("lat") lat : Double?,@Query("lon") lon : Double?): Flow<ApiResponse<ListForecastDTO>>

    @GET("data/2.5/weather")
    fun getCurrent(@Query("id") id : String?): Flow<ApiResponse<ForecastDTO>>

    @GET("data/2.5/weather")
    fun getCurrentByLocation(@Query("lat") lat : Double,@Query("lon") lon : Double): Flow<ApiResponse<ForecastDTO>>

}