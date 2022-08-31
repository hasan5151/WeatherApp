package com.huzi.forecast.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherDTO (val id: Int,val main :String,val description: String, val icon: String)
