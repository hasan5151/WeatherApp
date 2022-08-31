package com.huzi.forecast.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MainDTO(
    val temp: String,
    @field:Json(name = "feels_like")
    val feelsLike: String,
    @field:Json(name = "temp_min")
    val tempMin: String,
    @field:Json(name = "temp_max")
    val tempMax: String
)
