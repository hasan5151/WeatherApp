package com.huzi.forecast.models

import com.huzi.forecast.db.entity.CityList
import com.huzi.forecast.db.entity.CurrentWeather
import com.huzi.forecast.db.entity.HourlyWeather
import com.huzi.shared.extensions.toDate
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForecastDTO (val id: Int?,val main: MainDTO, val weather: List<WeatherDTO>,val name: String?,val dt_txt: String?)

fun ForecastDTO.toCurrentWeather() : CurrentWeather =
    CurrentWeather(
        id= this.id?: -1,
        temp = main.temp,
        tempMax = main.tempMax,
        tempMin = main.tempMin,
        icon = weather.first().icon,
        feelsLike = main.feelsLike
    )

fun ForecastDTO.toCity(hasGPS : Boolean): CityList =
     CityList(
         id = this.id?: -1,
         name = this.name?: "",
         isSelected = true,
         isGPS = hasGPS
     )

fun ForecastDTO.toHourWeather() : HourlyWeather =
    HourlyWeather(
        id = this.weather.first().id,
        temp = this.main.temp,
        tempMax = this.main.tempMax,
        tempMin = this.main.tempMin,
        icon =  this.weather.first().icon,
        time = this.dt_txt?.toDate() ?: "",
        feelsLike = this.main.feelsLike
    )