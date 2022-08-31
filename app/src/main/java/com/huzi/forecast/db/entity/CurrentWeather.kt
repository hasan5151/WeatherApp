package com.huzi.forecast.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrentWeather(
    @PrimaryKey
    val id: Int,
    val temp: String,
    val tempMax: String,
    val tempMin: String,
    val icon: String,
    val feelsLike: String,
)