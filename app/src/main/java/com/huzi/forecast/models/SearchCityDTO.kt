package com.huzi.forecast.models

import com.huzi.forecast.db.entity.CityList

data class SearchCityDTO(
    val id: Int,
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double
)

fun SearchCityDTO.toCityList()=
    CityList(
        id= this.id,
        name = this.name,
        isSelected = true,
        isGPS = false
    )