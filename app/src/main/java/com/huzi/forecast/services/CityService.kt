package com.huzi.forecast.services

import com.huzi.forecast.models.SearchCityDTO
import com.huzi.shared.result.ApiResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface CityService {

    @GET("city/find")
    fun searchCity(@Query("name") name : String?): Flow<ApiResponse<List<SearchCityDTO>>>
}