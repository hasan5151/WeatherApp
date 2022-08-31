package com.huzi.forecast.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HourlyWeather(
    val id: Int,
    val temp: String,
    val tempMax: String,
    val tempMin: String,
    val icon: String,
    @PrimaryKey
    val time: String,
    val feelsLike : String
)