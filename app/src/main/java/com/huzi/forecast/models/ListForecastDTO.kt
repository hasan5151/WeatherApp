package com.huzi.forecast.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ListForecastDTO(val cod : String, val list: List<ForecastDTO>, val city: CityDTO)
