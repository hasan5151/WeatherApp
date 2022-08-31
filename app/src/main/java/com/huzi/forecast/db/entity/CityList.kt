package com.huzi.forecast.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CityList (@PrimaryKey var id: Int, var name: String?, val isSelected: Boolean, val isGPS  :Boolean)