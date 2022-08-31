package com.huzi.forecast.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CityDTO(val id: Int, val name: String)